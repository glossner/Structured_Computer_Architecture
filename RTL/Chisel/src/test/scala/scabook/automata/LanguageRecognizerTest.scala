// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

// sbt 'testOnly scabook.automata.LanguageRecognizerTest'

package scabook.automata

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import scabook.automata.LanguageRecognizer._

class LanguageRecognizerTest extends AnyFlatSpec {
  "LanguageRecognizer" should "correctly recognize valid sequences a+ b+ and transition to sError on invalid symbols" in {
    simulate(new LanguageRecognizer) { dut =>
      // Reset automaton
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)

      // In sInit: apply 'a'
      dut.io.in.poke(true.B)
      assert(dut.io.out.peek().litValue == OutputCode.RecA.litValue, "sInit + 'a' must emit recA")
      dut.clock.step()

      // In sRunA: apply second 'a'
      assert(dut.io.state.peek().litValue == State.sRunA.litValue, "must transition to sRunA")
      dut.io.in.poke(true.B)
      assert(dut.io.out.peek().litValue == OutputCode.RecA.litValue, "sRunA + 'a' must emit recA")
      dut.clock.step()

      // In sRunA: apply 'b'
      assert(dut.io.state.peek().litValue == State.sRunA.litValue, "must stay in sRunA on repeated 'a'")
      dut.io.in.poke(false.B)
      assert(dut.io.out.peek().litValue == OutputCode.RecB.litValue, "sRunA + 'b' must emit recB")
      dut.clock.step()

      // In sRunB: apply second 'b'
      assert(dut.io.state.peek().litValue == State.sRunB.litValue, "must transition to sRunB")
      dut.io.in.poke(false.B)
      assert(dut.io.out.peek().litValue == OutputCode.RecB.litValue, "sRunB + 'b' must emit recB")
      dut.clock.step()

      // In sRunB: apply 'a' (illegal in a+ b+)
      assert(dut.io.state.peek().litValue == State.sRunB.litValue, "must stay in sRunB on repeated 'b'")
      dut.io.in.poke(true.B)
      assert(dut.io.out.peek().litValue == OutputCode.Fail.litValue, "sRunB + 'a' must emit fail")
      dut.clock.step()

      // In sError: must stay in sError permanently
      assert(dut.io.state.peek().litValue == State.sError.litValue, "must transition to sError")
      dut.io.in.poke(true.B)
      assert(dut.io.out.peek().litValue == OutputCode.Fail.litValue, "sError must emit fail")
      dut.clock.step()
      assert(dut.io.state.peek().litValue == State.sError.litValue, "sError must be an absorbing state")
    }
  }

  it should "immediately transition from sInit to sError if initial symbol is 'b'" in {
    simulate(new LanguageRecognizer) { dut =>
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)

      dut.io.in.poke(false.B) // 'b' in sInit
      assert(dut.io.out.peek().litValue == OutputCode.Fail.litValue, "sInit + 'b' must emit fail")
      dut.clock.step()

      assert(dut.io.state.peek().litValue == State.sError.litValue, "must transition to sError on initial 'b'")
    }
  }
}
