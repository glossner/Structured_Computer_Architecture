// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

// sbt 'testOnly scabook.memory.RegFile2R1WVecTest'

package scabook.memory

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec


/**
  * Unit test for the RegFile using the Ephemeral Simulator.
  *
  * This test simulates:
  *  - Preload Stage: Write initial values into registers 1 and 2.
  *  - Pipeline Stage 1 (Read): Read the contents of registers 1 and 2.
  *  - Pipeline Stage 2 (Dummy Execute): Add the two read values.
  *  - Pipeline Stage 3 (Write-back): Write the result into register 3.
  *  - Verification: Read back register 3 and check that it holds the expected value.
  */
class RegFile2R1WVecTest extends AnyFlatSpec {
  "RegFile" should "properly handle pipelined read, execute, and write-back operations" in {
    val width = 32
    val depth = 32
    simulate(new RegFile2R1WVec(width, depth)) { dut =>

      // --- Preload Stage: Write initial values ---
      // Cycle 0: Write 10 to register 1.
      dut.io.dst1.poke(1.U)
      dut.io.dst1data.poke(10.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()

      // Cycle 1: Write 20 to register 2.
      dut.io.dst1.poke(2.U)
      dut.io.dst1data.poke(20.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()

      // --- Pipeline Stage 1: Read Stage ---
      // Set read addresses to get values from registers 1 and 2.
      dut.io.src1.poke(1.U)
      dut.io.src2.poke(2.U)
      // Disable writing during the read stage.
      dut.io.wen.poke(false.B)
      dut.clock.step()

      // Capture the read values.
      val reg1Val = dut.io.src1data.peek().litValue
      val reg2Val = dut.io.src2data.peek().litValue
      println(s"Read Stage: reg1 = $reg1Val, reg2 = $reg2Val")

      // --- Pipeline Stage 2: Dummy Execute Stage ---
      // Simply add the two register values.
      val result = reg1Val + reg2Val
      println(s"Execute Stage: result = $result")

      // --- Pipeline Stage 3: Write-back Stage ---
      // Write the computed result to register 3.
      dut.io.dst1.poke(3.U)
      dut.io.dst1data.poke(result.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()

      // --- Verification Stage ---
      // Read back register 3 to verify the result.
      dut.io.src1.poke(3.U)
      dut.io.wen.poke(false.B)
      dut.clock.step()
      val reg3Val = dut.io.src1data.peek().litValue
      println(s"Verification Stage: reg3 = $reg3Val")
      
      assert(
        reg3Val == result,
        s"Expected register 3 to be $result, but got $reg3Val"
      )
    }
  }
}
