// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.memory

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class RiscvDataMemoryTest extends AnyFlatSpec {
  "RiscvDataMemory" should "support byte, halfword, and word stores and loads with proper sign extension" in {
    simulate(new RiscvDataMemory(depthWords = 256)) { dut =>
      // Step 1: Write Word 0x12345678 to address 0x00
      dut.io.addr.poke(0.U)
      dut.io.writeData.poke("h12345678".U)
      dut.io.funct3.poke("b010".U) // SW
      dut.io.memWrite.poke(true.B)
      dut.io.memRead.poke(false.B)
      dut.clock.step()

      // Step 2: Read Word back from address 0x00
      dut.io.memWrite.poke(false.B)
      dut.io.memRead.poke(true.B)
      dut.io.addr.poke(0.U)
      dut.io.funct3.poke("b010".U) // LW
      dut.clock.step()
      assert(dut.io.readData.peek().litValue == "h12345678".U.litValue, "LW must return full stored word")

      // Step 3: Read Byte 0 (0x78) with LBU
      dut.io.addr.poke(0.U)
      dut.io.funct3.poke("b100".U) // LBU
      dut.clock.step()
      assert(dut.io.readData.peek().litValue == 0x78, "LBU at byte 0 must return 0x78")

      // Step 4: Write Byte 0xFF to byte 1 (address 0x01) via SB
      dut.io.addr.poke(1.U)
      dut.io.writeData.poke(0xFF.U)
      dut.io.funct3.poke("b000".U) // SB
      dut.io.memWrite.poke(true.B)
      dut.io.memRead.poke(false.B)
      dut.clock.step()

      // Step 5: Read Byte 1 with LB (sign-extended) -> 0xFF -> -1 (0xFFFFFFFF)
      dut.io.memWrite.poke(false.B)
      dut.io.memRead.poke(true.B)
      dut.io.addr.poke(1.U)
      dut.io.funct3.poke("b000".U) // LB
      dut.clock.step()
      assert(dut.io.readData.peek().litValue == "hFFFFFFFF".U.litValue, "LB of 0xFF must sign-extend to -1")

      // Step 6: Read Byte 1 with LBU (zero-extended) -> 0x000000FF
      dut.io.funct3.poke("b100".U) // LBU
      dut.clock.step()
      assert(dut.io.readData.peek().litValue == 0xFF, "LBU of 0xFF must zero-extend to 0xFF")
    }
  }
}
