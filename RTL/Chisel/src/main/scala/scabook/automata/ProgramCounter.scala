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
  })

  val pcReg      = RegInit(initPC.U(width.W))
  val nextPCWire = WireDefault(pcReg + 4.U)

  switch(io.mode) {
    is(Mode.Plus4)  { nextPCWire := pcReg + 4.U }
    is(Mode.Branch) { nextPCWire := pcReg + io.branchImm }
    is(Mode.Jalr)   { nextPCWire := Cat(io.jalrTarget(width - 1, 1), 0.U(1.W)) }
    is(Mode.Stall)  { nextPCWire := pcReg }
  }

  pcReg     := nextPCWire
  io.pc     := pcReg
  io.nextPC := nextPCWire
}
