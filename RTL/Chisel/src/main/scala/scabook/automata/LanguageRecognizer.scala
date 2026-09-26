// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.automata

import chisel3._
import chisel3.util._

/**
  * Mealy Language Recognizer Automaton.
  *
  * Recognizes strings in the regular language a+ b+ where 'a' is encoded as 1.B
  * and 'b' is encoded as 0.B.
  *
  * This Chisel module implements the Mealy automaton defined in Chapter 2.10
  * (mirroring A10_regLang.sv, A10_loopCLC.sv, and A10_outCLC.sv).
  *
  * States:
  *   - sInit:  Initial state, awaiting first 'a'
  *   - sRunA:  Received at least one 'a'
  *   - sRunB:  Received 'b' following 'a'
  *   - sError: Invalid sequence entered
  *
  * Outputs (Mealy: dependent on current state and input symbol):
  *   - recA (2'b10): successfully recognizes an 'a' in sequence
  *   - recB (2'b01): successfully recognizes a 'b' in sequence
  *   - fail (2'b00): unrecognized input or error transition
  */
object LanguageRecognizer {
  object State extends ChiselEnum {
    val sInit, sRunA, sRunB, sError = Value
  }

  object OutputCode {
    val Fail = 0.U(2.W)
    val RecB = 1.U(2.W)
    val RecA = 2.U(2.W)
  }
}

class LanguageRecognizer extends Module {
  import LanguageRecognizer._

  val io = IO(new Bundle {
    val in    = Input(Bool())         // Input symbol: true.B = 'a', false.B = 'b'
    val out   = Output(UInt(2.W))     // Output recognition code
    val state = Output(State())       // Current state for inspection
  })

  val state = RegInit(State.sInit)
  val nextState = WireDefault(state)
  val outCode = WireDefault(OutputCode.Fail)

  switch(state) {
    is(State.sInit) {
      when(io.in) { // 'a'
        nextState := State.sRunA
        outCode   := OutputCode.RecA
      }.otherwise {
        nextState := State.sError
        outCode   := OutputCode.Fail
      }
    }
    is(State.sRunA) {
      when(io.in) { // 'a'
        nextState := State.sRunA
        outCode   := OutputCode.RecA
      }.otherwise { // 'b'
        nextState := State.sRunB
        outCode   := OutputCode.RecB
      }
    }
    is(State.sRunB) {
      when(!io.in) { // 'b'
        nextState := State.sRunB
        outCode   := OutputCode.RecB
      }.otherwise {  // 'a' after 'b' is invalid in a+ b+
        nextState := State.sError
        outCode   := OutputCode.Fail
      }
    }
    is(State.sError) {
      nextState := State.sError
      outCode   := OutputCode.Fail
    }
  }

  state := nextState
  io.state := state
  io.out := outCode
}
