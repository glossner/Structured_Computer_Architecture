// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

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
