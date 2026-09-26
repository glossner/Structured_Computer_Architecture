// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

// sbt 'testOnly scabook.memory.RiscvRegFileTest'

package scabook.memory

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

/**
  * Unit test for RiscvRegFile using EphemeralSimulator.
  *
  * Verifies:
  *  1. Hardwired x0 semantics: x0 reads 0, and writes to x0 are discarded.
  *  2. Standard read/write operations across general-purpose registers (x1-x31).
  *  3. Simultaneous dual combinational reads on rs1 and rs2.
  *  4. Write-enable gating (wen = false preserves stored data).
  */
class RiscvRegFileTest extends AnyFlatSpec {
  "RiscvRegFile" should "enforce x0 hardwired to 0 and support dual reads and synchronous writes" in {
    simulate(new RiscvRegFile(32)) { dut =>
      // Pulse reset to initialize RegInit registers
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)

      // Initial check: x0 and general registers read 0
      dut.io.rs1.poke(0.U)
      dut.io.rs2.poke(1.U)
      dut.io.wen.poke(false.B)
      assert(dut.io.rs1_data.peek().litValue == 0, "x0 must read 0 initially")
      assert(dut.io.rs2_data.peek().litValue == 0, "x1 must read 0 initially")

      // Attempt write to x0: should be discarded
      dut.io.rd.poke(0.U)
      dut.io.rd_data.poke(0xDEADBEEFL.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()

      dut.io.wen.poke(false.B)
      dut.io.rs1.poke(0.U)
      assert(dut.io.rs1_data.peek().litValue == 0, "x0 must still read 0 after write attempt")

      // Write distinct values to x1 and x2
      dut.io.rd.poke(1.U)
      dut.io.rd_data.poke(42.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()

      dut.io.rd.poke(2.U)
      dut.io.rd_data.poke(100.U)
      dut.io.wen.poke(true.B)
      dut.clock.step()

      // Disable write and verify dual simultaneous reads
      dut.io.wen.poke(false.B)
      dut.io.rs1.poke(1.U)
      dut.io.rs2.poke(2.U)
      assert(dut.io.rs1_data.peek().litValue == 42, "rs1 should read 42 from x1")
      assert(dut.io.rs2_data.peek().litValue == 100, "rs2 should read 100 from x2")

      // Verify that wen=false does not overwrite
      dut.io.rd.poke(1.U)
      dut.io.rd_data.poke(999.U)
      dut.io.wen.poke(false.B)
      dut.clock.step()

      dut.io.rs1.poke(1.U)
      assert(dut.io.rs1_data.peek().litValue == 42, "x1 must retain value when wen is low")

      // Verify boundary register x31
      dut.io.rd.poke(31.U)
      dut.io.rd_data.poke(0xCAFEBABEL.U(32.W))
      dut.io.wen.poke(true.B)
      dut.clock.step()

      dut.io.wen.poke(false.B)
      dut.io.rs1.poke(31.U)
      dut.io.rs2.poke(0.U)
      assert(dut.io.rs1_data.peek().litValue == 0xCAFEBABEL, "x31 must store expected value")
      assert(dut.io.rs2_data.peek().litValue == 0, "x0 must remain 0 during simultaneous read with x31")
    }
  }
}
