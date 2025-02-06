// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

// sbt 'testOnly scabook.memory.RegFile2R1WSRAMTest'

package scabook.memory

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

/**
  * Unit test for the SRAM-based register file (RegFile2R1WSync).
  *
  * The test simulates a simple 3-stage pipeline:
  *   1. Preload Stage: Write initial values into registers 1 and 2.
  *   2. Read Stage: Set read addresses for registers 1 and 2.
  *      (Because of synchronous reads and the extra RegNext, wait 2 cycles for the outputs.)
  *   3. Execute Stage: Add the two read values.
  *   4. Write-back Stage: Write the computed result into register 3.
  *   5. Verification: Read back register 3 (waiting 2 cycles) and check its value.
  */
class RegFile2R1WSRAMTest extends AnyFlatSpec {
  "RegFile2R1WSync" should "properly handle pipelined operations with synchronous reads" in {
    val width = 32
    val depth = 32
    simulate(new RegFile2R1WSRAM(width, depth)) { dut =>
      
      // --- Preload Stage ---
      // Cycle 0: Write 10 into register 1.
      dut.io.dst1.poke(1.U)
      dut.io.dst1data.poke(10.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()  // Write takes effect on rising edge.
      
      // Cycle 1: Write 20 into register 2.
      dut.io.dst1.poke(2.U)
      dut.io.dst1data.poke(20.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()
      
      // --- Pipeline Stage 1: Read Stage ---
      // Set read addresses for registers 1 and 2.
      dut.io.src1.poke(1.U)
      dut.io.src2.poke(2.U)
      // Disable write during the read stage.
      dut.io.wen.poke(false.B)
      
      // Wait 2 cycles for the synchronous read plus RegNext delay.
      dut.clock.step(2)
      
      // Capture the read values.
      val readData1 = dut.io.src1data.peek().litValue
      val readData2 = dut.io.src2data.peek().litValue
      println(s"Read Stage: reg1 = $readData1, reg2 = $readData2")
      
      // --- Pipeline Stage 2: Dummy Execute Stage ---
      // Compute the result (simply add the two register values).
      val result = readData1 + readData2
      println(s"Execute Stage: result = $result")
      
      // --- Pipeline Stage 3: Write-back Stage ---
      // Write the computed result into register 3.
      dut.io.dst1.poke(3.U)
      dut.io.dst1data.poke(result.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()  // Write happens on this cycle.
      
      // --- Verification Stage ---
      // Set the read address to register 3.
      dut.io.src1.poke(3.U)
      dut.io.wen.poke(false.B)
      
      // Again wait 2 cycles for the read to propagate.
      dut.clock.step(2)
      val reg3Val = dut.io.src1data.peek().litValue
      println(s"Verification Stage: reg3 = $reg3Val")
      
      // Check that register 3 holds the computed result.
      assert(reg3Val == result, s"Expected register 3 to be $result, but got $reg3Val")
    }
  }
}
