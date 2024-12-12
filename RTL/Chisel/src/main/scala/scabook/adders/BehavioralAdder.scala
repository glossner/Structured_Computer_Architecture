// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._

class BehavioralAdder[T <: Data](gen: T) extends Adder(gen) {
  io.sum := io.a + io.b
}