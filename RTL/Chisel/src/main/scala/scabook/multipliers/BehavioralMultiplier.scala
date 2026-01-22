// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.multipliers

import chisel3._

class BehavioralMultiplier(width: Int) extends Multiplier(width) {
  when(io.isSigned === 1.U) {
    io.result := (io.a.asSInt * io.b.asSInt).asUInt // Signed multiplication
  }.otherwise {
    io.result := io.a * io.b // Unsigned multiplication
  }
}

// Companion object for easier instantiation
object BehavioralMultiplier {
  def apply(a: UInt, b: UInt, isSigned: UInt, width: Int): UInt = {
    val multiplier = Module(new BehavioralMultiplier(width))
    multiplier.io.a := a
    multiplier.io.b := b
    multiplier.io.isSigned := isSigned
    multiplier.io.result
  }
}
