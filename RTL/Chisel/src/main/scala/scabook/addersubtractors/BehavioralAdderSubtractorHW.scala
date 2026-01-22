// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.addersubtractors

import chisel3._

class BehavioralAdderSubtractorHW(width: Int) extends Module {
  // Define the I/O for the module
  val io = IO(new Bundle {
    val a = Input(UInt(width.W))       // First operand
    val b = Input(UInt(width.W))       // Second operand
    val subtract = Input(UInt(1.W))    // Control signal: 1 for subtraction, 0 for addition
    val result = Output(UInt(width.W)) // Result of addition or subtraction
  })

  // Two's complement for subtraction
  val bAdjusted = Mux(io.subtract.asBool, ~io.b + 1.U, io.b) 
  val fullResult = io.a + bAdjusted

  // Truncate result to the specified width
  io.result := fullResult(width - 1, 0)
}

// Companion object for easier instantiation
object BehavioralAdderSubtractorHW {
  def apply(a: UInt, b: UInt, subtract: UInt, width: Int): UInt = {
    val module = Module(new BehavioralAdderSubtractor(width))
    module.io.a := a
    module.io.b := b
    module.io.subtract := subtract
    module.io.result
  }
}
