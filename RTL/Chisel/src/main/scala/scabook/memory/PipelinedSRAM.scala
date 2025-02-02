// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.memory

import chisel3._
import chisel3.util._

class PipelinedSRAM(depth: Int, width: Int) extends Module {
  val io = IO(new Bundle {
    val addr       = Input(UInt(log2Ceil(depth).W)) // Address input
    val dataIn     = Input(UInt(width.W))          // Data input
    val dataOut    = Output(UInt(width.W))         // Registered data output
    val writeEnable = Input(Bool())                // Write enable
  })

  // Create a synchronous SRAM memory
  val mem = SyncReadMem(depth, UInt(width.W))

  // Register for pipelining the read output
  val readReg = RegNext(mem.read(io.addr, !io.writeEnable))

  // Write operation when writeEnable is asserted
  when(io.writeEnable) {
    mem.write(io.addr, io.dataIn)
  }

  // Registered output for pipeline behavior
  io.dataOut := readReg
}
