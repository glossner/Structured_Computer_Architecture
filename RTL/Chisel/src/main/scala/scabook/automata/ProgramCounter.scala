// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.automata

import chisel3._, chisel3.util._

object ProgramCounter {
  object Mode extends ChiselEnum {
    val Plus4, Branch, Jalr, Stall = Value
  }
}

class ProgramCounter(val width: Int = 32, val initPC: BigInt = 0) extends Module {
  import ProgramCounter._
  val io = IO(new Bundle {
    val mode       = Input(Mode())
    val branchImm  = Input(UInt(width.W))
    val jalrTarget = Input(UInt(width.W))
    val pc         = Output(UInt(width.W))
    val nextPC     = Output(UInt(width.W))
    val pcPlus4    = Output(UInt(width.W))
  })

  val pcReg = RegInit(initPC.U(width.W))

  val nextPCWire = MuxCase(pcReg + 4.U, Seq(
    (io.mode === Mode.Plus4)  -> (pcReg + 4.U),
    (io.mode === Mode.Branch) -> (pcReg + io.branchImm),
    (io.mode === Mode.Jalr)   -> Cat(io.jalrTarget(width - 1, 1), 0.U(1.W)),
    (io.mode === Mode.Stall)  -> pcReg
  ))

  pcReg      := nextPCWire
  io.pc      := pcReg
  io.nextPC  := nextPCWire
  io.pcPlus4 := pcReg + 4.U
}
