// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chisel3.util._
import scabook.memory.RiscvRegFile

/** Pipeline Register Bundles */
class IfIdBundle(val xlen: Int = 32) extends Bundle {
  val valid = Bool()
  val pc    = UInt(xlen.W)
  val inst  = UInt(xlen.W)
}

class IdExBundle(val xlen: Int = 32) extends Bundle {
  val valid     = Bool()
  val pc        = UInt(xlen.W)
  val inst      = UInt(xlen.W)
  val rs1       = UInt(5.W)
  val rs2       = UInt(5.W)
  val rd        = UInt(5.W)
  val funct3    = UInt(3.W)
  val rs1Data   = UInt(xlen.W)
  val rs2Data   = UInt(xlen.W)
  val imm       = UInt(xlen.W)
  val aluOp     = UInt(5.W)
  val aluSrc    = Bool()
  val regWrite  = Bool()
  val memRead   = Bool()
  val memWrite  = Bool()
  val memToReg  = Bool()
  val branch    = Bool()
  val jump      = Bool()
  val isJal     = Bool()
  val isJalr    = Bool()
  val isLui     = Bool()
  val isAuipc   = Bool()
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
}

/** RISC-V RV32I_Zmmul 4-Stage Pipelined Processor Core (Stall-on-Hazard).
  *
  * Pipeline Stages:
  *   1. IF (Instruction Fetch): Program counter generation and instruction memory fetch.
  *   2. ID (Instruction Decode): Instruction decode, operand fetch from register file with
  *      internal write-through bypass, and hazard detection unit (stalling on RAW hazards).
  *   3. EX (Execute): ALU computation (RV32I and Zmmul), branch condition evaluation,
  *      and jump target calculation with pipeline flush on control transfer.
  *   4. MEM/WB (Memory & Writeback): Data memory read/write and register file writeback.
  */
class RiscvPipelined(val xlen: Int = 32, val initPC: BigInt = 0, val enableZmmul: Boolean = true) extends Module {
  val io = IO(new Bundle {
    val imem     = new ImemPort(xlen, xlen)
    val dmem     = new DmemPort(xlen, xlen)
    val pc       = Output(UInt(xlen.W))
    val inst     = Output(UInt(xlen.W))
    val aluOut   = Output(UInt(xlen.W))
    val regWrite = Output(Bool())
  })

  // Hardware Subsystems
  val decoder = Module(new RiscvDecoder)
  val alu     = Module(new RiscvALU(width = xlen, enableZmmul = enableZmmul))
  val regFile = Module(new RiscvRegFile(width = xlen))

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

  val wbData = MuxCase(ex_mem.aluResult, Seq(
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
  // STAGE 3: EXECUTE (EX)
  // ==========================================
  alu.io.opA   := id_ex.rs1Data
  alu.io.opB   := Mux(id_ex.aluSrc, id_ex.imm, id_ex.rs2Data)
  alu.io.aluOp := id_ex.aluOp

  val branchCond = MuxCase(false.B, Seq(
    (id_ex.funct3 === "b000".U) -> alu.io.zero,
    (id_ex.funct3 === "b001".U) -> !alu.io.zero,
    (id_ex.funct3 === "b100".U) -> alu.io.lessThan,
    (id_ex.funct3 === "b101".U) -> !alu.io.lessThan,
    (id_ex.funct3 === "b110".U) -> alu.io.lessThanU,
    (id_ex.funct3 === "b111".U) -> !alu.io.lessThanU
  ))

  val branchTaken = id_ex.valid && id_ex.branch && branchCond
  val jalTaken    = id_ex.valid && id_ex.isJal
  val jalrTaken   = id_ex.valid && id_ex.isJalr
  val exRedirect  = branchTaken || jalTaken || jalrTaken

  val branchTarget = id_ex.pc + id_ex.imm
  val jalTarget    = id_ex.pc + id_ex.imm
  val jalrTarget   = Cat((id_ex.rs1Data + id_ex.imm)(xlen - 1, 1), 0.U(1.W))
  val exTargetPC   = Mux(jalrTaken, jalrTarget, branchTarget)

  // EX -> MEM/WB Register update
  ex_mem.valid     := id_ex.valid
  ex_mem.pc        := id_ex.pc
  ex_mem.inst      := id_ex.inst
  ex_mem.rd        := id_ex.rd
  ex_mem.funct3    := id_ex.funct3
  ex_mem.aluResult := alu.io.result
  ex_mem.rs2Data   := id_ex.rs2Data
  ex_mem.imm       := id_ex.imm
  ex_mem.regWrite  := id_ex.regWrite
  ex_mem.memRead   := id_ex.memRead
  ex_mem.memWrite  := id_ex.memWrite
  ex_mem.memToReg  := id_ex.memToReg
  ex_mem.isJal     := id_ex.isJal
  ex_mem.isJalr    := id_ex.isJalr
  ex_mem.isLui     := id_ex.isLui
  ex_mem.isAuipc   := id_ex.isAuipc

  // ==========================================
  // STAGE 2: INSTRUCTION DECODE (ID) & HAZARD DETECTION
  // ==========================================
  decoder.io.inst := if_id.inst
  val d = decoder.io.decoded
  val c = d.control
  val f = RiscvFields(if_id.inst)

  regFile.io.rs1 := d.rs1
  regFile.io.rs2 := d.rs2

  // No forwarding: operands read directly from architectural register file
  val rs1Val = regFile.io.rs1_data
  val rs2Val = regFile.io.rs2_data

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

  // Hazard Detection Unit: stall on RAW hazards with EX stage (distance 1) and MEM/WB stage (distance 2)
  val exWillWrite  = id_ex.valid && id_ex.regWrite && (id_ex.rd =/= 0.U)
  val memWillWrite = ex_mem.valid && ex_mem.regWrite && (ex_mem.rd =/= 0.U)

  val rawHazardRs1 = (exWillWrite && readsRs1 && (id_ex.rd === d.rs1)) ||
                     (memWillWrite && readsRs1 && (ex_mem.rd === d.rs1))
  val rawHazardRs2 = (exWillWrite && readsRs2 && (id_ex.rd === d.rs2)) ||
                     (memWillWrite && readsRs2 && (ex_mem.rd === d.rs2))
  val stall        = if_id.valid && (rawHazardRs1 || rawHazardRs2)

  // ID -> EX Pipeline Register update
  when(exRedirect) {
    // Control transfer flush overrides decode
    id_ex.valid    := false.B
    id_ex.regWrite := false.B
    id_ex.memRead  := false.B
    id_ex.memWrite := false.B
    id_ex.branch   := false.B
    id_ex.jump     := false.B
    id_ex.isJal    := false.B
    id_ex.isJalr   := false.B
    id_ex.isLui    := false.B
    id_ex.isAuipc  := false.B
  }.elsewhen(stall) {
    // Inject bubble into EX on RAW hazard stall
    id_ex.valid    := false.B
    id_ex.regWrite := false.B
    id_ex.memRead  := false.B
    id_ex.memWrite := false.B
    id_ex.branch   := false.B
    id_ex.jump     := false.B
    id_ex.isJal    := false.B
    id_ex.isJalr   := false.B
    id_ex.isLui    := false.B
    id_ex.isAuipc  := false.B
  }.otherwise {
    id_ex.valid    := if_id.valid
    id_ex.pc       := if_id.pc
    id_ex.inst     := if_id.inst
    id_ex.rs1      := d.rs1
    id_ex.rs2      := d.rs2
    id_ex.rd       := d.rd
    id_ex.funct3   := f.funct3
    id_ex.rs1Data  := rs1Val
    id_ex.rs2Data  := rs2Val
    id_ex.imm      := d.imm
    id_ex.aluOp    := c.aluOp
    id_ex.aluSrc   := c.aluSrc
    id_ex.regWrite := c.regWrite
    id_ex.memRead  := c.memRead
    id_ex.memWrite := c.memWrite
    id_ex.memToReg := c.memToReg
    id_ex.branch   := c.branch
    id_ex.jump     := c.jump
    id_ex.isJal    := (f.opcode === RiscvOpcodes.JAL)
    id_ex.isJalr   := (f.opcode === RiscvOpcodes.JALR)
    id_ex.isLui    := (f.opcode === RiscvOpcodes.LUI)
    id_ex.isAuipc  := (f.opcode === RiscvOpcodes.AUIPC)
  }

  // ==========================================
  // STAGE 1: INSTRUCTION FETCH (IF)
  // ==========================================
  io.imem.addr := pcReg
  val ifInst = io.imem.inst

  when(exRedirect) {
    pcReg        := exTargetPC
    if_id.valid  := false.B
  }.elsewhen(stall) {
    // Freeze PC and IF/ID register on hazard stall
    pcReg        := pcReg
    if_id        := if_id
  }.otherwise {
    pcReg        := pcReg + 4.U
    if_id.valid  := true.B
    if_id.pc     := pcReg
    if_id.inst   := ifInst
  }

  // Observability Ports
  io.pc       := ex_mem.pc
  io.inst     := ex_mem.inst
  io.aluOut   := ex_mem.aluResult
  io.regWrite := wbWen
}
