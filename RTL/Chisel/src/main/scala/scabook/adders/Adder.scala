// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._

abstract class Adder[T <: Data](gen: T) extends Module {
    require(width > 0, "Width must be greater than 0")
  val io = IO(new Bundle {
    val a = Input(gen.cloneType)
    val b = Input(gen.cloneType)
    val sum = Output(gen.cloneType)
  })
}

// Dynamic companion object
object Adder {
  def apply[T <: Data](gen: T, constructor: T => Adder[T]): Adder[T] = {
    Module(constructor(gen))
  }
}