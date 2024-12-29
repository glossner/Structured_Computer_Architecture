// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.multipliers

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class BehavioralMultiplierTest extends AnyFlatSpec {
  "BehavioralMultiplier" should "correctly compute signed and unsigned multiplication" in {
    val width = 4
    simulate(new BehavioralMultiplier(width)) { dut =>
      
      def testOperation(
        a: BigInt,
        b: BigInt,
        isSigned: Boolean,
        width: Int,
        expected: BigInt
      ): Unit = {
        // Interpret inputs as unsigned
        val aUInt = if (isSigned) a & ((BigInt(1) << width) - 1) else a
        val bUInt = if (isSigned) b & ((BigInt(1) << width) - 1) else b

        // Interpret expected as unsigned
        val expectedUInt = expected & ((BigInt(1) << (2 * width)) - 1)

        // Simulate the multiplier and test the output
        dut.io.a.poke(aUInt.U(width.W))
        dut.io.b.poke(bUInt.U(width.W))
        dut.io.isSigned.poke(if (isSigned) 1.U(1.W) else 0.U(1.W))
        
        dut.clock.step()

        val actual = dut.io.result.peek().litValue

        assert(
          actual == expectedUInt,
          s"Expected: ${expectedUInt.toString(16)}, Got: ${actual.toString(16)}, A: ${aUInt.toString(16)}, B: ${bUInt.toString(16)}, isSigned: $isSigned"
        )
        }


      val testCases = Seq(
        // Unsigned test cases
        (3, 2, false, 6),      // 3 * 2 = 6
        (15, 15, false, 225),  // 15 * 15 = 225

        // Signed test cases
        (-3, 2, true, -6),      // -3 * 2 = -6
        (-8, -1, true, 8),      // -8 * -1 = 8
        (-4, -4, true, 16),     // -4 * -4 = 16
        (7, -1, true, -7)       // 7 * -1 = -7
      )

      for ((a, b, isSigned, expected) <- testCases) {
        testOperation(a, b, isSigned, width, expected)
      }
    }
  }
}
