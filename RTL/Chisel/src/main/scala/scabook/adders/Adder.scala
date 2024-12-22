// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._

abstract class Adder(width: Int) extends Module {
  require(width > 0, "Width must be greater than 0")

  val io = IO(new Bundle {
    val a = Input(UInt(width.W))    // Input A
    val b = Input(UInt(width.W))    // Input B
    val sum = Output(UInt(width.W)) // Output sum
  })
}