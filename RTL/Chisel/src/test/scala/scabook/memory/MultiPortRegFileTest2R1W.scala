// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

// sbt 'testOnly scabook.memory.MultiPortRegFileTest2R1W'

package scabook.memory

import chisel3._
import chisel3.util._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class MultiPortRegFileTest2R1W extends AnyFlatSpec {
  "TwoReadOneWriteRegFile" should "correctly write and read values" in {
    val numRegs = 16
    val width = 32
    val numReadPorts = 2
    val numWritePorts = 1
    val numBanks = 1 // Single bank to avoid complex indexing

    simulate(new MultiPortRegFile(numRegs, width, numReadPorts, numWritePorts, numBanks)) { dut =>

      def write(addr: Int, data: BigInt, writePort: Int = 0): Unit = {
        dut.io.writeAddr(writePort).poke(addr.U)
        dut.io.writeData(writePort).poke(data.U(width.W))
        dut.io.writeEnable(writePort).poke(true.B)
        dut.clock.step(1) // Ensure the write completes
        dut.io.writeEnable(writePort).poke(false.B) // Disable write
      }

      def read(addr: Int, readPort: Int): BigInt = {
        dut.io.readAddr(readPort).poke(addr.U)
        dut.clock.step(1) // Ensure read completes
        dut.io.readData(readPort).peek().litValue
      }

      // Step 1: Initialize all memory locations
      for (addr <- 0 until numRegs) {
        write(addr, 0) // Write `0` to every register
      }
      dut.clock.step(2) // Allow writes to settle

      // Step 2: Define test cases
      val testCases = Seq(
        (2, BigInt("12345678", 16)), // Write 0x12345678 to register 2
        (5, BigInt("DEADBEEF", 16)), // Write 0xDEADBEEF to register 5
        (8, BigInt("CAFEBABE", 16)), // Write 0xCAFEBABE to register 8
        (10, BigInt("0FFFFFFF", 16)) // Write 0x0FFFFFFF to register 10
      )

      // Step 3: Perform Writes
      for ((addr, data) <- testCases) {
        write(addr, data)
      }

      // Step 4: Allow writes to settle before reads
      dut.clock.step(2)

      // Step 5: Perform Reads and Verify
      for ((addr, expected) <- testCases) {
        val actual1 = read(addr, 0) // Read using first port
        val actual2 = read(addr, 1) // Read using second port

        assert(
          actual1 == expected,
          f"ERROR: Expected 0x${expected.toString(16)}, but got 0x${actual1.toString(16)} on read port 0 at address $addr"
        )
        assert(
          actual2 == expected,
          f"ERROR: Expected 0x${expected.toString(16)}, but got 0x${actual2.toString(16)} on read port 1 at address $addr"
        )
      }
    }
  }
}