// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class RiscvDecoderTest extends AnyFlatSpec {
  "RiscvDecoder" should "correctly decode fields, control signals, and direct-wire ALU opcodes" in {
    simulate(new RiscvDecoder) { dut =>
      // Test 1: R-type ADD: add x3, x1, x2 (0x002081B3)
      // opcode=0x33, rd=3, funct3=0, rs1=1, rs2=2, funct7=0
      dut.io.inst.poke("h002081B3".U)
      assert(dut.io.decoded.rd.peek().litValue == 3)
      assert(dut.io.decoded.rs1.peek().litValue == 1)
      assert(dut.io.decoded.rs2.peek().litValue == 2)
      assert(dut.io.decoded.control.regWrite.peek().litToBoolean)
      assert(!dut.io.decoded.control.memRead.peek().litToBoolean)
      assert(!dut.io.decoded.control.aluSrc.peek().litToBoolean) // rs2
      assert(dut.io.decoded.control.aluOp.peek().litValue == AluOp.ADD.litValue)

      // Test 2: R-type SUB: sub x4, x1, x2 (0x40208233)
      // funct7[5]=1 -> aluOp must be SUB (0x08)
      dut.io.inst.poke("h40208233".U)
      assert(dut.io.decoded.rd.peek().litValue == 4)
      assert(dut.io.decoded.control.aluOp.peek().litValue == AluOp.SUB.litValue)

      // Test 3: Zmmul MUL: mul x5, x1, x2 (0x022082B3)
      // funct7[0]=1, funct3=0 -> aluOp must be MUL (0x10)
      dut.io.inst.poke("h022082B3".U)
      assert(dut.io.decoded.rd.peek().litValue == 5)
      assert(dut.io.decoded.control.aluOp.peek().litValue == AluOp.MUL.litValue)

      // Test 4: I-type ADDI: addi x6, x1, 15 (0x00F08313)
      dut.io.inst.poke("h00F08313".U)
      assert(dut.io.decoded.rd.peek().litValue == 6)
      assert(dut.io.decoded.imm.peek().litValue == 15)
      assert(dut.io.decoded.control.aluSrc.peek().litToBoolean) // immediate
      assert(dut.io.decoded.control.regWrite.peek().litToBoolean)
      assert(dut.io.decoded.control.aluOp.peek().litValue == AluOp.ADD.litValue)

      // Test 5: S-type SW: sw x1, 12(x2) (0x00112623)
      dut.io.inst.poke("h00112623".U)
      assert(dut.io.decoded.rs1.peek().litValue == 2)
      assert(dut.io.decoded.rs2.peek().litValue == 1)
      assert(dut.io.decoded.control.memWrite.peek().litToBoolean)
      assert(!dut.io.decoded.control.regWrite.peek().litToBoolean)

      // Test 6: I-type LW: lw x7, 8(x2) (0x00812383)
      dut.io.inst.poke("h00812383".U)
      assert(dut.io.decoded.rd.peek().litValue == 7)
      assert(dut.io.decoded.control.memRead.peek().litToBoolean)
      assert(dut.io.decoded.control.memToReg.peek().litToBoolean)
      assert(dut.io.decoded.control.regWrite.peek().litToBoolean)
    }
  }
}
