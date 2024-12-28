// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class SevenSegmentDisplayFlatTester extends AnyFlatSpec {
  "SevenSegmentDisplayFlat" should "correctly decode numbers 0-9 to 7-segment representation" in {
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

    // Simulate the SevenSegmentDisplayFlat module
    simulate(new SevenSegmentDisplayFlat) { dut =>
      val printDebug = false
      for ((expectedOutput, inputValue) <- expectedOutputs.zipWithIndex) {
        // Set the individual bits of the input using poke()
        dut.io.binIn_B0.poke((inputValue & 1) == 1) // Boolean for poke()
        dut.io.binIn_B1.poke(((inputValue >> 1) & 1) == 1)
        dut.io.binIn_B2.poke(((inputValue >> 2) & 1) == 1)
        dut.io.binIn_B3.poke(((inputValue >> 3) & 1) == 1)

        // Allow the combinational logic to propagate
        dut.clock.step()

        // Debugging: Print the bitwise output for verification
        if(printDebug) println(s"Testing input $inputValue")
        val bitValues = Seq(
          dut.io.segOut_a.peek().litValue, // MSB (a)
          dut.io.segOut_b.peek().litValue,
          dut.io.segOut_c.peek().litValue,
          dut.io.segOut_d.peek().litValue,
          dut.io.segOut_e.peek().litValue,
          dut.io.segOut_f.peek().litValue,
          dut.io.segOut_g.peek().litValue  // LSB (g)
        )

        if (printDebug) println(s"Output bits: ${bitValues.mkString(", ")}")

        // Combine the bits into a single value, matching the output order
        val actualOutput = bitValues.zipWithIndex.foldLeft(0L) {
          case (acc, (bit, idx)) => acc | (bit.toLong << (6 - idx)) // Convert bit to Long
        }

        val expectedValue = expectedOutput.litValue
        if (printDebug) println(s"Expected: $expectedValue, Got: $actualOutput")

        // Compare the reconstructed output with the expected value
        assert(
          actualOutput == expectedValue,
          s"Error: For input $inputValue, expected $expectedValue, got $actualOutput"
        )
      }
    }
  }
}
