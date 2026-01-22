// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

// sbt 'testOnly scabook.memory.PipelinedSRAMTest'

package scabook.memory

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class PipelinedSRAMTest extends AnyFlatSpec {
  "PipelinedSRAM" should "correctly write and read data with a pipeline delay" in {
    val depth = 16
    val width = 32

    simulate(new PipelinedSRAM(depth, width)) { dut =>

      def write(addr: Int, data: BigInt): Unit = {
        dut.io.addr.poke(addr.U)
        dut.io.dataIn.poke(data.U(width.W))
        dut.io.writeEnable.poke(true.B)
        dut.clock.step(1) // Write happens on this cycle
      }

      def read(addr: Int, expected: BigInt): Unit = {
        dut.io.addr.poke(addr.U)
        dut.io.writeEnable.poke(false.B)
        dut.clock.step(1) // Read issued
        dut.clock.step(1) // Pipelined read data appears

        val actual = dut.io.dataOut.peek().litValue

        assert(
          actual == expected,
          s"Expected: ${expected.toString(16)}, Got: ${actual.toString(16)}, Address: $addr"
        )
      }

      // Test Cases: Write and then read after pipeline delay
      val testCases = Seq(
        (0, BigInt("DEADBEEF", 16)),
        (1, BigInt("12345678", 16)),
        (2, BigInt("CAFEBEBE", 16)),
        (3, BigInt("0", 16)), // Writing zero
        (4, BigInt("FFFFFFFF", 16)) // Writing all ones
      )

      // Perform Writes
      for ((addr, data) <- testCases) {
        write(addr, data)
      }

      // Perform Reads (pipelined, requiring extra clock cycle)
      for ((addr, data) <- testCases) {
        read(addr, data)
      }
    }
  }
}
