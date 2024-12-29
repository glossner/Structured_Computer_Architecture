// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.multipliers

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class BehavioralMultiplierTester extends AnyFlatSpec {
  "BehavioralMultiplier" should "correctly compute the product of two inputs" in {
    val testCases = Seq(
      (6, 7, 42),    // 6 * 7 = 42
      (0, 255, 0),   // 0 * 255 = 0
      (15, 15, 225), // 15 * 15 = 225
      (255, 255, 65025) // 255 * 255 = 65025
    )

    simulate(new BehavioralMultiplier(8)) { dut =>
      for ((a, b, expected) <- testCases) {
        // Poke the inputs
        dut.io.a.poke(a.U)
        dut.io.b.poke(b.U)

        // Allow the combinational logic to propagate
        dut.clock.step()

        // Peek the output
        val actual = dut.io.result.peek().litValue
        assert(
          actual == expected,
          s"Error: For inputs a=$a, b=$b, expected $expected, got $actual"
        )
      }
    }
  }
}
