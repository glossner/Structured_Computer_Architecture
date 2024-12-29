// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.multipliers

import chisel3._

abstract class Multiplier(width: Int) extends Module {
  require(width > 0, "Width must be greater than 0")

  val io = IO(new Bundle {
    val a = Input(UInt(width.W))       // Input A
    val b = Input(UInt(width.W))       // Input B
    val isSigned = Input(UInt(1.W))    // 1 for signed, 0 for unsigned
    val result = Output(UInt((2 * width).W)) // Full precision result
  })
}
