// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class SevenSegmentDisplayTester extends AnyFlatSpec {
  "SevenSegmentDisplay" should "correctly decode numbers 0-9 to 7-segment representation" in {
    // Expected outputs for each input from 0 to 9
    val expectedOutputs = Seq(
      "b1111110".U(7.W), // 0
      "b0110000".U(7.W), // 1
      "b1101101".U(7.W), // 2
      "b1111001".U(7.W), // 3
      "b0110011".U(7.W), // 4
      "b1011011".U(7.W), // 5
      "b1011111".U(7.W), // 6
      "b1110000".U(7.W), // 7
      "b1111111".U(7.W), // 8
      "b1111011".U(7.W)  // 9
    )

    // Simulate the SevenSegmentDisplay module
    simulate(new SevenSegmentDisplay) { dut =>
      for ((expectedOutput, inputValue) <- expectedOutputs.zipWithIndex) {
        // Use poke() to set input value
        dut.io.binIn.poke(inputValue.U)

        // Allow the combinational logic to propagate
        dut.clock.step()

        // Use peek() to check output value
        val actualOutput = dut.io.segOut.peek().litValue //peek returns BigInt
        val expectedValue = expectedOutput.litValue

        assert(
          actualOutput == expectedValue,
          s"Error: For input $inputValue, expected ${expectedOutput.litValue}, got ${actualOutput}"
        )
      }
    }
  }
}
