// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class BehavioralAdderTest extends AnyFlatSpec {
  "BehavioralAdderUnsigned" should "correctly compute unsigned addition" in {
    simulate(new BehavioralAdderUnsigned(8)) { dut =>
      // Test case 1: Simple addition
      dut.io.a.poke(10.U)
      dut.io.b.poke(20.U)
      dut.io.carryIn.poke(false.B)
      dut.clock.step()
      assert(dut.io.sum.peek().litValue == 30)
      assert(dut.io.carryOut.peek().litValue == 0)

      // Test case 2: Overflow addition
      dut.io.a.poke(255.U)
      dut.io.b.poke(1.U)
      dut.io.carryIn.poke(true.B)
      dut.clock.step()
      assert(dut.io.sum.peek().litValue == 1) // Overflow wraps around
      assert(dut.io.carryOut.peek().litValue == 1)
    }
  }

  "BehavioralAdderSigned" should "correctly compute signed addition" in {
    simulate(new BehavioralAdderSigned(8)) { dut =>
      // Test case 1: Positive + Positive
      dut.io.a.poke(10.S)
      dut.io.b.poke(20.S)
      dut.clock.step()
      assert(dut.io.sum.peek().litValue == 30)

      // Test case 2: Positive + Negative
      dut.io.a.poke(10.S)
      dut.io.b.poke(-10.S)
      dut.clock.step()
      assert(dut.io.sum.peek().litValue == 0)

      // Test case 3: Negative + Negative
      dut.io.a.poke(-10.S)
      dut.io.b.poke(-20.S)
      dut.clock.step()
      assert(dut.io.sum.peek().litValue == -30)
    }
  }
}
