// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.adders

import chisel3._

class BehavioralAdder(width: Int) extends Adder(width) {
  // Perform addition and truncate to width
  io.sum := (io.a + io.b)(width - 1, 0)
}

// Companion object for easier instantiation
object BehavioralAdder {
  def apply(a: UInt, b: UInt, width: Int): UInt = {
    val adder = Module(new BehavioralAdder(width))
    adder.io.a := a
    adder.io.b := b
    adder.io.sum
  }
}
