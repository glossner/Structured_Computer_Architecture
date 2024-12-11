// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._

class BehavioralAdder(width: Int) extends Adder(width) {
  io.sum := io.a + io.b
}
