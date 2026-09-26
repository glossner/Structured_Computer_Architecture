// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class RiscvALUTest extends AnyFlatSpec {
  "RiscvALU" should "correctly compute all RV32I and Zmmul operations with condition flags" in {
    simulate(new RiscvALU(width = 32)) { dut =>
      // Test ADD
      dut.io.opA.poke(25.U)
      dut.io.opB.poke(17.U)
      dut.io.aluOp.poke(AluOp.ADD)
      assert(dut.io.result.peek().litValue == 42)
      assert(!dut.io.zero.peek().litToBoolean)

      // Test SUB
      dut.io.opA.poke(42.U)
      dut.io.opB.poke(42.U)
      dut.io.aluOp.poke(AluOp.SUB)
      assert(dut.io.result.peek().litValue == 0)
      assert(dut.io.zero.peek().litToBoolean)

      // Test SLL
      dut.io.opA.poke(3.U)
      dut.io.opB.poke(4.U)
      dut.io.aluOp.poke(AluOp.SLL)
      assert(dut.io.result.peek().litValue == 48)

      // Test SLT (signed)
      dut.io.opA.poke((-10).S(32.W).asUInt)
      dut.io.opB.poke(5.U)
      dut.io.aluOp.poke(AluOp.SLT)
      assert(dut.io.result.peek().litValue == 1)
      assert(dut.io.lessThan.peek().litToBoolean)

      // Test SLTU (unsigned)
      dut.io.opA.poke((-10).S(32.W).asUInt) // 0xFFFFFFF6 in unsigned is huge
      dut.io.opB.poke(5.U)
      dut.io.aluOp.poke(AluOp.SLTU)
      assert(dut.io.result.peek().litValue == 0)
      assert(!dut.io.lessThanU.peek().litToBoolean)

      // Test XOR, OR, AND
      dut.io.opA.poke("hAA55AA55".U)
      dut.io.opB.poke("hFF00FF00".U)
      dut.io.aluOp.poke(AluOp.XOR)
      assert(dut.io.result.peek().litValue == "h55555555".U.litValue)
      dut.io.aluOp.poke(AluOp.OR)
      assert(dut.io.result.peek().litValue == "hFF55FF55".U.litValue)
      dut.io.aluOp.poke(AluOp.AND)
      assert(dut.io.result.peek().litValue == "hAA00AA00".U.litValue)

      // Test SRL and SRA
      dut.io.opA.poke("h80000000".U)
      dut.io.opB.poke(2.U)
      dut.io.aluOp.poke(AluOp.SRL)
      assert(dut.io.result.peek().litValue == "h20000000".U.litValue)
      dut.io.aluOp.poke(AluOp.SRA)
      assert(dut.io.result.peek().litValue == "hE0000000".U.litValue)

      // Test Zmmul MUL (lower 32 bits)
      dut.io.opA.poke(1234.U)
      dut.io.opB.poke(5678.U)
      dut.io.aluOp.poke(AluOp.MUL)
      assert(dut.io.result.peek().litValue == (1234L * 5678L))

      // Test Zmmul MULH (upper 32 bits signed)
      dut.io.opA.poke((-2).S(32.W).asUInt)
      dut.io.opB.poke(2.U)
      dut.io.aluOp.poke(AluOp.MULH)
      // -2 * 2 = -4 -> 64-bit: 0xFFFFFFFFFFFFFFFC -> high 32 bits is 0xFFFFFFFF (-1)
      assert(dut.io.result.peek().litValue == "hFFFFFFFF".U.litValue)

      // Test Zmmul MULHU (upper 32 bits unsigned)
      dut.io.opA.poke("hFFFFFFFF".U)
      dut.io.opB.poke("hFFFFFFFF".U)
      dut.io.aluOp.poke(AluOp.MULHU)
      // (2^32 - 1)^2 = 2^64 - 2^33 + 1 -> high 32 bits is 2^32 - 2 = 0xFFFFFFFE
      assert(dut.io.result.peek().litValue == "hFFFFFFFE".U.litValue)
    }
  }
}
