// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.util._

class CarryLookAheadAdderDualMode(width: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(width.W))       // Unsigned input A
    val b = Input(UInt(width.W))       // Unsigned input B
    val carryIn = Input(Bool())        // Carry-in
    val isSigned = Input(Bool())       // Control signal: true for signed, false for unsigned
    val sum = Output(UInt(width.W))    // Result (interpreted based on isSigned)
    val carryOut = Output(Bool())      // Carry-out for unsigned addition
    val overflow = Output(Bool())      // Overflow for signed addition
  })

  // Propagate and Generate signals
  val propagate = Wire(Vec(width, Bool()))
  val generate = Wire(Vec(width, Bool()))
  val carry = Wire(Vec(width + 1, Bool())) // Includes the carry-out bit

  carry(0) := io.carryIn // Initial carry-in

  // Compute propagate and generate signals
  for (i <- 0 until width) {
    propagate(i) := io.a(i) ^ io.b(i)
    generate(i) := io.a(i) & io.b(i)
  }

  // Carry computation using Carry Look-Ahead logic
  for (i <- 1 to width) {
    carry(i) := generate(i - 1) | (propagate(i - 1) & carry(i - 1))
  }

  // Sum computation
  val sumBits = Wire(Vec(width, Bool()))
  for (i <- 0 until width) {
    sumBits(i) := propagate(i) ^ carry(i)
  }

  // Outputs
  io.sum := sumBits.asUInt
  io.carryOut := carry(width) // Carry-out for unsigned addition
  io.overflow := io.isSigned && (carry(width - 1) ^ carry(width)) // Overflow for signed addition
}
