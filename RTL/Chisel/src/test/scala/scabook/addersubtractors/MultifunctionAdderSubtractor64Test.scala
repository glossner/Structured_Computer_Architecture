// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import scabook.addersubtractors.MultifunctionAdderSubtractor64.Opcode

class MultifunctionAdderSubtractor64Test extends AnyFlatSpec {

  "MultifunctionAdderSubtractor64" should "correctly compute addition and subtraction for all opcodes and handle overflow/underflow with truncation" in {
    simulate(new MultifunctionAdderSubtractor64) { dut =>
      val printDebugInfo = false

      testArithmetic()

      def testOperation(a: BigInt, b: BigInt, opcode: UInt, expected: BigInt, 
                        expectedFlag: Boolean, expectedZeroFlag: Boolean, expectedNegativeFlag: Boolean): Unit = {
        val isSigned = opcode(2).litToBoolean

        if (isSigned) {
          // Handle signed inputs
          dut.io.a.poke(a.S(64.W).asUInt) // Convert signed BigInt to SInt, then to UInt
          dut.io.b.poke(b.S(64.W).asUInt) // Convert signed BigInt to SInt, then to UInt
        } else {
          // Handle unsigned inputs
          dut.io.a.poke(a.U(64.W)) // Ensure BigInt is treated as non-negative
          dut.io.b.poke(b.U(64.W))
        }
        dut.io.opcode.poke(opcode)
        dut.clock.step()
        val result = dut.io.result.peek().litValue
        val carryOut = dut.io.carryOut.peek().litValue == 1 // Convert UInt(1.W) to Boolean by comparing to 1
        val overflowFlag = dut.io.overflowFlag.peek().litValue == 1
        val zeroFlag = dut.io.zeroFlag.peek().litValue == 1
        val negativeFlag = dut.io.negativeFlag.peek().litValue == 1
        if(printDebugInfo) println(s"[ALU64 - Result] -- Sum: $result, Carry: $carryOut, zeroFlag: $zeroFlag, negativeFlag $negativeFlag")
        
        assert(result == expected, s"[ALU64] -- Expected result 0x${expected.toString(16)} but got 0x${result.toString(16)} for opcode $opcode")
        if( isSigned ) {
          assert(overflowFlag == expectedFlag, s"[ALU64] -- Expected Overflow Flag $expectedFlag but got overflowFlag $overflowFlag for opcode= $opcode and isSigned= $isSigned")
        } else {
          assert(carryOut == expectedFlag, s"[ALU64] -- ExpectedFlag $expectedFlag but got carryOut $carryOut for opcode $opcode")
        }

        assert(zeroFlag == expectedZeroFlag, s"[ALU64] -- ExpectedZeroFlag $expectedZeroFlag but got zeroFlag $zeroFlag for opcode $opcode")
      }

    def testArithmetic(): Unit = {
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

        if(printDebugInfo)
        {
          println(s"[ALU64] -- Opcode: $opcode, Signed: $signed, Width: $width")
          println(s"[ALU64] -- a: 4, b: 3")
          println(s"[ALU64] -- ExpectedResult: $expected, ExpectedCarry: 0")
        }
        if (isAdd) testOperation(4, 3, opcode, expected, false, false, false)
        if (isSub) testOperation(4, 3, opcode, expected, false, false, false) 

        
        //Set up corner case values
        val maxValUnsigned: BigInt = (BigInt(1) <<  width) - 1       // Unsigned maximum
        val minValUnsigned: BigInt = 0
        
        val minValNegSigned: BigInt =  -(BigInt(1) << (width - 1))       
        val maxValPosSigned: BigInt =   (BigInt(1) << (width - 1)) - 1
        
        val truncMask = (BigInt(1) << width) - 1

        //#######################
        //# Unsigned Carry/Borrow
        //#######################
        if(!signed)
        {
          // Test carryOut
  
          if (isAdd) {
            val a: BigInt = maxValUnsigned 
            val b: BigInt = 1 // Addend to cause overflow

            val overflowExpected = minValUnsigned
            val overflowCarry = true
            val zeroFlag = true       
            val negativeFlag = false

            if(printDebugInfo)
            {  
              println(s"[ALU64 - Add - Overflow} -- Opcode: $opcode, Signed: $signed, Width: $width")
              println(s"[ALU64 - Add - Overflow] -- MaxValUnsigned: $maxValUnsigned, MinValUnsigned: $minValUnsigned")
              println(s"[ALU64 - Add - Overflow] -- a: $a, b: 1")
              println(s"[ALU64 - Add - Overflow] -- ExpectedResult: $overflowExpected, ExpectedCarry: $overflowCarry")
            }

            testOperation(a, b, opcode, overflowExpected, overflowCarry, zeroFlag, negativeFlag)
          }

          //#######################
          //# Unsigned Borrow
          //#######################
          // Test borrow case
          if (isSub) {
            val a: BigInt = 0 
            val b: BigInt = 1 // Subtrahend to cause a borrow

            val borrowExpectedResult: BigInt = maxValUnsigned // Maximum unsigned value
            val borrowCarry = true 
            val zeroFlag = false
            val negativeFlag = false

            if(printDebugInfo)
            {
              println(s"[ALU64 - Sub - Borrow] -- Opcode: $opcode, Signed: $signed, Width: $width")
              println(s"[ALU64 - Sub - Borrow] -- MaxValUnsigned: $maxValUnsigned, MinVal: $minValUnsigned")
              println(s"[ALU64 - Sub - Borrow] -- a: $a, b: $b")
              println(s"[ALU64 - Sub - Borrow] -- ExpectedResult: ${borrowExpectedResult.toString(16)}, ExpectedCarry: $borrowCarry")
            }

            testOperation(a, b, opcode, borrowExpectedResult, borrowCarry, zeroFlag, negativeFlag)
         }          
        }

        //#######################
        //# Signed Overflow
        //#######################
        if(signed)
        {
          if(isAdd)
          {
            val a: BigInt = maxValPosSigned
            val b: BigInt = 1

            val overflowExpectedResult: BigInt = minValNegSigned.abs  //Result comes back as a UInt
            val overflowExpectedFlag = true
            val zeroFlag = false
            val negativeFlag = true

            if(printDebugInfo)
            {
              println(s"[ALU64 - Add - Overflow] -- Opcode: $opcode, Signed: $signed, Width: $width")
              println(s"[ALU64 - Add - Overflow] -- MaxValPosSigned: $maxValPosSigned, MinValNegSigned: $minValNegSigned")
              println(s"[ALU64 - Add - Overflow] -- a: $a, b: $b")
              println(s"[ALU64 - Add - Overflow] -- ExpectedResult: ${overflowExpectedResult.toString(16)}, overflowExpectedFlag: $overflowExpectedFlag")
            }
           
           testOperation(a, b, opcode, overflowExpectedResult, overflowExpectedFlag, zeroFlag, negativeFlag)
          }
          //#######################
          //# Signed Underflow
          //#######################         
          if (isSub) {
            val a: BigInt = minValNegSigned // Smallest negative number
            val b: BigInt = 1              // Subtract 1

            val underflowExpectedResult: BigInt = maxValPosSigned // Result wraps around to maximum positive value
            val underflowExpectedFlag = true // Overflow flag should be set for signed subtraction underflow
            val zeroFlag = false
            val negativeFlag = false

            if (printDebugInfo) {
              println(s"[ALU64 - Sub - Underflow] -- Opcode: $opcode, Signed: $signed, Width: $width")
              println(s"[ALU64 - Sub - Underflow] -- MaxValPosSigned: $maxValPosSigned, MinValNegSigned: $minValNegSigned")
              println(s"[ALU64 - Sub - Underflow] -- a: $a, b: $b")
              println(s"[ALU64 - Sub - Underflow] -- ExpectedResult: ${underflowExpectedResult.toString(16)}, underflowExpectedFlag: $underflowExpectedFlag")
            }

            testOperation(a, b, opcode, underflowExpectedResult, underflowExpectedFlag, zeroFlag, negativeFlag)
          }          
        }
        
        //#######################
        //# Zero Flag
        //#######################
        if(!signed && isSub) {       
          val a: BigInt = 5 // Fits in all datatypes
          val b: BigInt = 5

          val expectedResult: BigInt = 0
          val expectedCarryFlag: Boolean = false
          val expectedZeroFlag: Boolean = true
          val expectedNegativeFlag: Boolean = false

          if (printDebugInfo) {
            println(s"[ALU64 - Zero Test] -- Opcode: $opcode, Signed: $signed, Width: $width")
            println(s"[ALU64 - Zero Test] -- a: $a, b: $b")
            println(s"[ALU64 - Zero Test] -- ExpectedResult: ${expectedResult.toString(16)}, ZeroFlag: $expectedZeroFlag")
          }

          testOperation(a, b, opcode, expectedResult, expectedCarryFlag, expectedZeroFlag, expectedNegativeFlag)
        }
        if(signed) {       
          val a: BigInt = 5 // Fits in all datatypes
          val b: BigInt = if (isAdd) -5 else 5

          val expectedResult: BigInt = 0
          val expectedCarryFlag: Boolean = false
          val expectedZeroFlag: Boolean = true
          val expectedNegativeFlag: Boolean = false

          if (printDebugInfo) {
            println(s"[ALU64 - Zero Test] -- Opcode: $opcode, Signed: $signed, Width: $width")
            println(s"[ALU64 - Zero Test] -- a: $a, b: $b")
            println(s"[ALU64 - Zero Test] -- ExpectedResult: ${expectedResult.toString(16)}, ZeroFlag: $expectedZeroFlag")
          }

          testOperation(a, b, opcode, expectedResult, expectedCarryFlag, expectedZeroFlag, expectedNegativeFlag)
        }
      }

      }
    }
  }
}
