// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._
import chisel3.util._

class MultiplexerGenericType[T <: Data](gen: T, val n: Int) extends Module {
  require(n > 0, "The number of inputs must be greater than zero.")
  require(isPow2(n), "The number of inputs must be a power of two.")

  val io = IO(new Bundle {
    val inputs = Input(Vec(n, gen))  // `n` inputs of type `T`
    val select = Input(UInt(log2Ceil(n).W)) // Select line
    val output = Output(gen) 
  })

  io.output := io.inputs(io.select) // Select which input
}

// Companion object to simplify instantiation
object MultiplexerGenericType {
  def apply[T <: Data](inputs: Vec[T], select: UInt): T = {
    val mux = Module(new MultiplexerGenericType(inputs(0).cloneType, inputs.length))
    mux.io.inputs := inputs
    mux.io.select := select
    mux.io.output
  }
}
