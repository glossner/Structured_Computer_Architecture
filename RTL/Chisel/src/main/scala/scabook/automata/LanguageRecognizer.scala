// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.automata

import chisel3._, chisel3.util._

object LanguageRecognizer {
  object State extends ChiselEnum { val sInit, sRunA, sRunB, sError = Value }
  object OutputCode { val Fail = 0.U(2.W); val RecB = 1.U(2.W); val RecA = 2.U(2.W) }
}

class LanguageRecognizer extends Module {
  import LanguageRecognizer._
  val io = IO(new Bundle {
    val in    = Input(Bool())
    val out   = Output(UInt(2.W))
    val state = Output(State())
  })

  val state = RegInit(State.sInit)
  val isA   = io.in
  val isB   = !io.in

  // One-hot state decoding
  val inInit  = state === State.sInit
  val inRunA  = state === State.sRunA
  val inRunB  = state === State.sRunB
  val inError = state === State.sError

  // Next-State Combinational Logic (loopCLC)
  val nextState = MuxCase(State.sError, Seq(
    inInit  -> Mux(isA, State.sRunA, State.sError),
    inRunA  -> Mux(isA, State.sRunA, State.sRunB),
    inRunB  -> Mux(isB, State.sRunB, State.sError),
    inError -> State.sError
  ))

  // Output Combinational Logic (outCLC)
  val outCode = MuxCase(OutputCode.Fail, Seq(
    inInit  -> Mux(isA, OutputCode.RecA, OutputCode.Fail),
    inRunA  -> Mux(isA, OutputCode.RecA, OutputCode.RecB),
    inRunB  -> Mux(isB, OutputCode.RecB, OutputCode.Fail),
    inError -> OutputCode.Fail
  ))

  state    := nextState
  io.state := state
  io.out   := outCode
}
