// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class DeMux16Test extends AnyFlatSpec {
  "DeMux16" should "correctly route input to the selected output" in {
    simulate(new DeMux16) { dut =>
      // Test case 1: Route input to output 0
      dut.io.input.poke(42.U)    // Input value
      dut.io.select.poke(0.U)    // Select line chooses output 0
      dut.clock.step()
      assert(dut.io.outputs(0).peek().litValue == 42) // Check output 0
      for (i <- 1 until 16) {
        assert(dut.io.outputs(i).peek().litValue == 0) // All other outputs should be zero
      }

      // Test case 2: Route input to output 15
      dut.io.input.poke(99.U)    // New input value
      dut.io.select.poke(15.U)   // Select line chooses output 15
      dut.clock.step()
      assert(dut.io.outputs(15).peek().litValue == 99) // Check output 15
      for (i <- 0 until 15) {
        assert(dut.io.outputs(i).peek().litValue == 0) // All other outputs should be zero
      }

      // Test case 3: Test with maximum input value
      dut.io.input.poke((BigInt(2).pow(64) - 1).U) // Maximum 64-bit value
      dut.io.select.poke(7.U)                      // Select output 7
      dut.clock.step()
      assert(dut.io.outputs(7).peek().litValue == BigInt(2).pow(64) - 1)
      for (i <- 0 until 16 if i != 7) {
        assert(dut.io.outputs(i).peek().litValue == 0) // All other outputs should be zero
      }
    }
  }
}
