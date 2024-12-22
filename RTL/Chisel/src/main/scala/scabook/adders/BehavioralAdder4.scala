// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._

class BehavioralAdder4 extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(4.W))    // 4-bit unsigned input
    val b = Input(UInt(4.W))    // 4-bit unsigned input
    val sum = Output(UInt(4.W)) // 4-bit unsigned output
  })

  // Directly use the BehavioralAdder module with UInt
  io.sum := BehavioralAdder(io.a, io.b, 4)
}
