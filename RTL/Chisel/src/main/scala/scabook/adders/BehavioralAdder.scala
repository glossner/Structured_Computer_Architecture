// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.adders

import chisel3._

class BehavioralAdder(width: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(width.W))    // Input A (UInt)
    val b = Input(UInt(width.W))    // Input B (UInt)
    val sum = Output(UInt(width.W)) // Fixed-width sum output
  })

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
