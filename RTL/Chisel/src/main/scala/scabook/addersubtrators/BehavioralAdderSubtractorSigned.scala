// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._


class BehavioralAdderSubtractorSigned(width: Int) extends AdderSubtractor(SInt(width.W)) {
  val fullResult = Wire(SInt(width.W))

  // Compute two's complement of b if subtracting
  val bEffective = Mux(io.subtract, ~io.b + 1.S, io.b)

  fullResult := io.a + bEffective + io.carryIn.asSInt  //Chisel will autoexpand wires

  // Assign outputs
  io.result := fullResult   //Truncate result back to wire width

  // Detect signed overflow
  io.carryOut := (io.a.head(1) === bEffective.head(1)) && (fullResult.head(1) =/= io.a.head(1))

    // Debugging
  printf(p"BehavioralAdderSubtractorSigned Debug:\n")
  printf(p"  io.a: ${io.a}, io.b: ${io.b}, io.carryIn: ${io.carryIn}, io.subtract: ${io.subtract}\n")
  printf(p"  bEffective: $bEffective, fullResult: $fullResult\n")
  printf(p"  io.result: ${io.result}, io.carryOut: ${io.carryOut}\n")



  // Companion object for signed adders/subtractors
  object BehavioralAdderSubtractorSigned {
    def apply(width: Int): BehavioralAdderSubtractorSigned = {
      Module(new BehavioralAdderSubtractorSigned(width))
    }
  }
}
