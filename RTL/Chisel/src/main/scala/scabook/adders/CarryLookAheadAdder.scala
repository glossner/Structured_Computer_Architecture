// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.util._

class CarryLookAheadAdder(width: Int) extends Adder(width) {
  // Propagate and Generate signals
  val propagate = Wire(Vec(width, Bool()))
  val generate = Wire(Vec(width, Bool()))
  val carry = Wire(Vec(width + 1, Bool())) // Includes the carry out

  carry(0) := false.B // Initial carry-in is zero

  for (i <- 0 until width) {
    propagate(i) := io.a(i) ^ io.b(i)
    generate(i) := io.a(i) & io.b(i)
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
  io.sum := sum.asUInt
  io.carryOut := carry(width)

  // Define abstract method behavior
  override def computeSum(): Unit = {
    // The sum is already computed above as part of the CLA logic
  }
}