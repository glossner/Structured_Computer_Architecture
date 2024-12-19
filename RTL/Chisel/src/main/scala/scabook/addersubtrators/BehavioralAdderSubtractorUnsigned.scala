// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._



class BehavioralAdderSubtractorUnsigned(width: Int) extends AdderSubtractor(UInt(width.W)) {
  val fullResult = Wire(UInt(width.W))

  // Compute two's complement of b if subtracting
  val bEffective = Mux(io.subtract, ~io.b + 1.U, io.b)

  // Perform addition with carry-in
  fullResult := io.a + bEffective + io.carryIn  //Chisel automatically adds a bit

  // Assign outputs
  io.result := fullResult(width - 1, 0) //Truncate to width bits
  io.carryOut := fullResult < io.a || (io.subtract && fullResult < bEffective)

  // Companion object for unsigned adders/subtractors
  object BehavioralAdderSubtractorUnsigned {
    def apply(width: Int): BehavioralAdderSubtractorUnsigned = {
      Module(new BehavioralAdderSubtractorUnsigned(width))
    }
  }
}