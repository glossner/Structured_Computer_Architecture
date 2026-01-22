// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook

import chisel3._


class DeMux16 extends Module {
  val io = IO(new Bundle {
    val input = Input(UInt(64.W))      // 64-bit input bus
    val select = Input(UInt(4.W))     // 4-bit selector for 16 outputs
    val outputs = Output(Vec(16, UInt(64.W))) // 16 output buses, each 64-bit
  })

  // Instantiate the 1-to-16 demultiplexer
  io.outputs := Demultiplexer(
    input = io.input,
    select = io.select,
    numOutputs = 16
  )
}