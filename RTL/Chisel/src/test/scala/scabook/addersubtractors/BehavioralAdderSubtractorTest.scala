// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class BehavioralAdderSubtractorTest extends AnyFlatSpec {
  "BehavioralAdderSubtractorUnsigned" should "correctly compute addition and subtraction for corner cases" in {
    simulate(new BehavioralAdderSubtractorUnsigned(16)) { dut =>
      // Test case 1: Simple addition
      dut.io.a.poke(12345.U)
      dut.io.b.poke(6789.U)
      dut.io.subtract.poke(false.B) // Perform addition
      dut.io.carryIn.poke(false.B)
      dut.clock.step()
      assert(dut.io.result.peek().litValue == 12345 + 6789)
      assert(dut.io.carryOut.peek().litValue == 0)

      // Test case 2: Simple subtraction
      dut.io.a.poke(12345.U)
      dut.io.b.poke(6789.U)
      dut.io.subtract.poke(true.B) // Perform subtraction
      dut.io.carryIn.poke(false.B)
      dut.clock.step()
      assert(dut.io.result.peek().litValue == 12345 - 6789)

      // Test case 3: Subtraction with borrow
      dut.io.a.poke(6789.U)
      dut.io.b.poke(12345.U)
      dut.io.subtract.poke(true.B)
      dut.io.carryIn.poke(false.B)
      dut.clock.step()
      assert(dut.io.result.peek().litValue == (BigInt(1) << 16) + 6789 - 12345) // Wraparound
      //assert(dut.io.carryOut.peek().litValue == 1)

      // Test case 4: Overflow addition
      dut.io.a.poke(65535.U) // Max 16-bit unsigned value
      dut.io.b.poke(1.U)
      dut.io.subtract.poke(false.B)
      dut.io.carryIn.poke(false.B)
      dut.clock.step()
      assert(dut.io.result.peek().litValue == 0) // Wraparound
      assert(dut.io.carryOut.peek().litValue == 1)
    }
  }

  "BehavioralAdderSubtractorSigned" should "correctly compute addition and subtraction for corner cases" in {
    simulate(new BehavioralAdderSubtractorSigned(16)) { dut =>
      // Test case 1: Simple addition
      dut.io.a.poke(12345.S)
      dut.io.b.poke(6789.S)
      dut.io.subtract.poke(false.B) // Perform addition
      dut.io.carryIn.poke(false.B)
      dut.clock.step()
      assert(dut.io.result.peek().litValue == 12345 + 6789)

      // Test case 2: Simple subtraction
      dut.io.a.poke(12345.S)
      dut.io.b.poke(6789.S)
      dut.io.subtract.poke(true.B) // Perform subtraction
      dut.io.carryIn.poke(false.B)
      dut.clock.step()
      assert(dut.io.result.peek().litValue == 12345 - 6789)

      // Test case 3: Subtraction with negative result
      dut.io.a.poke(-6789.S)
      dut.io.b.poke(12345.S)
      dut.io.subtract.poke(true.B)
      dut.io.carryIn.poke(false.B)
      dut.clock.step()
      assert(dut.io.result.peek().litValue == -6789 - 12345)

      // Test case 4: Overflow addition
        dut.io.a.poke(32767.S) // Max 16-bit signed value
        dut.io.b.poke(1.S)
        dut.io.subtract.poke(false.B)
        dut.io.carryIn.poke(false.B)
        dut.clock.step()
        assert(dut.io.result.peek().litValue == 0) // Overflow
        assert(dut.io.carryOut.peek().litValue == 1) // Overflow detected

    }
  }
}
