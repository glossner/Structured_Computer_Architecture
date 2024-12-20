// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import scabook.addersubtractors.MultifunctionAdder.Opcode

class MultifunctionAdderSubtractorTest extends AnyFlatSpec {

  "MultifunctionAdder" should "correctly compute addition and subtraction for all opcodes and handle overflow/underflow with truncation" in {
    simulate(new MultifunctionAdderSubtractor) { dut =>
      def testOperation(a: BigInt, b: BigInt, opcode: UInt, expected: BigInt, expectedCarry: Boolean): Unit = {
        dut.io.a.poke(a.U(64.W))
        dut.io.b.poke(b.U(64.W))
        dut.io.opcode.poke(opcode)
        dut.clock.step()
        val result = dut.io.result.peek().litValue
        val carryOut = dut.io.carryOut.peek().litToBoolean
        assert(result == expected, s"Expected result $expected but got $result for opcode $opcode")
        assert(carryOut == expectedCarry, s"Expected carryOut $expectedCarry but got $carryOut for opcode $opcode")
      }

      // Test cases for all opcodes
      val opcodes = Seq(
        (Opcode.ADD_U8, false, 8), (Opcode.ADD_U16, false, 16),
        (Opcode.ADD_U32, false, 32), (Opcode.ADD_U64, false, 64),
        (Opcode.SUB_U8, false, 8), (Opcode.SUB_U16, false, 16),
        (Opcode.SUB_U32, false, 32), (Opcode.SUB_U64, false, 64),
        (Opcode.ADD_S8, true, 8), (Opcode.ADD_S16, true, 16),
        (Opcode.ADD_S32, true, 32), (Opcode.ADD_S64, true, 64),
        (Opcode.SUB_S8, true, 8), (Opcode.SUB_S16, true, 16),
        (Opcode.SUB_S32, true, 32), (Opcode.SUB_S64, true, 64)
      )

      for ((opcode, signed, width) <- opcodes) {
 
        val isSub = opcode(3).litToBoolean        
        val isAdd = !isSub


        // Non-corner case: 4 + 3 or 4 - 3
        val expected = if (isSub) (4 - 3)  else (4 + 3)
        testOperation(4, 3, opcode, expected, false)

        // Test overflow
        val minVal = if (signed) -(1 << (width - 1)) else 0
        val maxVal = if (signed) {
        (BigInt(1) << (width - 1)) - 1 // Signed maximum
        } else {
        (BigInt(1) << width) - 1       // Unsigned maximum
        }

        val truncMask = (BigInt(1) << width) - 1 

        if (isAdd) {
          val overflowExpected = if (signed) maxVal + 1 else BigInt(0)  //maxVal+1 because result is returned a UInt
          val overflowCarry = !signed 
            
          println(s"Opcode: $opcode, Signed: $signed, Width: $width")
          println(s"MaxVal: $maxVal, MinVal: $minVal")
          println(s"OverflowExpected: $overflowExpected, OverflowCarry: $overflowCarry")

          testOperation(maxVal, 1, opcode, overflowExpected, overflowCarry)
        }

 
        // FIXME: Test Underflow

      }
    }
  }
}
