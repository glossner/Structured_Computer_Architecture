// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

// sbt 'testOnly scabook.riscv.RiscvFetchExecuteTest'

package scabook.riscv

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class RiscvFetchExecuteTest extends AnyFlatSpec {

  // Instruction encoder helpers
  def rType(funct7: Int, rs2: Int, rs1: Int, funct3: Int, rd: Int, opcode: Int = 0x33): BigInt =
    ((BigInt(funct7 & 0x7f) << 25) |
     (BigInt(rs2 & 0x1f) << 20) |
     (BigInt(rs1 & 0x1f) << 15) |
     (BigInt(funct3 & 0x7) << 12) |
     (BigInt(rd & 0x1f) << 7) |
     BigInt(opcode & 0x7f))

  def iType(imm: Int, rs1: Int, funct3: Int, rd: Int, opcode: Int = 0x13): BigInt = {
    val imm12 = BigInt(imm) & 0xfff
    ((imm12 << 20) |
     (BigInt(rs1 & 0x1f) << 15) |
     (BigInt(funct3 & 0x7) << 12) |
     (BigInt(rd & 0x1f) << 7) |
     BigInt(opcode & 0x7f))
  }

  def sType(imm: Int, rs2: Int, rs1: Int, funct3: Int, opcode: Int = 0x23): BigInt = {
    val imm12   = BigInt(imm) & 0xfff
    val imm11_5 = (imm12 >> 5) & 0x7f
    val imm4_0  = imm12 & 0x1f
    ((imm11_5 << 25) |
     (BigInt(rs2 & 0x1f) << 20) |
     (BigInt(rs1 & 0x1f) << 15) |
     (BigInt(funct3 & 0x7) << 12) |
     (imm4_0 << 7) |
     BigInt(opcode & 0x7f))
  }

  def bType(imm: Int, rs2: Int, rs1: Int, funct3: Int, opcode: Int = 0x63): BigInt = {
    val imm13   = BigInt(imm) & 0x1fff
    val b12     = (imm13 >> 12) & 0x1
    val b10_5   = (imm13 >> 5) & 0x3f
    val b4_1    = (imm13 >> 1) & 0xf
    val b11     = (imm13 >> 11) & 0x1
    ((b12 << 31) |
     (b10_5 << 25) |
     (BigInt(rs2 & 0x1f) << 20) |
     (BigInt(rs1 & 0x1f) << 15) |
     (BigInt(funct3 & 0x7) << 12) |
     (b4_1 << 8) |
     (b11 << 7) |
     BigInt(opcode & 0x7f))
  }

  def jType(imm: Int, rd: Int, opcode: Int = 0x6f): BigInt = {
    val imm21   = BigInt(imm) & 0x1fffff
    val b20     = (imm21 >> 20) & 0x1
    val b10_1   = (imm21 >> 1) & 0x3ff
    val b11     = (imm21 >> 11) & 0x1
    val b19_12  = (imm21 >> 12) & 0xff
    ((b20 << 31) |
     (b10_1 << 21) |
     (b11 << 20) |
     (b19_12 << 12) |
     (BigInt(rd & 0x1f) << 7) |
     BigInt(opcode & 0x7f))
  }

  def addi(rd: Int, rs1: Int, imm: Int) = iType(imm, rs1, 0x0, rd, 0x13)
  def add(rd: Int, rs1: Int, rs2: Int)  = rType(0x00, rs2, rs1, 0x0, rd, 0x33)
  def sub(rd: Int, rs1: Int, rs2: Int)  = rType(0x20, rs2, rs1, 0x0, rd, 0x33)
  def mul(rd: Int, rs1: Int, rs2: Int)  = rType(0x01, rs2, rs1, 0x0, rd, 0x33)
  def andi(rd: Int, rs1: Int, imm: Int) = iType(imm, rs1, 0x7, rd, 0x13)
  def sw(rs2: Int, rs1: Int, offset: Int)= sType(offset, rs2, rs1, 0x2, 0x23)
  def lw(rd: Int, rs1: Int, offset: Int) = iType(offset, rs1, 0x2, rd, 0x03)
  def beq(rs1: Int, rs2: Int, offset: Int)= bType(offset, rs2, rs1, 0x0, 0x63)
  def bne(rs1: Int, rs2: Int, offset: Int)= bType(offset, rs2, rs1, 0x1, 0x63)
  def jal(rd: Int, offset: Int)          = jType(offset, rd, 0x6f)
  def jalr(rd: Int, rs1: Int, offset: Int)= iType(offset, rs1, 0x0, rd, 0x67)

  "RiscvFetchExecute" should "execute RV32I base arithmetic and Zmmul in single-cycle Fetch-Execute" in {
    val prog = Seq(
      addi(1, 0, 15),       // PC=0:  x1 = 15
      addi(2, 0, 25),       // PC=4:  x2 = 25
      add(3, 1, 2),         // PC=8:  x3 = x1 + x2 = 40
      sub(4, 3, 1),         // PC=12: x4 = x3 - x1 = 25
      mul(5, 1, 2)          // PC=16: x5 = x1 * x2 = 375 (Zmmul extension)
    )

    simulate(new RiscvSystem(prog)) { dut =>
      dut.reset.poke(true.B); dut.clock.step(); dut.reset.poke(false.B)

      // Cycle 0: PC=0: addi x1, x0, 15
      assert(dut.io.pc.peek().litValue == 0, "PC must be 0")
      assert(dut.io.aluOut.peek().litValue == 15, "x1 ALU out must be 15")
      dut.clock.step()

      // Cycle 1: PC=4: addi x2, x0, 25
      assert(dut.io.pc.peek().litValue == 4, "PC must be 4")
      assert(dut.io.aluOut.peek().litValue == 25, "x2 ALU out must be 25")
      dut.clock.step()

      // Cycle 2: PC=8: add x3, x1, x2 (uses x1=15, x2=25)
      assert(dut.io.pc.peek().litValue == 8, "PC must be 8")
      assert(dut.io.aluOut.peek().litValue == 40, "x3 ALU out must be 40 (15+25)")
      dut.clock.step()

      // Cycle 3: PC=12: sub x4, x3, x1 (uses x3=40, x1=15)
      assert(dut.io.pc.peek().litValue == 12, "PC must be 12")
      assert(dut.io.aluOut.peek().litValue == 25, "x4 ALU out must be 25 (40-15)")
      dut.clock.step()

      // Cycle 4: PC=16: mul x5, x1, x2 (uses x1=15, x2=25)
      assert(dut.io.pc.peek().litValue == 16, "PC must be 16")
      assert(dut.io.aluOut.peek().litValue == 375, "x5 ALU out must be 375 (15*25)")
      dut.clock.step()

      assert(dut.io.pc.peek().litValue == 20, "PC must advance to 20")
    }
  }

  it should "execute branch instructions and redirect PC immediately" in {
    val prog = Seq(
      addi(1, 0, 10),       // PC=0:  x1 = 10
      addi(2, 0, 10),       // PC=4:  x2 = 10
      beq(1, 2, 8),         // PC=8:  if (x1 == x2) jump to PC + 8 = 16
      addi(3, 0, 1),        // PC=12: skipped
      addi(3, 0, 99)        // PC=16: x3 = 99
    )

    simulate(new RiscvSystem(prog)) { dut =>
      dut.reset.poke(true.B); dut.clock.step(); dut.reset.poke(false.B)

      // Cycle 0: PC=0
      assert(dut.io.pc.peek().litValue == 0)
      dut.clock.step()

      // Cycle 1: PC=4
      assert(dut.io.pc.peek().litValue == 4)
      dut.clock.step()

      // Cycle 2: PC=8: BEQ x1, x2, +8
      assert(dut.io.pc.peek().litValue == 8)
      dut.clock.step()

      // Cycle 3: Branch taken -> PC redirects directly to 16
      assert(dut.io.pc.peek().litValue == 16, "PC must branch directly to 16")
      assert(dut.io.aluOut.peek().litValue == 99, "ALU out must be 99 at target")
      dut.clock.step()

      assert(dut.io.pc.peek().litValue == 20)
    }
  }

  it should "execute JAL function call and JALR return sequence" in {
    val prog = Seq(
      addi(1, 0, 5),        // PC=0:  x1 = 5
      jal(1, 8),            // PC=4:  jump to PC+8=12, save link PC+4=8 into x1 (ra)
      addi(2, 0, 111),      // PC=8:  return target
      jalr(2, 1, 0)         // PC=12: jump to x1+0=8, save link PC+4=16 into x2
    )

    simulate(new RiscvSystem(prog)) { dut =>
      dut.reset.poke(true.B); dut.clock.step(); dut.reset.poke(false.B)

      // Cycle 0: PC=0
      assert(dut.io.pc.peek().litValue == 0)
      dut.clock.step()

      // Cycle 1: PC=4: JAL x1, +8
      assert(dut.io.pc.peek().litValue == 4)
      dut.clock.step()

      // Cycle 2: PC=12: JAL target reached, x1 has link address 8
      assert(dut.io.pc.peek().litValue == 12, "PC must be at JAL target 12")
      dut.clock.step()

      // Cycle 3: PC=8: JALR target reached (return to 8)
      assert(dut.io.pc.peek().litValue == 8, "PC must return to 8 via JALR")
      assert(dut.io.aluOut.peek().litValue == 111, "ALU out must be 111 at return point")
      dut.clock.step()

      assert(dut.io.pc.peek().litValue == 12)
    }
  }

  it should "support data memory store and load instructions with unbuffered memory" in {
    val prog = Seq(
      addi(1, 0, 16),       // PC=0:  x1 = 16 (data memory word address)
      addi(2, 0, 0x42),     // PC=4:  x2 = 0x42 (data word)
      sw(2, 1, 0),          // PC=8:  Store Word: mem[16] = 0x42
      lw(3, 1, 0),          // PC=12: Load Word: x3 = mem[16]
      addi(4, 3, 10)        // PC=16: x4 = x3 + 10 = 0x42 + 10 = 76
    )

    simulate(new RiscvSystem(prog)) { dut =>
      dut.reset.poke(true.B); dut.clock.step(); dut.reset.poke(false.B)

      // Cycle 0: PC=0: x1 = 16
      assert(dut.io.pc.peek().litValue == 0)
      dut.clock.step()

      // Cycle 1: PC=4: x2 = 0x42
      assert(dut.io.pc.peek().litValue == 4)
      dut.clock.step()

      // Cycle 2: PC=8: SW x2, 0(x1)
      assert(dut.io.pc.peek().litValue == 8)
      dut.clock.step()

      // Cycle 3: PC=12: LW x3, 0(x1) -> reads 0x42 combinationally
      assert(dut.io.pc.peek().litValue == 12)
      dut.clock.step()

      // Cycle 4: PC=16: ADDI x4, x3, 10 -> x3=0x42, result=76
      assert(dut.io.pc.peek().litValue == 16)
      assert(dut.io.aluOut.peek().litValue == 76, "x4 ALU out must be 0x42 + 10 = 76")
      dut.clock.step()

      assert(dut.io.pc.peek().litValue == 20)
    }
  }
}
