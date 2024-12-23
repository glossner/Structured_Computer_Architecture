// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class BehavioralAdderTest extends AnyFlatSpec {
  "BehavioralAdder" should "correctly compute unsigned addition" in {
    simulate(new BehavioralAdder(8)) { dut =>
      // Test case 1: Simple addition
      dut.io.a.poke(10.U)
      dut.io.b.poke(20.U)
      dut.clock.step()
      assert(dut.io.sum.peek().litValue == 30)
 
      // Test case 2: Overflow addition
      dut.io.a.poke(255.U)
      dut.io.b.poke(1.U)
       dut.clock.step()
      assert(dut.io.sum.peek().litValue == 0) // Overflow truncates
    }
  }


}
