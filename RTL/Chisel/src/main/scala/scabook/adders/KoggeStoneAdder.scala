// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.util._

// Kogge-Stone Adder Implementation
class KoggeStoneAdder[T <: Data](gen: T, width: Int) extends Adder(gen) {

  val a = io.a.asUInt
  val b = io.b.asUInt
  
  val g = Wire(Vec(width, Bool())) // Generate signals
  val p = Wire(Vec(width, Bool())) // Propagate signals
  val c = Wire(Vec(width + 1, Bool())) // Carry signals

  // Initialize generate and propagate signals
  for (i <- 0 until width) {
    g(i) := a(i) & b(i) // Generate: G = A & B
    p(i) := a(i) ^ b(i) // Propagate: P = A ^ B
  }

  // Initialize carry-in
  c(0) := false.B

  // Kogge-Stone parallel prefix computation
  for (stage <- 0 until log2Ceil(width)) {
    for (i <- 0 until width) {
      if (i >= (1 << stage)) {
        val prevG = g(i - (1 << stage))
        val prevP = p(i - (1 << stage))

        g(i) := g(i) | (p(i) & prevG)
        p(i) := p(i) & prevP
      }
    }
  }

  // Compute carry-out for each bit
  for (i <- 0 until width) {
    c(i + 1) := g(i) | (p(i) & c(i))
  }

  // Compute sum
  val sumBits = Wire(Vec(width, Bool()))
  for (i <- 0 until width) {
    sumBits(i) := p(i) ^ c(i)
  }

  io.sum := sumBits.asUInt.asTypeOf(gen)  //Ensure output type == input type
}