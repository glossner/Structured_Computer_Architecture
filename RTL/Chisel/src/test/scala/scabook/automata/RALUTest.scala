// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

// sbt 'testOnly scabook.automata.RALUTest'

package scabook.automata

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import scabook.automata.RALU._

class RALUTest extends AnyFlatSpec {
  "RALU" should "execute register-register, register-immediate operations and write back results to registers" in {
    simulate(new RALU(width = 32)) { dut =>
      // Reset automaton
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)

      // Step 1: Load external data into x1 (val = 50) via memToReg
      dut.io.rdAddr.poke(1.U)
      dut.io.extData.poke(50.U)
      dut.io.memToReg.poke(true.B)
      dut.io.regWrite.poke(true.B)
      dut.clock.step()

      // Step 2: Load external data into x2 (val = 20) via memToReg
      dut.io.rdAddr.poke(2.U)
      dut.io.extData.poke(20.U)
      dut.io.memToReg.poke(true.B)
      dut.io.regWrite.poke(true.B)
      dut.clock.step()

      // Step 3: Compute x3 = x1 + x2 (50 + 20 = 70) using ALU
      dut.io.rs1Addr.poke(1.U)
      dut.io.rs2Addr.poke(2.U)
      dut.io.useImm.poke(false.B)
      dut.io.aluOp.poke(Opcode.ADD)
      dut.io.memToReg.poke(false.B)
      dut.io.rdAddr.poke(3.U)
      dut.io.regWrite.poke(true.B)

      assert(dut.io.rs1Data.peek().litValue == 50, "rs1 must read 50 from x1")
      assert(dut.io.rs2Data.peek().litValue == 20, "rs2 must read 20 from x2")
      assert(dut.io.aluResult.peek().litValue == 70, "ALU addition must produce 70")
      assert(!dut.io.zeroFlag.peek().litToBoolean, "zeroFlag must be false for non-zero result")
      dut.clock.step()

      // Step 4: Compute x4 = x1 - x2 (50 - 20 = 30)
      dut.io.rdAddr.poke(4.U)
      dut.io.aluOp.poke(Opcode.SUB)
      assert(dut.io.aluResult.peek().litValue == 30, "ALU subtraction must produce 30")
      dut.clock.step()

      // Step 5: Compute x5 = x1 + 15 (50 + 15 = 65) using immediate operand
      dut.io.useImm.poke(true.B)
      dut.io.immVal.poke(15.U)
      dut.io.aluOp.poke(Opcode.ADD)
      dut.io.rdAddr.poke(5.U)
      assert(dut.io.aluResult.peek().litValue == 65, "Immediate addition must produce 65")
      dut.clock.step()

      // Step 6: Verify readback of computed results from register file
      dut.io.regWrite.poke(false.B)
      dut.io.rs1Addr.poke(3.U) // x3
      dut.io.rs2Addr.poke(5.U) // x5
      assert(dut.io.rs1Data.peek().litValue == 70, "x3 must hold stored sum 70")
      assert(dut.io.rs2Data.peek().litValue == 65, "x5 must hold stored sum 65")

      // Step 7: Subtraction resulting in zero: x6 = x3 - 70 -> zeroFlag must be true
      dut.io.rs1Addr.poke(3.U)
      dut.io.useImm.poke(true.B)
      dut.io.immVal.poke(70.U)
      dut.io.aluOp.poke(Opcode.SUB)
      assert(dut.io.aluResult.peek().litValue == 0, "70 - 70 must equal 0")
      assert(dut.io.zeroFlag.peek().litToBoolean, "zeroFlag must be asserted for 0 result")

      // Step 8: Verify x0 invariance
      dut.io.rdAddr.poke(0.U)
      dut.io.extData.poke(999.U)
      dut.io.memToReg.poke(true.B)
      dut.io.regWrite.poke(true.B)
      dut.clock.step()

      dut.io.regWrite.poke(false.B)
      dut.io.rs1Addr.poke(0.U)
      assert(dut.io.rs1Data.peek().litValue == 0, "x0 must strictly remain 0")
    }
  }
}
