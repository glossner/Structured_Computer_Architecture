// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._
import chisel3.util._

class Demultiplexer(numOutputs: Int, width: Int) extends Module {
  require(isPow2(numOutputs), "Number of outputs must be a power of 2")

  val io = IO(new Bundle {
    val input = Input(UInt(width.W))                     // Single `UInt` input
    val select = Input(UInt(log2Ceil(numOutputs).W))     // Select signal
    val outputs = Output(Vec(numOutputs, UInt(width.W))) // Vec of `UInt` outputs
  })

  // Default all outputs to zero
  io.outputs.foreach(_ := 0.U)

  // Drive the selected output
  io.outputs(io.select) := io.input
}

// Companion object for easier instantiation
object Demultiplexer {
  def apply(input: UInt, select: UInt, numOutputs: Int): Vec[UInt] = {
    require(numOutputs > 0, "Number of outputs must be greater than zero.")
    require(isPow2(numOutputs), "Number of outputs must be a power of 2.")
    val demux = Module(new Demultiplexer(numOutputs, input.getWidth))
    demux.io.input := input
    demux.io.select := select
    demux.io.outputs
  }
}
