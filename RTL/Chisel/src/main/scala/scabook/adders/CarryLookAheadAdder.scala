// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.util._

class CarryLookAheadAdder[T <: Data](gen: T, width: Int) extends Adder(gen) {
  require(width > 0, "Width must be greater than 0")

  // Propagate and Generate signals
  val propagate = Wire(Vec(width, Bool()))
  val generate = Wire(Vec(width, Bool()))
  val carry = Wire(Vec(width + 1, Bool())) // Includes the carry-out bit

  carry(0) := false.B // Initial carry-in is zero

  // Compute propagate and generate signals
  for (i <- 0 until width) {
    propagate(i) := io.a.asUInt(i) ^ io.b.asUInt(i)
    generate(i) := io.a.asUInt(i) & io.b.asUInt(i)
  }

  // Carry computation using Carry Look-Ahead logic
  for (i <- 1 to width) {
    carry(i) := generate(i - 1) | (propagate(i - 1) & carry(i - 1))
  }

  // Sum computation
  val sum = Wire(Vec(width, Bool()))
  for (i <- 0 until width) {
    sum(i) := propagate(i) ^ carry(i)
  }

  // Outputs
  io.sum := sum.asUInt.asTypeOf(gen)
  io.carryOut := carry(width)
}