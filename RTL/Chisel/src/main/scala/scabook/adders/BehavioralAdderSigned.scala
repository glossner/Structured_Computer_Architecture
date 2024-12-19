// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._


class BehavioralAdderSigned(width: Int) extends Adder(SInt(width.W)) {
 
  val fullSum = Wire(SInt(width.W))

  // Perform the signed addition
  fullSum := io.a + io.b

  // Assign outputs
  io.sum := fullSum

  // Default companion object for signed adders
  object BehavioralAdderSigned {
    def apply(width: Int): BehavioralAdderSigned = {
      Module(new BehavioralAdderSigned(width))
    }
  }
}