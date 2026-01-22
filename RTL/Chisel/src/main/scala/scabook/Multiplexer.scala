// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook

import chisel3._
import chisel3.util._

class Multiplexer(val n: Int, val width: Int) extends Module {
  require(n > 0, "The number of inputs must be greater than zero.")
  require(isPow2(n), "The number of inputs must be a power of two.")

  val io = IO(new Bundle {
    val inputs = Input(Vec(n, UInt(width.W))) // `n` inputs of type `UInt`
    val select = Input(UInt(log2Ceil(n).W))  // Select line
    val output = Output(UInt(width.W))       // Output of type `UInt`
  })

  io.output := io.inputs(io.select) // Select which input
}

// Companion object to simplify instantiation
object Multiplexer {
  def apply(inputs: Vec[UInt], select: UInt): UInt = {
    require(inputs.nonEmpty, "Inputs cannot be empty.")
    val mux = Module(new Multiplexer(inputs.length, inputs(0).getWidth))
    mux.io.inputs := inputs
    mux.io.select := select
    mux.io.output
  }
}
