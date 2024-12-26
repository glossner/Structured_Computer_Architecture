// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._

class BehavioralAdderSubtractorHW4 extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(4.W))    // 4-bit unsigned input
    val b = Input(UInt(4.W))    // 4-bit unsigned input
    val subtract = Input(UInt(1.W))
    val sum = Output(UInt(4.W)) // 4-bit unsigned output
  })

  // Directly use the BehavioralAdder module with UInt
  io.sum := BehavioralAdderSubtractorHW(io.a, io.b, io.subtract, 4)
}
