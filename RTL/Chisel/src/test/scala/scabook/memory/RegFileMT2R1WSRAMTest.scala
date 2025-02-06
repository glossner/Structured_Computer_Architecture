// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.memory

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

/**
  * Unit test for the barrel-threaded multithreaded SRAM register file (RegFileMT2R1WSRAM)
  * using a single thread ID input.
  *
  * The test simulates a pipelined operation:
  *
  *  1. Preload Stage:
  *       With threadID = 0, write 10 to register 1 and 20 to register 2.
  *
  *  2. Read Stage:
  *       With threadID = 0, read registers 1 and 2 (wait 2 cycles for the synchronous read plus RegNext delay).
  *
  *  3. Execute Stage:
  *       Add the two read values.
  *
  *  4. Write-back Stage:
  *       Write the computed result into register 3 (with threadID = 0).
  *
  *  5. Verification Stage:
  *       Read back register 3 (with threadID = 0) and verify the result.
  *       Additionally, switch to threadID = 1 and verify that its register 1 remains at 0.
  */
class RegFileMT2R1WSRAMTest extends AnyFlatSpec {
  "RegFileMT2R1WSRAM" should "properly handle pipelined operations using a single thread ID input" in {
    val width = 32
    val depth = 32
    val threads = 8
    simulate(new RegFileMT2R1WSRAM(width, depth, threads)) { dut =>
      // --- Preload Stage ---
      // Use threadID = 0 for main operations.
      dut.io.threadID.poke(0.U)
      // Cycle 0: Write 10 into thread 0, register 1.
      dut.io.dst1.poke(1.U)
      dut.io.dst1data.poke(10.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()

      // Cycle 1: Write 20 into thread 0, register 2.
      dut.io.dst1.poke(2.U)
      dut.io.dst1data.poke(20.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()

      // --- Pipeline Stage 1: Read Stage ---
      // Set read addresses for thread 0, registers 1 and 2.
      dut.io.src1.poke(1.U)
      dut.io.src2.poke(2.U)
      // Disable writing during the read stage.
      dut.io.wen.poke(false.B)
      // Wait 2 cycles for the SRAM synchronous read plus the RegNext delay.
      dut.clock.step(2)

      // Capture the read values.
      val readData1 = dut.io.src1data.peek().litValue
      val readData2 = dut.io.src2data.peek().litValue
      println(s"Thread 0 Read Stage: reg1 = $readData1, reg2 = $readData2")

      // --- Pipeline Stage 2: Execute Stage ---
      // Compute the result.
      val result = readData1 + readData2
      println(s"Thread 0 Execute Stage: result = $result")

      // --- Pipeline Stage 3: Write-back Stage ---
      // Write the computed result to thread 0, register 3.
      dut.io.dst1.poke(3.U)
      dut.io.dst1data.poke(result.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()  // Write occurs on this cycle.

      // --- Verification Stage ---
      // Read back thread 0, register 3.
      dut.io.src1.poke(3.U)
      dut.io.wen.poke(false.B)
      dut.clock.step(2)  // Wait 2 cycles for the synchronous read.
      val reg3Val = dut.io.src1data.peek().litValue
      println(s"Thread 0 Verification Stage: reg3 = $reg3Val")
      assert(reg3Val == result, s"Expected thread 0, reg3 to be $result, but got $reg3Val")

      // --- Additional Check ---
      // Switch to threadID = 1 and verify that register 1 is still at its default (0).
      dut.io.threadID.poke(1.U)
      dut.io.src1.poke(1.U)
      dut.clock.step(2)
      val t1Reg1 = dut.io.src1data.peek().litValue
      println(s"Thread 1 Verification: reg1 = $t1Reg1")
      assert(t1Reg1 == 0, s"Expected thread 1, reg1 to be 0, but got $t1Reg1")
    }
  }
}
