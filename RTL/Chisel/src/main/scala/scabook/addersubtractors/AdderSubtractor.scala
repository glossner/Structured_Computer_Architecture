// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.addersubtractors

import chisel3._

abstract class AdderSubtractor(width: Int) extends Module {
  require(width > 0, "Width must be greater than 0")

  val io = IO(new Bundle {
    val a = Input(UInt(width.W))       // Input A
    val b = Input(UInt(width.W))       // Input B
    val subtract = Input(UInt(1.W))    // Control signal: 1 for subtraction, 0 for addition
    val result = Output(UInt(width.W)) // Result of the operation
  })
}
