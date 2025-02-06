// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

// sbt 'testOnly scabook.memory.MultiPortRegFileTest'

package scabook.memory

import chisel3._
import chisel3.util._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class RegFileTest8R4W extends AnyFlatSpec {
  "EightReadFourWriteRegFile" should "correctly write and read values with multiple ports" in {
    val numRegs = 64
    val width = 64
    val numReadPorts = 8
    val numWritePorts = 4
    val numBanks = 4

    simulate(new RegFile(numRegs, width, numReadPorts, numWritePorts, numBanks)) { dut =>

      def write(addr: Int, data: BigInt, writePort: Int): Unit = {
        dut.io.writeAddr(writePort).poke(addr.U)
        dut.io.writeData(writePort).poke(data.U(width.W))
        dut.io.writeEnable(writePort).poke(true.B)
        dut.clock.step(1) // Allow write to settle
        dut.io.writeEnable(writePort).poke(false.B) // Disable write
      }

      def read(addr: Int, readPort: Int): BigInt = {
        dut.io.readAddr(readPort).poke(addr.U)
        dut.clock.step(1) // Allow memory read to complete
        dut.io.readData(readPort).peek().litValue
      }

      // Step 1: Disable all writes initially
      for (i <- 0 until numWritePorts) {
        dut.io.writeEnable(i).poke(false.B)
      }
      dut.clock.step(2) // Ensure system is stable

      // Step 2: **Bootstrapping Phase (Testbench Memory Initialization)**
      for (addr <- 0 until numRegs) {
        write(addr, 0, addr % numWritePorts) // Write `0` to every memory location
      }

      // Ensure memory writes settle before performing real tests
      dut.clock.step(2)

      // Step 3: Define multiple test cases (Write + Read)
      val testCases = Seq(
        (5, BigInt("DEADBEEFCAFEBABE", 16)), // Register 5
        (10, BigInt("1234567890ABCDEF", 16)), // Register 10
        (20, BigInt("FEDCBA9876543210", 16)), // Register 20
        (25, BigInt("0FFFFFFFFFFFFFFF", 16))  // Register 25
      )

      // Step 4: Perform Writes (Using Multiple Write Ports)
      for (((addr, data), idx) <- testCases.zipWithIndex) {
        write(addr, data, idx % numWritePorts)
      }

      // Step 5: Allow writes to settle before reads
      dut.clock.step(2)

      // Step 6: Perform Reads and Verify Values
      for (((addr, expected), idx) <- testCases.zipWithIndex) {
        val actual = read(addr, idx % numReadPorts)
        assert(
          actual == expected,
          f"ERROR: Expected 0x${expected.toString(16)}, but got 0x${actual.toString(16)} at address $addr"
        )
      }

      // Step 7: Perform Additional Randomized Read/Write Tests
      val randomTestCases = Seq(
        (8, BigInt("AAAAAAAAAAAAAAAA", 16)), // Register 8
        (16, BigInt("5555555555555555", 16)), // Register 16
        (32, BigInt("ABCDEF1234567890", 16)), // Register 32
        (48, BigInt("1111111111111111", 16))  // Register 48
      )

      for (((addr, data), idx) <- randomTestCases.zipWithIndex) {
        write(addr, data, idx % numWritePorts)
      }

      dut.clock.step(2) // Allow time for writes

      for (((addr, expected), idx) <- randomTestCases.zipWithIndex) {
        val actual = read(addr, idx % numReadPorts)
        assert(
          actual == expected,
          f"ERROR: Expected 0x${expected.toString(16)}, but got 0x${actual.toString(16)} at address $addr"
        )
      }
    }
  }
}
