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

  val state     = RegInit(State.sInit)
  val nextState = WireDefault(state)
  val outCode   = WireDefault(OutputCode.Fail)

  switch(state) {
    is(State.sInit)  { nextState := Mux(io.in, State.sRunA, State.sError)
                       outCode   := Mux(io.in, OutputCode.RecA, OutputCode.Fail) }
    is(State.sRunA)  { nextState := Mux(io.in, State.sRunA, State.sRunB)
                       outCode   := Mux(io.in, OutputCode.RecA, OutputCode.RecB) }
    is(State.sRunB)  { nextState := Mux(!io.in, State.sRunB, State.sError)
                       outCode   := Mux(!io.in, OutputCode.RecB, OutputCode.Fail) }
    is(State.sError) { nextState := State.sError; outCode := OutputCode.Fail }
  }
  state := nextState; io.state := state; io.out := outCode
}
