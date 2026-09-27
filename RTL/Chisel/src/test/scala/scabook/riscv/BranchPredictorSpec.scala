// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BranchPredictorSpec extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "BranchPredictor (BTFN)"

  it should "predict Not-Taken on cold miss and backward taken after warmup" in {
    test(new BranchPredictor(xlen = 32, predictorType = "btfn", btbEntries = 16)) { dut =>
      // 1. Initial cold query at loop branch 0x1020
      dut.io.pc.poke("h1020".U)
      dut.io.updateValid.poke(false.B)
      dut.clock.step(1)

      // Cold miss -> Not Taken
      dut.io.predTaken.expect(false.B)
      dut.io.predTarget.expect("h1024".U)

      // 2. Resolve backward branch in EX: branch at 0x1020 taken to 0x1000 (loop head)
      dut.io.updateValid.poke(true.B)
      dut.io.updateIsBr.poke(true.B)
      dut.io.updateIsJal.poke(false.B)
      dut.io.updatePC.poke("h1020".U)
      dut.io.actualTaken.poke(true.B)
      dut.io.actualTarget.poke("h1000".U)
      dut.clock.step(1)

      // 3. Query 0x1020 again -> BTB hit and backward (0x1000 < 0x1020) -> PREDICT TAKEN!
      dut.io.updateValid.poke(false.B)
      dut.io.pc.poke("h1020".U)
      dut.clock.step(1)

      dut.io.predTaken.expect(true.B)
      dut.io.predTarget.expect("h1000".U)

      // 4. Test forward branch: branch at 0x1004 taken to 0x1040 (0x1040 >= 0x1004)
      dut.io.updateValid.poke(true.B)
      dut.io.updateIsBr.poke(true.B)
      dut.io.updateIsJal.poke(false.B)
      dut.io.updatePC.poke("h1004".U)
      dut.io.actualTaken.poke(true.B)
      dut.io.actualTarget.poke("h1040".U)
      dut.clock.step(1)

      // Query 0x1004 -> BTB hit but forward -> PREDICT NOT TAKEN!
      dut.io.updateValid.poke(false.B)
      dut.io.pc.poke("h1004".U)
      dut.clock.step(1)

      dut.io.predTaken.expect(false.B)
      dut.io.predTarget.expect("h1008".U)
    }
  }

  behavior of "BranchPredictor (Gshare)"

  it should "dynamically train 2-bit counters and track global history in a loop" in {
    test(new BranchPredictor(xlen = 32, predictorType = "gshare", btbEntries = 16, historyWidth = 4)) { dut =>
      // 1. Initial cold query at loop branch 0x2000
      dut.io.pc.poke("h2000".U)
      dut.io.updateValid.poke(false.B)
      dut.clock.step(1)

      // Cold miss -> Not Taken
      dut.io.predTaken.expect(false.B)

      // 2. Execute loop: branch 0x2000 is taken 6 times back to 0x3000
      // This fills the BTB and drives GHR to 0xF (all 1s)
      for (_ <- 0 until 6) {
        val h = dut.io.predHistory.peek().litValue
        dut.io.updateValid.poke(true.B)
        dut.io.updateIsBr.poke(true.B)
        dut.io.updateIsJal.poke(false.B)
        dut.io.updatePC.poke("h2000".U)
        dut.io.actualTaken.poke(true.B)
        dut.io.actualTarget.poke("h3000".U)
        dut.io.prevHistory.poke(h.U)
        dut.clock.step(1)

        dut.io.updateValid.poke(false.B)
        dut.io.pc.poke("h2000".U)
        dut.clock.step(1)
      }

      // At steady state in the loop, GHR is 0xF, counter at (0 ^ 0xF) is Strongly Taken (3)
      dut.io.predHistory.expect("hF".U)
      dut.io.predTaken.expect(true.B)
      dut.io.predTarget.expect("h3000".U)

      // 3. Now loop exits: branch 0x2000 is NOT taken repeatedly
      // 4 not-taken branches shift GHR to 0x0
      // 2 more not-taken branches decrement counter at (0 ^ 0) from 2 to 1 to 0
      for (_ <- 0 until 6) {
        val h = dut.io.predHistory.peek().litValue
        dut.io.updateValid.poke(true.B)
        dut.io.updateIsBr.poke(true.B)
        dut.io.updateIsJal.poke(false.B)
        dut.io.updatePC.poke("h2000".U)
        dut.io.actualTaken.poke(false.B)
        dut.io.actualTarget.poke("h3000".U)
        dut.io.prevHistory.poke(h.U)
        dut.clock.step(1)

        dut.io.updateValid.poke(false.B)
        dut.io.pc.poke("h2000".U)
        dut.clock.step(1)
      }

      // With GHR = 0, counter at index 0 is Strongly Not-Taken (0)
      dut.io.predHistory.expect(0.U)
      dut.io.predTaken.expect(false.B)
    }
  }
}
