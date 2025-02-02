// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.memory

import chisel3._
import chisel3.util._

class SRAM(depth: Int, width: Int) extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(log2Ceil(depth).W))
    val dataIn = Input(UInt(width.W))
    val dataOut = Output(UInt(width.W))
    val writeEnable = Input(Bool())
  })

  val mem = SyncReadMem(depth, UInt(width.W)) // Create a synchronous memory

  when(io.writeEnable) {
    mem.write(io.addr, io.dataIn) // Write operation
  }

  io.dataOut := mem.read(io.addr, !io.writeEnable) // Read operation
}