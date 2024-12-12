// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._

class BehavioralAdder[T <: Data](gen: T) extends Adder(gen) {
  val fullSum = Wire(gen.cloneType)
  
  // Perform the addition
  fullSum := io.a +& io.b // Use +& to include carry-out in calculation

  // Assign outputs
  io.sum := fullSum.asTypeOf(gen)
  io.carryOut := fullSum.asUInt >= (1.U << io.a.getWidth).asUInt // Extract carry-out
}