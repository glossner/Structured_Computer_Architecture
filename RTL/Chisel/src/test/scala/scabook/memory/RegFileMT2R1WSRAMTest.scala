// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

// sbt 'testOnly scabook.memory.RegFileMT2R1WSRAMTest'

package scabook.memory

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

/**
  * Unit test for the barrel-threaded multithreaded SRAM register file (RegFileMT2R1WSRAM).
  *
  * The test simulates a pipelined operation:
  *
  *  1. Preload Stage:
  *       In thread 0, write 10 to register 1 and 20 to register 2.
  *
  *  2. Read Stage:
  *       Read thread 0 registers 1 and 2 (waiting 2 cycles for the SRAM read and RegNext delay).
  *
  *  3. Execute Stage:
  *       Add the two read values.
  *
  *  4. Write-back Stage:
  *       Write the computed result to thread 0, register 3.
  *
  *  5. Verification Stage:
  *       Read back thread 0, register 3 (waiting 2 cycles) and verify the result.
  *       Also, verify that a different thread (thread 1) shows the default value (0) in register 1.
  */
class RegFileMT2R1WSRAMTest extends AnyFlatSpec {
  "RegFileMT2R1WSRAM" should "properly handle pipelined operations in a barrel-threaded multithreading system" in {
    val width = 32
    val depth = 32
    val threads = 8
    simulate(new RegFileMT2R1WSRAM(width, depth, threads)) { dut =>
      // Define thread IDs.
      val thread0 = 0.U
      val thread1 = 1.U

      // --- Preload Stage ---
      // Cycle 0: Write 10 into thread 0, register 1.
      dut.io.dst1Thread.poke(thread0)
      dut.io.dst1.poke(1.U)
      dut.io.dst1data.poke(10.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()

      // Cycle 1: Write 20 into thread 0, register 2.
      dut.io.dst1Thread.poke(thread0)
      dut.io.dst1.poke(2.U)
      dut.io.dst1data.poke(20.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()

      // --- Pipeline Stage 1: Read Stage ---
      // Read thread 0, register 1 and register 2.
      dut.io.src1Thread.poke(thread0)
      dut.io.src1.poke(1.U)
      dut.io.src2Thread.poke(thread0)
      dut.io.src2.poke(2.U)
      // Disable write.
      dut.io.wen.poke(false.B)
      // Wait 2 cycles for the synchronous read plus RegNext delay.
      dut.clock.step(2)

      // Capture the read values.
      val readData1 = dut.io.src1data.peek().litValue
      val readData2 = dut.io.src2data.peek().litValue
      println(s"Thread 0 Read Stage: reg1 = $readData1, reg2 = $readData2")

      // --- Pipeline Stage 2: Execute Stage ---
      // Compute the result (10 + 20).
      val result = readData1 + readData2
      println(s"Thread 0 Execute Stage: result = $result")

      // --- Pipeline Stage 3: Write-back Stage ---
      // Write the result to thread 0, register 3.
      dut.io.dst1Thread.poke(thread0)
      dut.io.dst1.poke(3.U)
      dut.io.dst1data.poke(result.U)
      dut.io.wen.poke(true.B)
      dut.clock.step() // Write occurs on this cycle.

      // --- Verification Stage ---
      // Read back thread 0, register 3.
      dut.io.src1Thread.poke(thread0)
      dut.io.src1.poke(3.U)
      dut.io.wen.poke(false.B)
      dut.clock.step(2) // Wait 2 cycles for the synchronous read.
      val reg3Val = dut.io.src1data.peek().litValue
      println(s"Thread 0 Verification Stage: reg3 = $reg3Val")
      assert(reg3Val == result, s"Expected thread 0, reg3 to be $result, but got $reg3Val")

      // --- Additional Check ---
      // Verify that thread 1's registers are independent.
      // For thread 1, register 1 should remain at its default value (0).
      dut.io.src1Thread.poke(thread1)
      dut.io.src1.poke(1.U)
      dut.clock.step(2)
      val t1Reg1 = dut.io.src1data.peek().litValue
      println(s"Thread 1 Verification: reg1 = $t1Reg1")
      assert(t1Reg1 == 0, s"Expected thread 1, reg1 to be 0, but got $t1Reg1")
    }
  }
}
