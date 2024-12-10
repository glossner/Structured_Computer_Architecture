// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._


class Mux4 extends Module {
  val io = IO(new Bundle {
    val inputs = Input(Vec(4, UInt(8.W)))
    val select = Input(UInt(2.W))
    val output = Output(UInt(8.W))
  })

  io.output := Multiplexer(io.inputs, io.select)
}