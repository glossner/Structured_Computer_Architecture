// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.util._

//  ripple-carry adders inherently operate on individual bits
//  constrain T to types that support indexing
class RippleCarryAdder[T <: Bits](gen: T, width: Int) extends Adder(gen) {
  val carries = Wire(Vec(width + 1, Bool()))
  val sumBits = Wire(Vec(width, Bool()))

  carries(0) := io.carryIn // Initial carry-in

  for (i <- 0 until width) {
    val aBit = io.a(i) // Access individual bits of `a`
    val bBit = io.b(i) // Access individual bits of `b`
    val sum = aBit ^ bBit ^ carries(i) // Sum bit
    val carry = (aBit & bBit) | (aBit & carries(i)) | (bBit & carries(i)) // Carry bit
    sumBits(i) := sum
    carries(i + 1) := carry
  }

  io.sum := sumBits.asUInt.asTypeOf(gen) // Convert sum bits to UInt and then to `gen`
  io.carryOut := carries(width)         // Final carry-out
}
