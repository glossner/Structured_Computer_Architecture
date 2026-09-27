// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chisel3.util._
import scabook.memory.RiscvRegFile
import scabook.multipliers.{PipelinedMultiplierStage1, PipelinedMultiplierStage2}

/** Pipeline Register Bundles */
class IfIdBundle(val xlen: Int = 32) extends Bundle {
  val valid       = Bool()
  val pc          = UInt(xlen.W)
  val inst        = UInt(xlen.W)
  val predTaken   = Bool()
  val predTarget  = UInt(xlen.W)
  val predHistory = UInt(8.W)
}

class IdExBundle(val xlen: Int = 32) extends Bundle {
  val valid       = Bool()
  val pc          = UInt(xlen.W)
  val inst        = UInt(xlen.W)
  val rs1         = UInt(5.W)
  val rs2         = UInt(5.W)
  val rd          = UInt(5.W)
  val funct3      = UInt(3.W)
  val rs1Data     = UInt(xlen.W)
  val rs2Data     = UInt(xlen.W)
  val imm         = UInt(xlen.W)
  val aluOp       = UInt(5.W)
  val aluSrc      = Bool()
  val regWrite    = Bool()
  val memRead     = Bool()
  val memWrite    = Bool()
  val memToReg    = Bool()
  val branch      = Bool()
  val jump        = Bool()
  val isJal       = Bool()
  val isJalr      = Bool()
  val isLui       = Bool()
  val isAuipc     = Bool()
  val isMul       = Bool()
  val predTaken   = Bool()
  val predTarget  = UInt(xlen.W)
  val predHistory = UInt(8.W)
}

class ExMemBundle(val xlen: Int = 32) extends Bundle {
  val valid     = Bool()
  val pc        = UInt(xlen.W)
  val inst      = UInt(xlen.W)
  val rd        = UInt(5.W)
  val funct3    = UInt(3.W)
  val aluResult = UInt(xlen.W)
  val rs2Data   = UInt(xlen.W)
  val imm       = UInt(xlen.W)
  val regWrite  = Bool()
  val memRead   = Bool()
  val memWrite  = Bool()
  val memToReg  = Bool()
  val isJal     = Bool()
  val isJalr    = Bool()
  val isLui     = Bool()
  val isAuipc   = Bool()
  val isMul     = Bool()
  val mulOp     = UInt(5.W)
  val mulSum    = UInt((2 * xlen).W)
  val mulCarry  = UInt((2 * xlen).W)
}

/** RISC-V RV32I_Zmmul 4-Stage Pipelined Processor Core (Stall-on-Hazard).
  *
  * Pipeline Stages:
  *   1. IF (Instruction Fetch): Program counter generation and instruction memory fetch.
  *   2. ID (Instruction Decode): Instruction decode, operand fetch from register file with
  *      internal write-through bypass, and hazard detection unit (stalling on RAW hazards).
  *   3. EX (Execute): ALU computation (RV32I and Zmmul Stage 1), branch condition evaluation,
  *      and jump target calculation with pipeline flush on control transfer.
  *   4. MEM/WB (Memory & Writeback): Data memory read/write, Zmmul Stage 2 vector-merging adder,
  *      forwarding to EX, and register file writeback.
  */
class RiscvPipelined(
  val xlen: Int = 32,
  val initPC: BigInt = 0,
  val enableZmmul: Boolean = true,
  val enableForwarding: Boolean = true,
  val enablePipelinedMul: Boolean = true,
  val branchPredictor: String = "none"
) extends Module {
  val io = IO(new Bundle {
    val imem     = new ImemPort(xlen, xlen)
    val dmem     = new DmemPort(xlen, xlen)
    val pc       = Output(UInt(xlen.W))
    val inst     = Output(UInt(xlen.W))
    val aluOut   = Output(UInt(xlen.W))
    val regWrite = Output(Bool())
  })

  // Hardware Subsystems
  val decoder   = Module(new RiscvDecoder)
  val alu       = Module(new RiscvALU(width = xlen, enableZmmul = enableZmmul && !enablePipelinedMul))
  val regFile   = Module(new RiscvRegFile(width = xlen))
  val predictor = Module(new BranchPredictor(xlen = xlen, predictorType = branchPredictor))

  // Pipelined Multiplier Modules
  val mulStage1 = if (enableZmmul && enablePipelinedMul) Some(Module(new PipelinedMultiplierStage1(xlen))) else None
  val mulStage2 = if (enableZmmul && enablePipelinedMul) Some(Module(new PipelinedMultiplierStage2(xlen))) else None

  // Pipeline Registers
  val if_id  = RegInit(0.U.asTypeOf(new IfIdBundle(xlen)))
  val id_ex  = RegInit(0.U.asTypeOf(new IdExBundle(xlen)))
  val ex_mem = RegInit(0.U.asTypeOf(new ExMemBundle(xlen)))

  val pcReg = RegInit(initPC.U(xlen.W))

  // ==========================================
  // STAGE 4: MEMORY ACCESS & WRITE-BACK (MEM/WB)
  // ==========================================
  io.dmem.addr      := ex_mem.aluResult
  io.dmem.funct3    := ex_mem.funct3
  io.dmem.memRead   := ex_mem.valid && ex_mem.memRead
  io.dmem.memWrite  := ex_mem.valid && ex_mem.memWrite
  io.dmem.writeData := ex_mem.rs2Data

  val mulStage2Result = WireDefault(0.U(xlen.W))
  if (mulStage2.isDefined) {
    mulStage2.get.io.sumIn   := ex_mem.mulSum
    mulStage2.get.io.carryIn := ex_mem.mulCarry
    mulStage2.get.io.mulOp   := ex_mem.mulOp
    mulStage2Result          := mulStage2.get.io.result
  }

  val wbData = MuxCase(ex_mem.aluResult, Seq(
    ex_mem.isMul                    -> mulStage2Result,
    ex_mem.memToReg                 -> io.dmem.readData,
    (ex_mem.isJal || ex_mem.isJalr) -> (ex_mem.pc + 4.U),
    ex_mem.isLui                    -> ex_mem.imm,
    ex_mem.isAuipc                  -> (ex_mem.pc + ex_mem.imm)
  ))
  val wbWen = ex_mem.valid && ex_mem.regWrite && (ex_mem.rd =/= 0.U)
  val wbRd  = ex_mem.rd

  regFile.io.wen     := wbWen
  regFile.io.rd      := wbRd
  regFile.io.rd_data := wbData

  // ==========================================
  // STAGE 3: EXECUTE (EX) & FORWARDING UNIT
  // ==========================================
  // Forwarding Unit (MEM/WB -> EX) for Distance-1 RAW Hazards:
  // Forward from ex_mem to EX stage if ex_mem writes to rd and rd matches rs1/rs2.
  val exMemWillWrite = ex_mem.valid && ex_mem.regWrite && (ex_mem.rd =/= 0.U)
  val exMemFwdData = MuxCase(ex_mem.aluResult, Seq(
    ex_mem.isMul                    -> mulStage2Result,
    (ex_mem.isJal || ex_mem.isJalr) -> (ex_mem.pc + 4.U),
    ex_mem.isLui                    -> ex_mem.imm,
    ex_mem.isAuipc                  -> (ex_mem.pc + ex_mem.imm)
  ))

  val forwardRs1 = enableForwarding.B && exMemWillWrite && (ex_mem.rd === id_ex.rs1) && (id_ex.rs1 =/= 0.U)
  val forwardRs2 = enableForwarding.B && exMemWillWrite && (ex_mem.rd === id_ex.rs2) && (id_ex.rs2 =/= 0.U)

  val exRs1 = Mux(forwardRs1, exMemFwdData, id_ex.rs1Data)
  val exRs2 = Mux(forwardRs2, exMemFwdData, id_ex.rs2Data)

  alu.io.opA   := exRs1
  alu.io.opB   := Mux(id_ex.aluSrc, id_ex.imm, exRs2)
  alu.io.aluOp := id_ex.aluOp

  if (mulStage1.isDefined) {
    mulStage1.get.io.a     := exRs1
    mulStage1.get.io.b     := Mux(id_ex.aluSrc, id_ex.imm, exRs2)
    mulStage1.get.io.mulOp := id_ex.aluOp
  }

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

  // Branch & Jump Misprediction Detection:
  // When predicted correctly (outcome and target match), NO pipeline flush occurs (0 bubbles).
  // On misprediction, pipeline is flushed (2-bubble penalty) and redirected to the correct path.
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

  val exRedirect = branchMispredicted || jalMispredicted || jalrRedirect
  val exTargetPC = Mux(jalrRedirect, jalrTarget,
                   Mux(jalMispredicted, jalTarget,
                       branchRecoveryPC))

  // Branch Predictor Update (from EX stage resolution)
  predictor.io.updateValid  := id_ex.valid && (id_ex.branch || id_ex.isJal)
  predictor.io.updateIsBr   := id_ex.branch
  predictor.io.updateIsJal  := id_ex.isJal
  predictor.io.updatePC     := id_ex.pc
  predictor.io.actualTaken  := Mux(id_ex.isJal, true.B, branchTaken)
  predictor.io.actualTarget := Mux(id_ex.isJal, jalTarget, branchTarget)
  predictor.io.prevHistory  := id_ex.predHistory

  // EX -> MEM/WB Register update
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

  // ==========================================
  // STAGE 2: INSTRUCTION DECODE (ID) & HAZARD DETECTION
  // ==========================================
  decoder.io.inst := if_id.inst
  val d = decoder.io.decoded
  val c = d.control
  val f = RiscvFields(if_id.inst)

  regFile.io.rs1 := d.rs1
  regFile.io.rs2 := d.rs2

  // Internal Register File / WB-to-ID Write-Through Bypass for Distance-2 Hazards:
  // If the instruction in MEM/WB writes to rs1/rs2, bypass wbData directly to ID operands.
  val wbBypassRs1 = enableForwarding.B && wbWen && (wbRd === d.rs1) && (d.rs1 =/= 0.U)
  val wbBypassRs2 = enableForwarding.B && wbWen && (wbRd === d.rs2) && (d.rs2 =/= 0.U)
  val rs1Val = Mux(wbBypassRs1, wbData, regFile.io.rs1_data)
  val rs2Val = Mux(wbBypassRs2, wbData, regFile.io.rs2_data)

  // Instruction operand read usage
  val readsRs1 = (f.opcode === RiscvOpcodes.OP     ||
                  f.opcode === RiscvOpcodes.OP_IMM ||
                  f.opcode === RiscvOpcodes.LOAD   ||
                  f.opcode === RiscvOpcodes.STORE  ||
                  f.opcode === RiscvOpcodes.BRANCH ||
                  f.opcode === RiscvOpcodes.JALR) && (d.rs1 =/= 0.U)

  val readsRs2 = (f.opcode === RiscvOpcodes.OP     ||
                  f.opcode === RiscvOpcodes.STORE  ||
                  f.opcode === RiscvOpcodes.BRANCH) && (d.rs2 =/= 0.U)

  // Hazard Detection Unit:
  // With forwarding enabled:
  //   Only Load-Use Hazards (instruction in EX is a LOAD and instruction in ID reads its rd) must stall for 1 cycle.
  //   All other RAW hazards (ALU distance 1 and distance 2) are resolved by forwarding without stalls.
  // Without forwarding:
  //   Stall on RAW hazards with EX stage (distance 1) and MEM/WB stage (distance 2).
  val exWillWrite  = id_ex.valid && id_ex.regWrite && (id_ex.rd =/= 0.U)
  val memWillWrite = ex_mem.valid && ex_mem.regWrite && (ex_mem.rd =/= 0.U)

  val isLoadUseHazard = id_ex.valid && id_ex.memRead && (id_ex.rd =/= 0.U) &&
                        ((readsRs1 && (id_ex.rd === d.rs1)) || (readsRs2 && (id_ex.rd === d.rs2)))

  val rawHazardRs1 = (exWillWrite && readsRs1 && (id_ex.rd === d.rs1)) ||
                     (memWillWrite && readsRs1 && (ex_mem.rd === d.rs1))
  val rawHazardRs2 = (exWillWrite && readsRs2 && (id_ex.rd === d.rs2)) ||
                     (memWillWrite && readsRs2 && (ex_mem.rd === d.rs2))

  val stall = if_id.valid && Mux(enableForwarding.B, isLoadUseHazard, rawHazardRs1 || rawHazardRs2)

  // ID -> EX Pipeline Register update
  when(exRedirect) {
    // Control transfer flush overrides decode
    id_ex.valid       := false.B
    id_ex.regWrite    := false.B
    id_ex.memRead     := false.B
    id_ex.memWrite    := false.B
    id_ex.branch      := false.B
    id_ex.jump        := false.B
    id_ex.isJal       := false.B
    id_ex.isJalr      := false.B
    id_ex.isLui       := false.B
    id_ex.isAuipc     := false.B
    id_ex.isMul       := false.B
    id_ex.predTaken   := false.B
    id_ex.predTarget  := 0.U
    id_ex.predHistory := 0.U
  }.elsewhen(stall) {
    // Inject bubble into EX on RAW hazard stall
    id_ex.valid       := false.B
    id_ex.regWrite    := false.B
    id_ex.memRead     := false.B
    id_ex.memWrite    := false.B
    id_ex.branch      := false.B
    id_ex.jump        := false.B
    id_ex.isJal       := false.B
    id_ex.isJalr      := false.B
    id_ex.isLui       := false.B
    id_ex.isAuipc     := false.B
    id_ex.isMul       := false.B
    id_ex.predTaken   := false.B
    id_ex.predTarget  := 0.U
    id_ex.predHistory := 0.U
  }.otherwise {
    id_ex.valid       := if_id.valid
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

  io.imem.addr := pcReg
  val ifInst = io.imem.inst

  val ifNextPC = Mux(predictor.io.predTaken, predictor.io.predTarget, pcReg + 4.U)

  when(exRedirect) {
    pcReg             := exTargetPC
    if_id.valid       := false.B
    if_id.predTaken   := false.B
    if_id.predTarget  := 0.U
    if_id.predHistory := 0.U
  }.elsewhen(stall) {
    // Freeze PC and IF/ID register on hazard stall
    pcReg        := pcReg
    if_id        := if_id
  }.otherwise {
    pcReg             := ifNextPC
    if_id.valid       := true.B
    if_id.pc          := pcReg
    if_id.inst        := ifInst
    if_id.predTaken   := predictor.io.predTaken
    if_id.predTarget  := predictor.io.predTarget
    if_id.predHistory := predictor.io.predHistory
  }

  // Observability Ports
  io.pc       := ex_mem.pc
  io.inst     := ex_mem.inst
  io.aluOut   := Mux(ex_mem.isMul, mulStage2Result, ex_mem.aluResult)
  io.regWrite := wbWen
}
