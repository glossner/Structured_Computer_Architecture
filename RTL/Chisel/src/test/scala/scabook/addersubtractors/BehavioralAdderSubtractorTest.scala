// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class BehavioralAdderSubtractorTest extends AnyFlatSpec {
  "BehavioralAdderSubtractor" should "correctly compute addition and subtraction for corner cases" in {
    simulate(new BehavioralAdderSubtractor(16)) { dut =>
      // Test case 1: Simple addition
      dut.io.a.poke(12345.U)
      dut.io.b.poke(6789.U)
      dut.io.subtract.poke(0.U) // Perform addition
      dut.clock.step()
      assert(dut.io.result.peek().litValue == 12345 + 6789)
 
      // Test case 2: Simple subtraction
      dut.io.a.poke(12345.U)
      dut.io.b.poke(6789.U)
      dut.io.subtract.poke( 1.U) // Perform subtraction
      dut.clock.step()
      assert(dut.io.result.peek().litValue == 12345 - 6789)

      // Test case 3: Subtraction with borrow
      dut.io.a.poke(6789.U)
      dut.io.b.poke(12345.U)
      dut.io.subtract.poke( 1.U)
      dut.clock.step()
      assert(dut.io.result.peek().litValue == (BigInt(1) << 16) + 6789 - 12345) // Wraparound

      // Test case 4: Overflow addition
      dut.io.a.poke(65535.U) // Max 16-bit unsigned value
      dut.io.b.poke(1.U)
      dut.io.subtract.poke( 0.U)
      dut.clock.step()
      assert(dut.io.result.peek().litValue == 0) // Wraparound
    }
  }


}
