// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.util._

class RippleCarryAdder(width: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(width.W))       // Input A
    val b = Input(UInt(width.W))       // Input B
    val carryIn = Input(UInt(1.W))     // Carry-in
    val isSigned = Input(UInt(1.W))    // Control signal: 1 for signed, 0 for unsigned
    val sum = Output(UInt(width.W))    // Sum output
    val carryOut = Output(UInt(1.W))   // Carry-out for unsigned addition
    val overflow = Output(UInt(1.W))   // Overflow for signed addition
  })

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

  io.sum := sumBits.asUInt          // Sum output
  io.carryOut := carries(width).asUInt     // Carry-out for unsigned addition
  io.overflow := (io.isSigned.asBool && (carries(width - 1) ^ carries(width))).asUInt // Overflow for signed addition
}

object RippleCarryAdder {
  /**
   * Creates a RippleCarryAdder module with the specified width.
   * 
   * @param width The width of the adder (number of bits for inputs and outputs)
   * @return A new instance of RippleCarryAdder
   */
  def apply(width: Int): RippleCarryAdder = {
    require(width > 0, "Width must be greater than 0")
    Module(new RippleCarryAdder(width))
  }
}