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

  "RiscvFetchExecute" should "execute RV32I base arithmetic and Zmmul with zero RAW hazard on back-to-back dependencies" in {
    val prog = Seq(
      addi(1, 0, 15),       // PC=0:  x1 = 15
      addi(2, 0, 25),       // PC=4:  x2 = 25
      add(3, 1, 2),         // PC=8:  x3 = x1 + x2 = 40 (RAW dependency on x1, x2)
      sub(4, 3, 1),         // PC=12: x4 = x3 - x1 = 25 (RAW dependency on x3)
      mul(5, 1, 2)          // PC=16: x5 = x1 * x2 = 375 (Zmmul extension!)
    )

    simulate(new RiscvSystem(prog)) { dut =>
      dut.reset.poke(true.B); dut.clock.step(); dut.reset.poke(false.B)

      // Cycle 1: Pipeline fill (Fetch PC=0)
      dut.clock.step()

      // Cycle 2: Execute PC=0 (ADDI x1, x0, 15)
      assert(dut.io.pc.peek().litValue == 0, "Execute PC must be 0")
      assert(dut.io.aluOut.peek().litValue == 15, "ADDI result must be 15")
      dut.clock.step()

      // Cycle 3: Execute PC=4 (ADDI x2, x0, 25)
      assert(dut.io.pc.peek().litValue == 4, "Execute PC must be 4")
      assert(dut.io.aluOut.peek().litValue == 25, "ADDI result must be 25")
      dut.clock.step()

      // Cycle 4: Execute PC=8 (ADD x3, x1, x2: 15 + 25 = 40) - RAW dependency resolved without bubble!
      assert(dut.io.pc.peek().litValue == 8, "Execute PC must be 8")
      assert(dut.io.aluOut.peek().litValue == 40, "ADD result must be 40")
      dut.clock.step()

      // Cycle 5: Execute PC=12 (SUB x4, x3, x1: 40 - 15 = 25) - Back-to-back RAW resolved!
      assert(dut.io.pc.peek().litValue == 12, "Execute PC must be 12")
      assert(dut.io.aluOut.peek().litValue == 25, "SUB result must be 25")
      dut.clock.step()

      // Cycle 6: Execute PC=16 (MUL x5, x1, x2: 15 * 25 = 375)
      assert(dut.io.pc.peek().litValue == 16, "Execute PC must be 16")
      assert(dut.io.aluOut.peek().litValue == 375, "MUL result must be 375")
    }
  }

  it should "execute branch instructions and flush fetched instruction on branch taken" in {
    val prog = Seq(
      addi(1, 0, 10),      // PC=0:  x1 = 10
      addi(2, 0, 10),      // PC=4:  x2 = 10
      beq(1, 2, 8),        // PC=8:  BEQ x1, x2, +8 (target = 8 + 8 = 16)
      addi(3, 0, 99),      // PC=12: In fetch buffer when branch taken -> MUST BE FLUSHED!
      addi(4, 0, 42)       // PC=16: x4 = 42
    )

    simulate(new RiscvSystem(prog)) { dut =>
      dut.reset.poke(true.B); dut.clock.step(); dut.reset.poke(false.B)

      dut.clock.step() // Cycle 1: Fetch PC=0
      dut.clock.step() // Cycle 2: Execute PC=0 (x1=10)
      dut.clock.step() // Cycle 3: Execute PC=4 (x2=10)

      // Cycle 4: Execute PC=8 (BEQ taken)
      assert(dut.io.pc.peek().litValue == 8, "Execute PC must be 8 for BEQ")
      dut.clock.step()

      // Cycle 5: Flushed bubble (NOP) while target PC=16 is being fetched
      assert(dut.io.inst.peek().litValue == 0x00000013, "Must execute NOP bubble during flush")
      dut.clock.step()

      // Cycle 6: Execute target PC=16
      assert(dut.io.pc.peek().litValue == 16, "Target PC must be 16")
      assert(dut.io.aluOut.peek().litValue == 42, "Target instruction result must be 42")
    }
  }

  it should "execute JAL function call and JALR return sequence" in {
    val prog = Seq(
      jal(1, 8),           // PC=0:  JAL x1, +8 (call subroutine at PC=8, x1 = 4)
      addi(3, 0, 100),     // PC=4:  Return destination: x3 = 100
      addi(2, 0, 77),      // PC=8:  Subroutine body: x2 = 77
      jalr(0, 1, 0)        // PC=12: JALR x0, x1, 0 (return to PC=4)
    )

    simulate(new RiscvSystem(prog)) { dut =>
      dut.reset.poke(true.B); dut.clock.step(); dut.reset.poke(false.B)

      dut.clock.step() // Cycle 1: Fetch PC=0

      // Cycle 2: Execute PC=0 (JAL x1, +8)
      assert(dut.io.pc.peek().litValue == 0, "Execute PC must be 0")
      dut.clock.step()

      // Cycle 3: Discard flush bubble, fetch target PC=8
      dut.clock.step()

      // Cycle 4: Execute Subroutine body at PC=8
      assert(dut.io.pc.peek().litValue == 8, "JAL target must execute at PC=8")
      assert(dut.io.aluOut.peek().litValue == 77, "Subroutine x2 = 77")
      dut.clock.step()

      // Cycle 5: Execute JALR at PC=12
      assert(dut.io.pc.peek().litValue == 12, "JALR must execute at PC=12")
      dut.clock.step()

      // Cycle 6: Discard flush bubble, fetch return target PC=4
      dut.clock.step()

      // Cycle 7: Execute return instruction at PC=4
      assert(dut.io.pc.peek().litValue == 4, "JALR must return to PC=4")
      assert(dut.io.aluOut.peek().litValue == 100, "Returned execution x3 = 100")
    }
  }

  it should "support data memory store and load instructions with interlock" in {
    val prog = Seq(
      addi(1, 0, 42),      // PC=0: x1 = 42
      sw(1, 0, 64),        // PC=4: SW x1, 64(x0) -> store 42 at address 64
      lw(2, 0, 64),        // PC=8: LW x2, 64(x0) -> load from address 64 into x2
      addi(3, 2, 8)        // PC=12: x3 = x2 + 8 = 50 (RAW dependency on loaded x2!)
    )

    simulate(new RiscvSystem(prog)) { dut =>
      dut.reset.poke(true.B); dut.clock.step(); dut.reset.poke(false.B)

      dut.clock.step() // Cycle 1: Fetch PC=0
      dut.clock.step() // Cycle 2: Execute PC=0 (ADDI x1, x0, 42)

      // Cycle 3: Execute PC=4 (SW x1, 64(x0))
      assert(dut.io.pc.peek().litValue == 4, "PC must be 4")
      assert(dut.io.aluOut.peek().litValue == 64, "Effective store address must be 64")
      dut.clock.step()

      // Cycle 4: Execute PC=8 (LW x2, 64(x0)) - initiates load
      assert(dut.io.pc.peek().litValue == 8, "PC must be 8")
      assert(dut.io.aluOut.peek().litValue == 64, "Effective load address must be 64")
      dut.clock.step()

      // Cycle 5: Load writeback cycle (loadStall holds pipeline to receive data)
      dut.clock.step()

      // Cycle 6: Execute PC=12 (ADDI x3, x2, 8) - reads loaded x2=42 + 8 = 50!
      assert(dut.io.pc.peek().litValue == 12, "PC must be 12")
      assert(dut.io.aluOut.peek().litValue == 50, "x3 = x2 (42) + 8 must be 50")
    }
  }
}
