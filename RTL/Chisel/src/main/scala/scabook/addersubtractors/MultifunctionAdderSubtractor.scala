// Licensed under the BSD 3-Clause License.
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._

abstract class MultifunctionAdderSubtractor(width: Int) extends Module {
  require(width > 0, "Width must be greater than 0")

  val io = IO(new Bundle {
    val a = Input(UInt(width.W))       // First operand
    val b = Input(UInt(width.W))       // Second operand
    val opcode = Input(UInt(4.W))      // Operation code
    val carryIn = Input(UInt(1.W))     // Optional carry-in (default 0)
    
    val result = Output(UInt(width.W)) // Computed result

    //Flags
    val carryOut = Output(UInt(1.W))  // carry-out
    val overflowFlag = Output(UInt(1.W)) // Overflow flag
    val zeroFlag = Output(UInt(1.W))  // Zero flag
    val negativeFlag = Output(UInt(1.W)) // Negative flag
  })

  // Default implementations for optional signals
  io.carryOut := DontCare 
  io.overflowFlag := DontCare
  io.zeroFlag := DontCare
  io.negativeFlag := DontCare
}
