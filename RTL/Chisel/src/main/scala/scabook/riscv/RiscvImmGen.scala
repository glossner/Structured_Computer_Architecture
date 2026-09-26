// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chisel3.util._

/** RISC-V Immediate Generator for I, S, B, U, and J formats. */
object RiscvImmGen {
  def immI(inst: UInt): UInt = Cat(Fill(20, inst(31)), inst(31, 20))
  def immS(inst: UInt): UInt = Cat(Fill(20, inst(31)), inst(31, 25), inst(11, 7))
  def immB(inst: UInt): UInt = Cat(Fill(20, inst(31)), inst(7), inst(30, 25), inst(11, 8), 0.U(1.W))
  def immU(inst: UInt): UInt = Cat(inst(31, 12), 0.U(12.W))
  def immJ(inst: UInt): UInt = Cat(Fill(12, inst(31)), inst(19, 12), inst(20), inst(30, 21), 0.U(1.W))
}

class RiscvImmGen extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val imm  = Output(UInt(32.W))
  })

  val opcode = io.inst(6, 0)

  io.imm := MuxLookup(opcode, 0.U(32.W))(Seq(
    RiscvOpcodes.LOAD   -> RiscvImmGen.immI(io.inst),
    RiscvOpcodes.OP_IMM -> RiscvImmGen.immI(io.inst),
    RiscvOpcodes.JALR   -> RiscvImmGen.immI(io.inst),
    RiscvOpcodes.STORE  -> RiscvImmGen.immS(io.inst),
    RiscvOpcodes.BRANCH -> RiscvImmGen.immB(io.inst),
    RiscvOpcodes.LUI    -> RiscvImmGen.immU(io.inst),
    RiscvOpcodes.AUIPC  -> RiscvImmGen.immU(io.inst),
    RiscvOpcodes.JAL    -> RiscvImmGen.immJ(io.inst)
  ))
}
