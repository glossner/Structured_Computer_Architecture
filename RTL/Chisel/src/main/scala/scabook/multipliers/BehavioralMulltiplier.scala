// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.multipliers

import chisel3._

class BehavioralMultiplier(width: Int) extends Multiplier(width) {
  io.result := io.a * io.b
}

// Companion object for easier instantiation
object BehavioralMultiplier {
  def apply(a: UInt, b: UInt, width: Int): UInt = {
    val module = Module(new BehavioralMultiplier(width))
    module.io.a := a
    module.io.b := b
    module.io.result
  }
}
