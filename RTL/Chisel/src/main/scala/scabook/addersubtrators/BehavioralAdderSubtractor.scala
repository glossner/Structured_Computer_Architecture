// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._

class BehavioralAdderSubtractor(width: Int) extends AdderSubtractor(width) {
  // Compute addition or subtraction
  val bAdjusted = Mux(io.subtract.asBool, ~io.b + 1.U, io.b) // Two's complement for subtraction
  val fullResult = io.a + bAdjusted

  // Truncate result to the specified width
  io.result := fullResult(width - 1, 0)
}

// Companion object for easier instantiation
object BehavioralAderdSubtractor {
  def apply(a: UInt, b: UInt, subtract: UInt, width: Int): UInt = {
    val module = Module(new BehavioralAdderSubtractor(width))
    module.io.a := a
    module.io.b := b
    module.io.subtract := subtract
    module.io.result
  }
}
