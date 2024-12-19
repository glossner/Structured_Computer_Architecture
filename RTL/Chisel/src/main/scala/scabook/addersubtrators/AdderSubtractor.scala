// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._

abstract class AdderSubtractor[T <: Data](gen: T) extends Module {
  val io = IO(new Bundle {
    val a = Input(gen.cloneType)
    val b = Input(gen.cloneType)
    val result = Output(gen.cloneType)
    val carryIn = Input(Bool())
    val carryOut = Output(Bool())
    val subtract = Input(Bool()) // Control signal for addition/subtraction
  })
}