// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class Mux4Test extends AnyFlatSpec {
  "Mux4" should "correctly output the selected input" in {
    simulate(new Mux4) { dut =>
      // Set the inputs
      dut.io.inputs(0).poke(10.U(8.W))
      dut.io.inputs(1).poke(20.U(8.W))
      dut.io.inputs(2).poke(30.U(8.W))
      dut.io.inputs(3).poke(40.U(8.W))

      // Test all select lines
      dut.io.select.poke(0.U)
      dut.clock.step()
      dut.io.output.expect(10.U)

      dut.io.select.poke(1.U)
      dut.clock.step()
      dut.io.output.expect(20.U)

      dut.io.select.poke(2.U)
      dut.clock.step()
      dut.io.output.expect(30.U)

      dut.io.select.poke(3.U)
      dut.clock.step()
      dut.io.output.expect(40.U)
    }
  }
}
