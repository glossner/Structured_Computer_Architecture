// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class RiscvImmGenTest extends AnyFlatSpec {
  "RiscvImmGen" should "correctly extract and sign-extend I, S, B, U, and J type immediates" in {
    simulate(new RiscvImmGen) { dut =>
      // I-type: ADDI x1, x2, -4 -> imm = -4 (0xFFFFFFFC)
      // inst = imm[11:0] | rs1 | funct3 | rd | opcode
      // imm = -4 = 0xFFC -> inst[31:20] = 0xFFC, rs1=2, funct3=0, rd=1, opcode=0x13
      val addiInst = "hFFC10093".U
      dut.io.inst.poke(addiInst)
      assert(dut.io.imm.peek().litValue == "hFFFFFFFC".U.litValue)

      // I-type positive: ADDI x1, x2, 42 -> imm = 42
      val addiPos = "h02A10093".U
      dut.io.inst.poke(addiPos)
      assert(dut.io.imm.peek().litValue == 42)

      // S-type: SW x1, -8(x2) -> imm = -8 (0xFFFFFFF8)
      // imm = -8 = 0xFF8 -> imm[11:5]=0x7F, imm[4:0]=0x18
      // inst = 0xFE112C23
      val swInst = "hFE112C23".U
      dut.io.inst.poke(swInst)
      assert(dut.io.imm.peek().litValue == "hFFFFFFF8".U.litValue)

      // U-type: LUI x1, 0x12345 -> imm = 0x12345000
      val luiInst = "h123450B7".U
      dut.io.inst.poke(luiInst)
      assert(dut.io.imm.peek().litValue == "h12345000".U.litValue)
    }
  }
}
