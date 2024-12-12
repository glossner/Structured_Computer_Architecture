// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.util._

class RippleCarryAdder[T <: Data](gen: T, width: Int) extends Adder(gen) {
  val carries = Wire(Vec(width + 1, Bool()))
  val sumBits = Wire(Vec(width, Bool()))

  carries(0) := false.B // Initial carry-in is 0

  for (i <- 0 until width) {
    val sum = io.a(i) ^ io.b(i) ^ carries(i) // Sum bit
    val carry = (io.a(i) & io.b(i)) | (io.a(i) & carries(i)) | (io.b(i) & carries(i)) // Carry bit
    sumBits(i) := sum
    carries(i + 1) := carry
  }

  io.sum := sumBits.asUInt.asTypeOf(gen) // Ensure output type matches input type
  io.carryOut := carries(width)         // Final carry-out bit
}