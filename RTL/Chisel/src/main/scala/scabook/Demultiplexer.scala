// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._
import chisel3.util._

class Demultiplexer[T <: Data](dataType: T, numOutputs: Int) extends Module {
  require(isPow2(numOutputs), "Number of outputs must be a power of 2")

  val io = IO(new Bundle {
    val input = Input(dataType)
    val select = Input(UInt(log2Ceil(numOutputs).W))
    val outputs = Output(Vec(numOutputs, dataType))
  })

  // Default all outputs to zero
  io.outputs.foreach(_ := 0.U.asTypeOf(dataType))

  // Drive the selected output
  io.outputs(io.select) := io.input
}

// Companion object for easier instantiation
object Demultiplexer {
  def apply[T <: Data](input: T, select: UInt, numOutputs: Int): Vec[T] = {
    val demux = Module(new Demultiplexer(input.cloneType, numOutputs))
    demux.io.input := input
    demux.io.select := select
    demux.io.outputs
  }
}