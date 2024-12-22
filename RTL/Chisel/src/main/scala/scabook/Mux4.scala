// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

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
