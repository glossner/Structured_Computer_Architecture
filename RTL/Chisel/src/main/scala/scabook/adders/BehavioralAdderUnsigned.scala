// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._

class BehavioralAdderUnsigned(width: Int) extends Adder(UInt(width.W)) {
  val fullSum = Wire(UInt(width.W))

  // Perform the unsigned addition with carry-in
  fullSum := io.a + io.b + io.carryIn

  // Assign outputs
  io.sum := fullSum
  io.carryOut := fullSum < io.a || fullSum < io.b

  // Override companion object for unsigned adders
  object BehavioralAdderUnsigned {
    def apply(width: Int): BehavioralAdderUnsigned = {
      Module(new BehavioralAdderUnsigned(width))
    }
  }
}