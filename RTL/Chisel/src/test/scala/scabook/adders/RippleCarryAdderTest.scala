// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class RippleCarryAdderTest extends AnyFlatSpec {
  "RippleCarryAdder" should "correctly compute 16-bit addition with carry" in {
    simulate(new RippleCarryAdder(16)) { dut =>
      // Test case 1: Simple addition without carry
      dut.io.a.poke(12345.U)
      dut.io.b.poke(6789.U)
      dut.io.carryIn.poke(0.U)
      dut.io.isSigned.poke(0.U)
      dut.clock.step()
      assert(dut.io.sum.peek().litValue == 12345 + 6789)
      assert(dut.io.carryOut.peek().litValue == 0)

      // Test case 2: Addition with carry-in
      dut.io.a.poke(12345.U)
      dut.io.b.poke(6789.U)
      dut.io.carryIn.poke(1.U)
      dut.io.isSigned.poke(0.U)
      dut.clock.step()
      assert(dut.io.sum.peek().litValue == 12345 + 6789 + 1)
      assert(dut.io.carryOut.peek().litValue == 0)

      // Test case 3: Overflow
      dut.io.a.poke(65535.U) // Maximum 16-bit value
      dut.io.b.poke(1.U)
      dut.io.carryIn.poke(1.U)
      dut.io.isSigned.poke(0.U)
      dut.clock.step()
      assert(dut.io.sum.peek().litValue == 1) 
      assert(dut.io.carryOut.peek().litValue == 1)
    }
  }
}
