// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook

import chisel3._

class Mux4 extends Module {
  val io = IO(new Bundle {
    val inputs = Input(Vec(4, UInt(8.W))) // Four 8-bit inputs
    val select = Input(UInt(2.W))        // 2-bit selector
    val output = Output(UInt(8.W))       // Single 8-bit output
  })

  // Use the Multiplexer companion object for simplicity
  io.output := Multiplexer(io.inputs, io.select)
}
