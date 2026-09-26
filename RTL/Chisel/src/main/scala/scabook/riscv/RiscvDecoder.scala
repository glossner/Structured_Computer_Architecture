// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._, chisel3.util._

/** RISC-V RV32I_Zmmul Modular Instruction and Control Decoder. */
class RiscvDecoder extends Module {
  val io = IO(new Bundle {
    val inst    = Input(UInt(32.W))
    val decoded = Output(new DecodedInstruction)
  })

  val f      = RiscvFields(io.inst)
  val immGen = Module(new RiscvImmGen); immGen.io.inst := io.inst

  val op       = f.opcode
  val isRType  = (op === RiscvOpcodes.OP)
  val isIType  = (op === RiscvOpcodes.OP_IMM)
  val isLoad   = (op === RiscvOpcodes.LOAD)
  val isStore  = (op === RiscvOpcodes.STORE)
  val isBranch = (op === RiscvOpcodes.BRANCH)
  val isJump   = (op === RiscvOpcodes.JAL  || op === RiscvOpcodes.JALR)
  val isUpper  = (op === RiscvOpcodes.LUI  || op === RiscvOpcodes.AUIPC)
  val isShift  = (f.funct3 === "b001".U    || f.funct3 === "b101".U)

  val aluOpWire = MuxCase(AluOp.ADD, Seq(
    isRType  -> Cat(f.isMul, f.isAlt, f.funct3),
    isIType  -> Cat(0.U(1.W), Mux(isShift, f.isAlt, 0.U(1.W)), f.funct3),
    isBranch -> AluOp.SUB
  ))

  val d = io.decoded; val c = d.control
  d.rd  := f.rd;  d.rs1 := f.rs1; d.rs2 := f.rs2; d.imm := immGen.io.imm
  c.regWrite := isRType || isIType || isLoad || isJump || isUpper
  c.memRead  := isLoad;   c.memWrite := isStore
  c.branch   := isBranch; c.jump     := isJump
  c.aluSrc   := isIType || isLoad || isStore || isUpper
  c.memToReg := isLoad;   c.aluOp    := aluOpWire
}
