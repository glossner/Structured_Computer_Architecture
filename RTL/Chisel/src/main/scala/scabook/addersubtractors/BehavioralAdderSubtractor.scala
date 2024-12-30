// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._

class BehavioralAdderSubtractor(width: Int) extends Module {
  // Define the I/O for the module
  val io = IO(new Bundle {
    val a = Input(UInt(width.W))       
    val b = Input(UInt(width.W))       
    val subtract = Input(UInt(1.W))    // 0: add, 1:subtract
    val result = Output(UInt(width.W)) 
  })

  // Compute addition or subtraction
  val fullResult = Mux(io.subtract.asBool, io.a - io.b, io.a + io.b)
  
  // Truncate result to the specified width
  io.result := fullResult(width - 1, 0)
}

// Companion object for easier instantiation
object BehavioralAdderSubtractor {
  def apply(a: UInt, b: UInt, subtract: UInt, width: Int): UInt = {
    val module = Module(new BehavioralAdderSubtractor(width))
    module.io.a := a
    module.io.b := b
    module.io.subtract := subtract
    module.io.result
  }
}
