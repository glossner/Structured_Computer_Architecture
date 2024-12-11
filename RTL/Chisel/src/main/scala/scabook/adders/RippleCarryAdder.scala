// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.util._

class RippleCarryAdder(width: Int) extends Adder(width) {
  val carry = Wire(Vec(width + 1, Bool()))
  carry(0) := false.B

  for (i <- 0 until width) {
    io.sum(i) := io.a(i) ^ io.b(i) ^ carry(i)
    carry(i + 1) := (io.a(i) & io.b(i)) | (carry(i) & (io.a(i) ^ io.b(i)))
  }

  io.carryOut := carry(width)
}
