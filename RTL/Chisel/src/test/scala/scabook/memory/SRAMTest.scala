// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

// sbt 'testOnly scabook.memory.SRAMTest'

package scabook.memory

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class SRAMTest extends AnyFlatSpec {
  "SRAM" should "correctly read and write data" in {
    val depth = 16
    val width = 32

    simulate(new SRAM(depth, width)) { dut =>

      def writeAndRead(addr: Int, data: BigInt, expected: BigInt): Unit = {
        // Perform Write
        dut.io.addr.poke(addr.U)
        dut.io.dataIn.poke(data.U(width.W))
        dut.io.writeEnable.poke(true.B)
        dut.clock.step(1)

        // Disable write for read operation
        dut.io.writeEnable.poke(false.B)
        dut.clock.step(1)

        // Read Back
        dut.io.addr.poke(addr.U)
        dut.clock.step(1)

        val actual = dut.io.dataOut.peek().litValue

        assert(
          actual == expected,
          s"Expected: ${expected.toString(16)}, Got: ${actual.toString(16)}, Address: $addr"
        )
      }

      // Write and Read Test Cases
      val testCases = Seq(
        (0, BigInt("DEADBEEF", 16), BigInt("DEADBEEF", 16)),
        (1, BigInt("12345678", 16), BigInt("12345678", 16)),
        (2, BigInt("CAFEBEBE", 16), BigInt("CAFEBEBE", 16)),
        (3, BigInt("0", 16), BigInt("0", 16)), // Writing zero
        (4, BigInt("FFFFFFFF", 16), BigInt("FFFFFFFF", 16)) // Writing all ones
      )


      for ((addr, data, expected) <- testCases) {
        writeAndRead(addr, data, expected)
      }
    }
  }
}
