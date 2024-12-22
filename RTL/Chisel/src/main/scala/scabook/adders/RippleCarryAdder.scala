package scabook.adders

import chisel3._
import chisel3.util._

class RippleCarryAdderDualMode(width: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(width.W))       // Input A
    val b = Input(UInt(width.W))       // Input B
    val carryIn = Input(Bool())        // Carry-in
    val isSigned = Input(Bool())       // Control signal: true for signed, false for unsigned
    val sum = Output(UInt(width.W))    // Sum output
    val carryOut = Output(Bool())      // Carry-out for unsigned addition
    val overflow = Output(Bool())      // Overflow for signed addition
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
  io.carryOut := carries(width)     // Carry-out for unsigned addition
  io.overflow := io.isSigned && (carries(width - 1) ^ carries(width)) // Overflow for signed addition
}