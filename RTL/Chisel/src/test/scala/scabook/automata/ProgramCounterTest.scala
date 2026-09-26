// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

// sbt 'testOnly scabook.automata.ProgramCounterTest'

package scabook.automata

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import scabook.automata.ProgramCounter._

class ProgramCounterTest extends AnyFlatSpec {
  "ProgramCounter" should "correctly step sequentially, execute branches, perform JALR with LSB cleared, and stall" in {
    simulate(new ProgramCounter(width = 32, initPC = 0x1000)) { dut =>
      // Reset automaton
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)

      // Initial state: pc should be 0x1000
      assert(dut.io.pc.peek().litValue == 0x1000, "PC must be initialized to 0x1000")

      // Cycle 1: Sequential step (+4)
      dut.io.mode.poke(Mode.Plus4)
      dut.io.branchImm.poke(0.U)
      dut.io.jalrTarget.poke(0.U)
      dut.clock.step()
      assert(dut.io.pc.peek().litValue == 0x1004, "PC must increment by 4 to 0x1004")

      // Cycle 2: Sequential step (+4)
      dut.clock.step()
      assert(dut.io.pc.peek().litValue == 0x1008, "PC must increment by 4 to 0x1008")

      // Cycle 3: Stall (hold PC)
      dut.io.mode.poke(Mode.Stall)
      dut.clock.step()
      assert(dut.io.pc.peek().litValue == 0x1008, "PC must hold at 0x1008 during stall")

      // Cycle 4: Branch forward (+32 bytes = 8 instructions)
      dut.io.mode.poke(Mode.Branch)
      dut.io.branchImm.poke(32.U)
      dut.clock.step()
      assert(dut.io.pc.peek().litValue == (0x1008 + 32), "PC must branch forward to 0x1028")

      // Cycle 5: Branch backward (-16 bytes using 32-bit two's complement)
      val negOffset = (BigInt(1) << 32) - 16
      dut.io.mode.poke(Mode.Branch)
      dut.io.branchImm.poke(negOffset.U(32.W))
      dut.clock.step()
      assert(dut.io.pc.peek().litValue == (0x1028 - 16), "PC must branch backward to 0x1018")

      // Cycle 6: JALR to address 0x2005 (odd address: LSB must be masked to 0x2004)
      dut.io.mode.poke(Mode.Jalr)
      dut.io.jalrTarget.poke(0x2005.U)
      dut.clock.step()
      assert(dut.io.pc.peek().litValue == 0x2004, "JALR must clear least-significant bit (0x2005 -> 0x2004)")

      // Cycle 7: Resume sequential execution from JALR target
      dut.io.mode.poke(Mode.Plus4)
      dut.clock.step()
      assert(dut.io.pc.peek().litValue == 0x2008, "PC must advance sequentially to 0x2008 following JALR")
    }
  }
}
