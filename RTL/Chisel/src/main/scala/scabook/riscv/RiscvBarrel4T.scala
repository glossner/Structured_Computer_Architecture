// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chisel3.util._
import scabook.memory.{L1Cache, CacheCpuReq, CacheCpuResp, CacheMemReq, CacheMemResp, RiscvRegFileMT}
import scabook.multipliers.{PipelinedMultiplierStage1, PipelinedMultiplierStage2}

/**
  * 4-Thread Barrel Multithreaded RISC-V RV32I_Zmmul Processor Core (RiscvBarrel4T).
  *
  * Features:
  *   - 4 hardware threads (T0, T1, T2, T3) scheduled round-robin with token-triggered execution
  *   - 4-stage pipeline: IF, ID, EX, MEM/WB
  *   - Multi-threaded register file (RiscvRegFileMT, 4 threads * 32 registers = 128 registers)
  *   - Balanced Carry-Select Adder (CSelA) ALU
  *   - 2-stage Pipelined Multiplier (Pipemul) across EX and MEM/WB
  *   - Inter-thread pipeline hazard elimination:
  *       Because consecutive instructions of any single thread are separated by 4 cycles,
  *       single-cycle ALU dependencies and branch redirections commit with zero stall bubbles!
  *   - Shared Level-1 Instruction Cache (4 KB, parameterized ways)
  *   - Shared Level-1 Data Cache (4 KB, parameterized ways)
  *   - External memory interface for Unified L2 Cache / DDR-5 memory bus
  */
class RiscvBarrel4T(
  val xlen: Int = 32,
  val initPC: BigInt = 0x80000000L,
  val numThreads: Int = 4,
  val enableZmmul: Boolean = true,
  val enableForwarding: Boolean = true,
  val enablePipelinedMul: Boolean = true,
  val icacheBytes: Int = 4096,
  val icacheWays: Int = 4,
  val dcacheBytes: Int = 4096,
  val dcacheWays: Int = 4,
  val lineBytes: Int = 32
) extends Module {
  val threadWidth = log2Ceil(numThreads)

  val io = IO(new Bundle {
    // External Memory Ports for Cache Refill and Writeback (shared L1 to L2/DDR-5)
    val imemRefill = Decoupled(new CacheMemReq(xlen, lineBytes))
    val imemResp   = Flipped(Valid(new CacheMemResp(lineBytes)))
    val dmemRefill = Decoupled(new CacheMemReq(xlen, lineBytes))
    val dmemResp   = Flipped(Valid(new CacheMemResp(lineBytes)))

    // Fallback unbuffered memory ports when caches are bypassed (for conformance tests)
    val rawImem = new ImemPort(xlen, xlen)
    val rawDmem = new DmemPort(xlen, xlen)
    val bypassCaches = Input(Bool())

    // Per-thread dynamic reset / restart control
    val threadReset   = Input(UInt(numThreads.W))
    val threadResetPC = Input(Vec(numThreads, UInt(xlen.W)))

    // Architectural Profiling Signals
    val currentThread   = Output(UInt(threadWidth.W))
    val activePC        = Output(UInt(xlen.W))
    val instRetired     = Output(Bool())
    val retiredThread   = Output(UInt(threadWidth.W))
    val retiredPC       = Output(UInt(xlen.W))
    val retiredInst     = Output(UInt(xlen.W))
    val isStall         = Output(Bool())
    val icacheHit       = Output(Bool())
    val icacheMiss      = Output(Bool())
    val dcacheHit       = Output(Bool())
    val dcacheMiss      = Output(Bool())
  })

  // Hardware Thread Contexts (Independent PCs initialized to initPC)
  val pcRegs = RegInit(VecInit(Seq.fill(numThreads)(initPC.U(xlen.W))))
  val fetchToken = RegInit(0.U(threadWidth.W))

  // Subsystems
  val regFile   = Module(new RiscvRegFileMT(xlen, numThreads))
  regFile.io.clearThread := io.threadReset
  val decoder   = Module(new RiscvDecoder())
  val alu       = Module(new RiscvALU(xlen, enableZmmul = !enablePipelinedMul))
  val mulStage1 = if (enableZmmul && enablePipelinedMul) Some(Module(new PipelinedMultiplierStage1(xlen))) else None
  val mulStage2 = if (enableZmmul && enablePipelinedMul) Some(Module(new PipelinedMultiplierStage2(xlen))) else None

  // Shared L1 Caches
  val icache = Module(new L1Cache(icacheBytes, icacheWays, lineBytes, isDcache = false, xlen))
  val dcache = Module(new L1Cache(dcacheBytes, dcacheWays, lineBytes, isDcache = true, xlen))

  // External memory routing
  io.imemRefill <> icache.io.memReq
  icache.io.memResp := io.imemResp
  io.dmemRefill <> dcache.io.memReq
  dcache.io.memResp := io.dmemResp

  val cacheStall = !io.bypassCaches && (icache.io.stall || dcache.io.stall)
  io.isStall := cacheStall

  // Thread-annotated Pipeline Registers
  val if_id_threadId  = RegInit(0.U(threadWidth.W))
  val if_id_pc        = RegInit(0.U(xlen.W))
  val if_id_inst      = RegInit(0.U(xlen.W))
  val if_id_valid     = RegInit(false.B)

  val id_ex_threadId  = RegInit(0.U(threadWidth.W))
  val id_ex_pc        = RegInit(0.U(xlen.W))
  val id_ex_inst      = RegInit(0.U(xlen.W))
  val id_ex_rs1Data   = RegInit(0.U(xlen.W))
  val id_ex_rs2Data   = RegInit(0.U(xlen.W))
  val id_ex_imm       = RegInit(0.U(xlen.W))
  val id_ex_rd        = RegInit(0.U(5.W))
  val id_ex_rs1       = RegInit(0.U(5.W))
  val id_ex_rs2       = RegInit(0.U(5.W))
  val id_ex_aluOp     = RegInit(0.U(5.W))
  val id_ex_aluSrc    = RegInit(false.B)
  val id_ex_memRead   = RegInit(false.B)
  val id_ex_memWrite  = RegInit(false.B)
  val id_ex_memToReg  = RegInit(false.B)
  val id_ex_regWrite  = RegInit(false.B)
  val id_ex_branch    = RegInit(false.B)
  val id_ex_jump      = RegInit(false.B)
  val id_ex_isJal     = RegInit(false.B)
  val id_ex_isJalr    = RegInit(false.B)
  val id_ex_isLui     = RegInit(false.B)
  val id_ex_isAuipc   = RegInit(false.B)
  val id_ex_isMul     = RegInit(false.B)
  val id_ex_funct3    = RegInit(0.U(3.W))
  val id_ex_valid     = RegInit(false.B)

  val ex_mem_threadId = RegInit(0.U(threadWidth.W))
  val ex_mem_pc       = RegInit(0.U(xlen.W))
  val ex_mem_inst     = RegInit(0.U(xlen.W))
  val ex_mem_aluResult= RegInit(0.U(xlen.W))
  val ex_mem_writeData= RegInit(0.U(xlen.W))
  val ex_mem_imm      = RegInit(0.U(xlen.W))
  val ex_mem_rd       = RegInit(0.U(5.W))
  val ex_mem_memRead  = RegInit(false.B)
  val ex_mem_memWrite = RegInit(false.B)
  val ex_mem_memToReg = RegInit(false.B)
  val ex_mem_regWrite = RegInit(false.B)
  val ex_mem_isJal    = RegInit(false.B)
  val ex_mem_isJalr   = RegInit(false.B)
  val ex_mem_isLui    = RegInit(false.B)
  val ex_mem_isAuipc  = RegInit(false.B)
  val ex_mem_funct3   = RegInit(0.U(3.W))
  val ex_mem_isMul    = RegInit(false.B)
  val ex_mem_mulOp    = RegInit(0.U(5.W))
  val ex_mem_mulSum   = RegInit(0.U((2 * xlen).W))
  val ex_mem_mulCarry = RegInit(0.U((2 * xlen).W))
  val ex_mem_valid    = RegInit(false.B)

  // ==========================================
  // STAGE 1: INSTRUCTION FETCH (IF)
  // ==========================================
  val currentFetchThread = fetchToken
  val currentFetchPC     = pcRegs(currentFetchThread)

  // Drive cache / raw memory request
  icache.io.req.valid := !io.bypassCaches && !dcache.io.stall
  icache.io.req.bits.addr := currentFetchPC
  icache.io.req.bits.writeData := 0.U
  icache.io.req.bits.isWrite := false.B
  icache.io.req.bits.funct3 := 2.U // Word

  io.rawImem.addr := currentFetchPC
  val fetchedInst = Mux(io.bypassCaches, io.rawImem.inst, icache.io.resp.bits.readData)

  when(!cacheStall) {
    if_id_valid    := !io.threadReset(currentFetchThread)
    if_id_threadId := currentFetchThread
    if_id_pc       := currentFetchPC
    if_id_inst     := fetchedInst

    // Sequential PC increment for current thread (branches update target later in EX)
    pcRegs(currentFetchThread) := currentFetchPC + 4.U

    // Advance round-robin token to next thread
    fetchToken := (fetchToken + 1.U) % numThreads.U
  }

  // ==========================================
  // STAGE 2: INSTRUCTION DECODE (ID)
  // ==========================================
  decoder.io.inst := if_id_inst
  val d = decoder.io.decoded
  val c = d.control
  val f = RiscvFields(if_id_inst)

  // Read register file for the thread currently in ID
  regFile.io.readThreadId := if_id_threadId
  regFile.io.rs1 := d.rs1
  regFile.io.rs2 := d.rs2

  // Write-back bypass from Stage 4 (if committing thread matches reading thread)
  val wbBypassRs1 = enableForwarding.B && ex_mem_valid && ex_mem_regWrite && (ex_mem_rd === d.rs1) && (d.rs1 =/= 0.U) && (ex_mem_threadId === if_id_threadId)
  val wbBypassRs2 = enableForwarding.B && ex_mem_valid && ex_mem_regWrite && (ex_mem_rd === d.rs2) && (d.rs2 =/= 0.U) && (ex_mem_threadId === if_id_threadId)

  // Stage 2 Multiplier Result Wire
  val mulStage2Result = WireDefault(0.U(xlen.W))
  if (enableZmmul && enablePipelinedMul) {
    mulStage2.get.io.sumIn   := ex_mem_mulSum
    mulStage2.get.io.carryIn := ex_mem_mulCarry
    mulStage2.get.io.mulOp   := ex_mem_mulOp
    mulStage2Result          := mulStage2.get.io.result
  }

  val dmemReadData = Mux(io.bypassCaches, io.rawDmem.readData, dcache.io.resp.bits.readData)

  val wbData = MuxCase(ex_mem_aluResult, Seq(
    ex_mem_memToReg -> dmemReadData,
    ex_mem_isJal    -> (ex_mem_pc + 4.U),
    ex_mem_isJalr   -> (ex_mem_pc + 4.U),
    ex_mem_isLui    -> ex_mem_imm,
    ex_mem_isAuipc  -> (ex_mem_pc + ex_mem_imm),
    (ex_mem_isMul && enablePipelinedMul.B) -> mulStage2Result
  ))

  val rs1Val = Mux(wbBypassRs1, wbData, regFile.io.rs1_data)
  val rs2Val = Mux(wbBypassRs2, wbData, regFile.io.rs2_data)

  when(!cacheStall) {
    id_ex_valid    := if_id_valid && !io.threadReset(if_id_threadId)
    id_ex_threadId := if_id_threadId
    id_ex_pc       := if_id_pc
    id_ex_inst     := if_id_inst
    id_ex_rs1Data  := rs1Val
    id_ex_rs2Data  := rs2Val
    id_ex_imm      := d.imm
    id_ex_rd       := d.rd
    id_ex_rs1      := d.rs1
    id_ex_rs2      := d.rs2
    id_ex_aluOp    := c.aluOp
    id_ex_aluSrc   := c.aluSrc
    id_ex_memRead  := c.memRead
    id_ex_memWrite := c.memWrite
    id_ex_memToReg := c.memToReg
    id_ex_regWrite := c.regWrite
    id_ex_branch   := c.branch
    id_ex_jump     := c.jump
    id_ex_isJal    := (f.opcode === RiscvOpcodes.JAL)
    id_ex_isJalr   := (f.opcode === RiscvOpcodes.JALR)
    id_ex_isLui    := (f.opcode === RiscvOpcodes.LUI)
    id_ex_isAuipc  := (f.opcode === RiscvOpcodes.AUIPC)
    id_ex_isMul    := enableZmmul.B && f.isMul && (f.opcode === RiscvOpcodes.OP)
    id_ex_funct3   := f.funct3
  }

  // ==========================================
  // STAGE 3: EXECUTE (EX)
  // ==========================================
  // Thread-aware forwarding: only forward if producer and consumer belong to the EXACT same thread!
  val fwdA_match = enableForwarding.B && ex_mem_valid && ex_mem_regWrite && (ex_mem_rd =/= 0.U) && (ex_mem_rd === id_ex_rs1) && (ex_mem_threadId === id_ex_threadId)
  val fwdB_match = enableForwarding.B && ex_mem_valid && ex_mem_regWrite && (ex_mem_rd =/= 0.U) && (ex_mem_rd === id_ex_rs2) && (ex_mem_threadId === id_ex_threadId)

  val opA_base = Mux(fwdA_match, wbData, id_ex_rs1Data)
  val opB_base = Mux(fwdB_match, wbData, id_ex_rs2Data)

  alu.io.aluOp := id_ex_aluOp
  alu.io.opA := opA_base
  alu.io.opB := Mux(id_ex_aluSrc, id_ex_imm, opB_base)

  // Multiplier Stage 1 (Booth Recoding + CSA Reduction Tree)
  if (enableZmmul && enablePipelinedMul) {
    mulStage1.get.io.a := opA_base
    mulStage1.get.io.b := Mux(id_ex_aluSrc, id_ex_imm, opB_base)
    mulStage1.get.io.mulOp := id_ex_aluOp
  }

  // Branch condition evaluation
  val branchCond = MuxCase(false.B, Seq(
    (id_ex_funct3 === "b000".U) -> alu.io.zero,
    (id_ex_funct3 === "b001".U) -> !alu.io.zero,
    (id_ex_funct3 === "b100".U) -> alu.io.lessThan,
    (id_ex_funct3 === "b101".U) -> !alu.io.lessThan,
    (id_ex_funct3 === "b110".U) -> alu.io.lessThanU,
    (id_ex_funct3 === "b111".U) -> !alu.io.lessThanU
  ))

  val branchTaken = (id_ex_branch && branchCond) || id_ex_jump
  val branchTarget = Mux(id_ex_isJalr, Cat((opA_base + id_ex_imm)(xlen - 1, 1), 0.U(1.W)), id_ex_pc + id_ex_imm)

  // When branch is taken, update the architectural PC of THAT thread directly!
  // In a 4-thread barrel core, that thread will not fetch again until cycle t+4,
  // so the branch target is ready with ZERO pipeline bubbles!
  when(id_ex_valid && branchTaken && !io.threadReset(id_ex_threadId)) {
    pcRegs(id_ex_threadId) := branchTarget
  }

  when(!cacheStall) {
    ex_mem_valid     := id_ex_valid && !io.threadReset(id_ex_threadId)
    ex_mem_threadId  := id_ex_threadId
    ex_mem_pc        := id_ex_pc
    ex_mem_inst      := id_ex_inst
    ex_mem_aluResult := alu.io.result
    ex_mem_writeData := opB_base
    ex_mem_imm       := id_ex_imm
    ex_mem_rd        := id_ex_rd
    ex_mem_memRead   := id_ex_memRead
    ex_mem_memWrite  := id_ex_memWrite
    ex_mem_memToReg  := id_ex_memToReg
    ex_mem_regWrite  := id_ex_regWrite
    ex_mem_isJal     := id_ex_isJal
    ex_mem_isJalr    := id_ex_isJalr
    ex_mem_isLui     := id_ex_isLui
    ex_mem_isAuipc   := id_ex_isAuipc
    ex_mem_funct3    := id_ex_funct3
    ex_mem_isMul     := id_ex_isMul
    ex_mem_mulOp     := id_ex_aluOp
    if (enableZmmul && enablePipelinedMul) {
      ex_mem_mulSum   := mulStage1.get.io.sumOut
      ex_mem_mulCarry := mulStage1.get.io.carryOut
    }
  }

  // ==========================================
  // STAGE 4: MEMORY / WRITE-BACK (MEM/WB)
  // ==========================================
  dcache.io.req.valid := ex_mem_valid && (ex_mem_memRead || ex_mem_memWrite) && !io.bypassCaches
  dcache.io.req.bits.addr := ex_mem_aluResult
  dcache.io.req.bits.writeData := ex_mem_writeData
  dcache.io.req.bits.isWrite := ex_mem_memWrite
  dcache.io.req.bits.funct3 := ex_mem_funct3

  io.rawDmem.addr := ex_mem_aluResult
  io.rawDmem.writeData := ex_mem_writeData
  io.rawDmem.memRead := ex_mem_valid && ex_mem_memRead && !io.threadReset(ex_mem_threadId)
  io.rawDmem.memWrite := ex_mem_valid && ex_mem_memWrite && !io.threadReset(ex_mem_threadId)
  io.rawDmem.funct3 := ex_mem_funct3

  // Commit to Register File for the committing thread
  regFile.io.writeThreadId := ex_mem_threadId
  regFile.io.rd := ex_mem_rd
  regFile.io.rd_data := wbData
  regFile.io.wen := ex_mem_valid && ex_mem_regWrite && !cacheStall && !io.threadReset(ex_mem_threadId)

  // Priority per-thread dynamic reset overrides
  for (t <- 0 until numThreads) {
    when(io.threadReset(t)) {
      pcRegs(t) := io.threadResetPC(t)
      when(if_id_threadId === t.U) {
        if_id_valid := false.B
      }
      when(id_ex_threadId === t.U) {
        id_ex_valid := false.B
      }
      when(ex_mem_threadId === t.U) {
        ex_mem_valid := false.B
      }
    }
  }

  // Profiling Outputs
  io.currentThread := currentFetchThread
  io.activePC      := currentFetchPC
  io.instRetired   := ex_mem_valid && !cacheStall
  io.retiredThread := ex_mem_threadId
  io.retiredPC     := ex_mem_pc
  io.retiredInst   := ex_mem_inst
  io.icacheHit     := icache.io.perfHit
  io.icacheMiss    := icache.io.perfMiss
  io.dcacheHit     := dcache.io.perfHit
  io.dcacheMiss    := dcache.io.perfMiss
}
