// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

// sbt 'testOnly scabook.memory.RegisterTest'

package scabook.memory

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class RegisterTest extends AnyFlatSpec {
  "Register" should "properly store and update values" in {
    val width = 32
    simulate(new Register(width)) { dut =>

      def testRegister(
        d: BigInt,
        enable: Boolean,
        reset: Boolean,
        expected: BigInt
      ): Unit = {
        dut.io.d.poke(d.U(width.W))
        dut.io.enable.poke(enable.B)
        dut.io.reset.poke(reset.B)

        dut.clock.step()

        val actual = dut.io.q.peek().litValue

        assert(
          actual == expected,
          s"Expected: ${expected.toString(16)}, Got: ${actual.toString(16)}, D: ${d.toString(16)}, Enable: $enable, Reset: $reset"
        )
      }

      // Test cases for the register
      val testCases = Seq(
        (0, false, true, 0),   // Reset active, should always be 0
        (42, false, false, 0), // Enable low, value should not change
        (42, true, false, 42), // Enable high, value should update
        (99, false, false, 42),// Enable low, should retain last value
        (99, true, false, 99)  // Enable high, should update
      )

      for ((d, enable, reset, expected) <- testCases) {
        testRegister(d, enable, reset, expected)
      }
    }
  }
}
