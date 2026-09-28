// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chisel3.util._
import scabook.memory.{L1Cache, CacheCpuReq, CacheCpuResp, CacheMemReq, CacheMemResp, RiscvRegFile}
import scabook.multipliers.{PipelinedMultiplierStage1, PipelinedMultiplierStage2}

/** RISC-V RV32I_Zmmul 4-Stage Pipelined Processor Core with L1 Instruction and Data Caches.
  *
  * Integrates:
  *   - Balanced Carry-Select Adder (CSelA) ALU
  *   - 2-Stage Pipelined Multiplier (Pipemul) with Booth Recoding & 6-level CSA Tree
  *   - Full distance-1 and distance-2 data bypassing
  *   - Dynamic Gshare branch predictor (256-entry PHT, 8-bit GHR, 32-entry BTB)
  *   - Parameterized Level-1 Instruction Cache (I-Cache)
  *   - Parameterized Level-1 Data Cache (D-Cache)
  *   - Memory bus interfaces for main memory (e.g. DDR-5) / L2 Cache refill and writeback
  */
class RiscvCachedPipelined(
  val xlen: Int = 32,
  val initPC: BigInt = 0x80000000L,
  val enableZmmul: Boolean = true,
  val enableForwarding: Boolean = true,
  val enablePipelinedMul: Boolean = true,
  val branchPredictor: String = "gshare",
  val icacheBytes: Int = 4096,
  val icacheWays: Int = 2,
  val dcacheBytes: Int = 4096,
  val dcacheWays: Int = 2,
  val lineBytes: Int = 32,
  val enableIcache: Boolean = true,
  val enableDcache: Boolean = true
) extends Module {
  val io = IO(new Bundle {
    // External Memory Ports for Cache Refill and Writeback
    val imemRefill = if (enableIcache) Some(Decoupled(new CacheMemReq(xlen, lineBytes))) else None
    val imemResp   = if (enableIcache) Some(Flipped(Valid(new CacheMemResp(lineBytes)))) else None

    val dmemRefill = if (enableDcache) Some(Decoupled(new CacheMemReq(xlen, lineBytes))) else None
    val dmemResp   = if (enableDcache) Some(Flipped(Valid(new CacheMemResp(lineBytes)))) else None

    // Fallback unbuffered memory ports when caches are disabled (for isolated profiling)
    val rawImem = if (!enableIcache) Some(new ImemPort(xlen, xlen)) else None
    val rawDmem = if (!enableDcache) Some(new DmemPort(xlen, xlen)) else None

    // Architectural Profiling Signals
    val pc              = Output(UInt(xlen.W))
    val inst            = Output(UInt(xlen.W))
    val instRetired     = Output(Bool())
    val isStall         = Output(Bool())
    val isLoadUseStall  = Output(Bool())
    val isIcacheStall   = Output(Bool())
    val isDcacheStall   = Output(Bool())
    val icacheHit       = Output(Bool())
    val icacheMiss      = Output(Bool())
    val dcacheHit       = Output(Bool())
    val dcacheMiss      = Output(Bool())
  })

  // ==========================================
  // HARDWARE AUTOMATA & SUBSYSTEMS
  // ==========================================
  val decoder   = Module(new RiscvDecoder())
  val regFile   = Module(new RiscvRegFile(xlen))
  val alu       = Module(new RiscvALU(xlen, enableZmmul = !enablePipelinedMul))
  val predictor = Module(new BranchPredictor(xlen, predictorType = branchPredictor))

  // Pipelined Multiplier Sub-stages (EX & MEM/WB)
  val mulStage1 = if (enableZmmul && enablePipelinedMul) Some(Module(new PipelinedMultiplierStage1(xlen))) else None
  val mulStage2 = if (enableZmmul && enablePipelinedMul) Some(Module(new PipelinedMultiplierStage2(xlen))) else None

  // L1 Caches
  val icache = if (enableIcache) Some(Module(new L1Cache(icacheBytes, icacheWays, lineBytes, isDcache = false, xlen))) else None
  val dcache = if (enableDcache) Some(Module(new L1Cache(dcacheBytes, dcacheWays, lineBytes, isDcache = true, xlen))) else None

  // Pipeline Registers
  val pcReg  = RegInit(initPC.U(xlen.W))
  val if_id  = RegInit(0.U.asTypeOf(new IfIdBundle(xlen)))
  val id_ex  = RegInit(0.U.asTypeOf(new IdExBundle(xlen)))
  val ex_mem = RegInit(0.U.asTypeOf(new ExMemBundle(xlen)))

  // Cache Stalls
  val icacheStall = if (enableIcache) icache.get.io.stall else false.B
  val dcacheStall = if (enableDcache) dcache.get.io.stall else false.B

  io.isIcacheStall := icacheStall
  io.isDcacheStall := dcacheStall
  io.icacheHit     := (if (enableIcache) icache.get.io.perfHit else true.B)
  io.icacheMiss    := (if (enableIcache) icache.get.io.perfMiss else false.B)
  io.dcacheHit     := (if (enableDcache) dcache.get.io.perfHit else true.B)
  io.dcacheMiss    := (if (enableDcache) dcache.get.io.perfMiss else false.B)

  // Route External Memory interfaces
  if (enableIcache) {
    io.imemRefill.get <> icache.get.io.memReq
    icache.get.io.memResp := io.imemResp.get
  }
  if (enableDcache) {
    io.dmemRefill.get <> dcache.get.io.memReq
    dcache.get.io.memResp := io.dmemResp.get
  }

  // ==========================================
  // STAGE 4: MEMORY ACCESS & WRITE-BACK (MEM/WB)
  // ==========================================
  val dmemReadData = WireDefault(0.U(xlen.W))

  if (enableDcache) {
    val dc = dcache.get
    dc.io.req.valid          := ex_mem.valid && (ex_mem.memRead || ex_mem.memWrite)
    dc.io.req.bits.addr      := ex_mem.aluResult
    dc.io.req.bits.funct3    := ex_mem.funct3
    dc.io.req.bits.isWrite   := ex_mem.memWrite
    dc.io.req.bits.writeData := ex_mem.rs2Data
    dmemReadData             := dc.io.resp.bits.readData
  } else {
    val rd = io.rawDmem.get
    rd.addr      := ex_mem.aluResult
    rd.funct3    := ex_mem.funct3
    rd.memRead   := ex_mem.valid && ex_mem.memRead
    rd.memWrite  := ex_mem.valid && ex_mem.memWrite
    rd.writeData := ex_mem.rs2Data
    dmemReadData := rd.readData
  }

  // Stage 2 Multiplier
  val mulStage2Result = WireDefault(0.U(xlen.W))
  if (mulStage2.isDefined) {
    val ms2 = mulStage2.get
    ms2.io.sumIn    := ex_mem.mulSum
    ms2.io.carryIn  := ex_mem.mulCarry
    ms2.io.mulOp    := ex_mem.mulOp
    mulStage2Result := ms2.io.result
  }

  val wbData = MuxCase(ex_mem.aluResult, Seq(
    ex_mem.memToReg -> dmemReadData,
    ex_mem.isJal    -> (ex_mem.pc + 4.U),
    ex_mem.isJalr   -> (ex_mem.pc + 4.U),
    ex_mem.isLui    -> ex_mem.imm,
    ex_mem.isAuipc  -> (ex_mem.pc + ex_mem.imm),
    (ex_mem.isMul && enablePipelinedMul.B) -> mulStage2Result
  ))

  val wbWen = ex_mem.valid && ex_mem.regWrite && (ex_mem.rd =/= 0.U) && !dcacheStall
  val wbRd  = ex_mem.rd

  regFile.io.rd      := wbRd
  regFile.io.rd_data := wbData
  regFile.io.wen     := wbWen

  // Distance-1 Forwarding Data
  val exMemFwdData = MuxCase(ex_mem.aluResult, Seq(
    ex_mem.isJal    -> (ex_mem.pc + 4.U),
    ex_mem.isJalr   -> (ex_mem.pc + 4.U),
    ex_mem.isLui    -> ex_mem.imm,
    ex_mem.isAuipc  -> (ex_mem.pc + ex_mem.imm),
    (ex_mem.isMul && enablePipelinedMul.B) -> mulStage2Result
  ))

  // ==========================================
  // STAGE 3: EXECUTE (EX)
  // ==========================================
  val fwdA = WireDefault("b00".U(2.W))
  val fwdB = WireDefault("b00".U(2.W))

  if (enableForwarding) {
    when(ex_mem.valid && ex_mem.regWrite && (ex_mem.rd =/= 0.U) && (ex_mem.rd === id_ex.rs1)) {
      fwdA := "b10".U
    }.elsewhen(wbWen && (wbRd === id_ex.rs1)) {
      fwdA := "b01".U
    }

    when(ex_mem.valid && ex_mem.regWrite && (ex_mem.rd =/= 0.U) && (ex_mem.rd === id_ex.rs2)) {
      fwdB := "b10".U
    }.elsewhen(wbWen && (wbRd === id_ex.rs2)) {
      fwdB := "b01".U
    }
  }

  val exRs1 = MuxCase(id_ex.rs1Data, Seq(
    (fwdA === "b10".U) -> exMemFwdData,
    (fwdA === "b01".U) -> wbData
  ))

  val exRs2 = MuxCase(id_ex.rs2Data, Seq(
    (fwdB === "b10".U) -> exMemFwdData,
    (fwdB === "b01".U) -> wbData
  ))

  alu.io.opA := exRs1
  alu.io.opB := Mux(id_ex.aluSrc, id_ex.imm, exRs2)
  alu.io.aluOp := id_ex.aluOp

  if (mulStage1.isDefined) {
    mulStage1.get.io.a := exRs1
    mulStage1.get.io.b := Mux(id_ex.aluSrc, id_ex.imm, exRs2)
    mulStage1.get.io.mulOp := id_ex.aluOp
  }

  // Branch & Jump Resolution
  val branchCond = MuxCase(false.B, Seq(
    (id_ex.funct3 === "b000".U) -> alu.io.zero,
    (id_ex.funct3 === "b001".U) -> !alu.io.zero,
    (id_ex.funct3 === "b100".U) -> alu.io.lessThan,
    (id_ex.funct3 === "b101".U) -> !alu.io.lessThan,
    (id_ex.funct3 === "b110".U) -> alu.io.lessThanU,
    (id_ex.funct3 === "b111".U) -> !alu.io.lessThanU
  ))

  val branchTaken  = id_ex.valid && id_ex.branch && branchCond
  val branchTarget = id_ex.pc + id_ex.imm
  val jalTarget    = id_ex.pc + id_ex.imm
  val jalrTarget   = Cat((exRs1 + id_ex.imm)(xlen - 1, 1), 0.U(1.W))

  val isBranch = id_ex.valid && id_ex.branch
  val branchMispredicted = isBranch && (
    (branchTaken =/= id_ex.predTaken) ||
    (branchTaken && (id_ex.predTarget =/= branchTarget))
  )
  val branchRecoveryPC = Mux(branchTaken, branchTarget, id_ex.pc + 4.U)

  val isJal = id_ex.valid && id_ex.isJal
  val jalMispredicted = isJal && (!id_ex.predTaken || (id_ex.predTarget =/= jalTarget))
  val isJalr = id_ex.valid && id_ex.isJalr
  val jalrRedirect = isJalr

  val exRedirect = (branchMispredicted || jalMispredicted || jalrRedirect) && !dcacheStall
  val exTargetPC = Mux(jalrRedirect, jalrTarget,
                   Mux(jalMispredicted, jalTarget,
                       branchRecoveryPC))

  predictor.io.updateValid  := id_ex.valid && (id_ex.branch || id_ex.isJal) && !dcacheStall
  predictor.io.updateIsBr   := id_ex.branch
  predictor.io.updateIsJal  := id_ex.isJal
  predictor.io.updatePC     := id_ex.pc
  predictor.io.actualTaken  := Mux(id_ex.isJal, true.B, branchTaken)
  predictor.io.actualTarget := Mux(id_ex.isJal, jalTarget, branchTarget)
  predictor.io.prevHistory  := id_ex.predHistory

  // Pipeline register EX -> MEM/WB
  when(!dcacheStall) {
    ex_mem.valid     := id_ex.valid
    ex_mem.pc        := id_ex.pc
    ex_mem.inst      := id_ex.inst
    ex_mem.rd        := id_ex.rd
    ex_mem.funct3    := id_ex.funct3
    ex_mem.aluResult := alu.io.result
    ex_mem.rs2Data   := exRs2
    ex_mem.imm       := id_ex.imm
    ex_mem.regWrite  := id_ex.regWrite
    ex_mem.memRead   := id_ex.memRead
    ex_mem.memWrite  := id_ex.memWrite
    ex_mem.memToReg  := id_ex.memToReg
    ex_mem.isJal     := id_ex.isJal
    ex_mem.isJalr    := id_ex.isJalr
    ex_mem.isLui     := id_ex.isLui
    ex_mem.isAuipc   := id_ex.isAuipc
    ex_mem.isMul     := id_ex.valid && id_ex.isMul
    ex_mem.mulOp     := id_ex.aluOp
    ex_mem.mulSum    := (if (mulStage1.isDefined) mulStage1.get.io.sumOut else 0.U)
    ex_mem.mulCarry  := (if (mulStage1.isDefined) mulStage1.get.io.carryOut else 0.U)
  }

  // ==========================================
  // STAGE 2: INSTRUCTION DECODE (ID) & HAZARDS
  // ==========================================
  decoder.io.inst := if_id.inst
  val d = decoder.io.decoded
  val c = d.control
  val f = RiscvFields(if_id.inst)

  regFile.io.rs1 := d.rs1
  regFile.io.rs2 := d.rs2

  val wbBypassRs1 = enableForwarding.B && wbWen && (wbRd === d.rs1) && (d.rs1 =/= 0.U)
  val wbBypassRs2 = enableForwarding.B && wbWen && (wbRd === d.rs2) && (d.rs2 =/= 0.U)
  val rs1Val = Mux(wbBypassRs1, wbData, regFile.io.rs1_data)
  val rs2Val = Mux(wbBypassRs2, wbData, regFile.io.rs2_data)

  val readsRs1 = (f.opcode === RiscvOpcodes.OP     ||
                  f.opcode === RiscvOpcodes.OP_IMM ||
                  f.opcode === RiscvOpcodes.LOAD   ||
                  f.opcode === RiscvOpcodes.STORE  ||
                  f.opcode === RiscvOpcodes.BRANCH ||
                  f.opcode === RiscvOpcodes.JALR) && (d.rs1 =/= 0.U)

  val readsRs2 = (f.opcode === RiscvOpcodes.OP     ||
                  f.opcode === RiscvOpcodes.STORE  ||
                  f.opcode === RiscvOpcodes.BRANCH) && (d.rs2 =/= 0.U)

  val isLoadUseHazard = id_ex.valid && id_ex.memRead && (id_ex.rd =/= 0.U) &&
                        ((readsRs1 && (id_ex.rd === d.rs1)) || (readsRs2 && (id_ex.rd === d.rs2)))

  val rawStall = if_id.valid && isLoadUseHazard
  io.isLoadUseStall := isLoadUseHazard

  when(dcacheStall) {
    // Freeze ID/EX during D-cache miss
    id_ex := id_ex
  }.elsewhen(exRedirect) {
    id_ex.valid := false.B
    id_ex.regWrite := false.B
    id_ex.memRead := false.B
    id_ex.memWrite := false.B
    id_ex.branch := false.B
    id_ex.jump := false.B
    id_ex.isJal := false.B
    id_ex.isJalr := false.B
    id_ex.isMul := false.B
  }.elsewhen(rawStall) {
    id_ex.valid := false.B
    id_ex.regWrite := false.B
    id_ex.memRead := false.B
    id_ex.memWrite := false.B
    id_ex.branch := false.B
    id_ex.jump := false.B
    id_ex.isJal := false.B
    id_ex.isJalr := false.B
    id_ex.isMul := false.B
  }.otherwise {
    id_ex.valid       := if_id.valid && !icacheStall
    id_ex.pc          := if_id.pc
    id_ex.inst        := if_id.inst
    id_ex.rs1         := d.rs1
    id_ex.rs2         := d.rs2
    id_ex.rd          := d.rd
    id_ex.funct3      := f.funct3
    id_ex.rs1Data     := rs1Val
    id_ex.rs2Data     := rs2Val
    id_ex.imm         := d.imm
    id_ex.aluOp       := c.aluOp
    id_ex.aluSrc      := c.aluSrc
    id_ex.regWrite    := c.regWrite
    id_ex.memRead     := c.memRead
    id_ex.memWrite    := c.memWrite
    id_ex.memToReg    := c.memToReg
    id_ex.branch      := c.branch
    id_ex.jump        := c.jump
    id_ex.isJal       := (f.opcode === RiscvOpcodes.JAL)
    id_ex.isJalr      := (f.opcode === RiscvOpcodes.JALR)
    id_ex.isLui       := (f.opcode === RiscvOpcodes.LUI)
    id_ex.isAuipc     := (f.opcode === RiscvOpcodes.AUIPC)
    id_ex.isMul       := enableZmmul.B && f.isMul && (f.opcode === RiscvOpcodes.OP)
    id_ex.predTaken   := if_id.predTaken
    id_ex.predTarget  := if_id.predTarget
    id_ex.predHistory := if_id.predHistory
  }

  // ==========================================
  // STAGE 1: INSTRUCTION FETCH (IF)
  // ==========================================
  predictor.io.pc := pcReg

  val fetchedInst = WireDefault(0.U(xlen.W))
  if (enableIcache) {
    val ic = icache.get
    ic.io.req.valid          := !dcacheStall
    ic.io.req.bits.addr      := pcReg
    ic.io.req.bits.funct3    := 2.U // word
    ic.io.req.bits.isWrite   := false.B
    ic.io.req.bits.writeData := 0.U
    fetchedInst              := ic.io.resp.bits.readData
  } else {
    val ri = io.rawImem.get
    ri.addr     := pcReg
    fetchedInst := ri.inst
  }

  val ifNextPC = Mux(predictor.io.predTaken, predictor.io.predTarget, pcReg + 4.U)

  when(dcacheStall) {
    // Freeze PC and IF/ID on D-cache stall
    pcReg := pcReg
    if_id := if_id
  }.elsewhen(exRedirect) {
    pcReg            := exTargetPC
    if_id.valid      := false.B
    if_id.predTaken  := false.B
    if_id.predTarget := 0.U
  }.elsewhen(rawStall || icacheStall) {
    pcReg := pcReg
    if_id := if_id
  }.otherwise {
    pcReg             := ifNextPC
    if_id.valid       := true.B
    if_id.pc          := pcReg
    if_id.inst        := fetchedInst
    if_id.predTaken   := predictor.io.predTaken
    if_id.predTarget  := predictor.io.predTarget
    if_id.predHistory := predictor.io.predHistory
  }

  // Outputs
  io.pc          := pcReg
  io.inst        := if_id.inst
  io.instRetired := ex_mem.valid && !dcacheStall
  io.isStall     := rawStall || icacheStall || dcacheStall
}
