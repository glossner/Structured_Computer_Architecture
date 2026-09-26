// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.memory

import chisel3._
import chisel3.util._

/**
  * 2-read, 1-write register file using synchronous memory (SyncReadMem).
  */
class RegFile2R1WSRAM(width: Int = 32, depth: Int = 32) extends Module {
  val addrWidth = log2Ceil(depth)

  val io = IO(new Bundle {
    val src1     = Input(UInt(addrWidth.W))  // Read port 1 address
    val src2     = Input(UInt(addrWidth.W))  // Read port 2 address
    val dst1     = Input(UInt(addrWidth.W))  // Write port address
    val wen      = Input(Bool())             // Write enable
    val dst1data = Input(UInt(width.W))      // Write data
    val src1data = Output(UInt(width.W))     // Read port 1 output
    val src2data = Output(UInt(width.W))     // Read port 2 output
  })

  // Synchronous memory array
  val mem = SyncReadMem(depth, UInt(width.W))

  // Synchronous reads registered for pipeline alignment
  val rdata1 = mem.read(io.src1)
  val rdata2 = mem.read(io.src2)
  io.src1data := RegNext(rdata1, 0.U)
  io.src2data := RegNext(rdata2, 0.U)

  // Write logic: update memory at dst1 when wen is asserted
  when(io.wen) {
    mem.write(io.dst1, io.dst1data)
  }
}
