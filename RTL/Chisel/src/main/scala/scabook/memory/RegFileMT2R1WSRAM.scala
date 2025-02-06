// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.memory

import chisel3._
import chisel3.util._

/**
  * A barrel-threaded multithreaded 2-read, 1-write register file implemented using SyncReadMem.
  *
  * In this design, a single thread ID (provided via `threadID`) is concatenated with the register index
  * for all read and write ports. The SRAM is implemented as a flat array with a depth of (threads * depth).
  * Each thread has its own independent register space.
  *
  * Ports:
  *   - threadID: The current thread identifier (applied to all accesses).
  *   - src1: Read port 1 register index.
  *   - src1data: Read port 1 output.
  *   - src2: Read port 2 register index.
  *   - src2data: Read port 2 output.
  *   - dst1: Write port register index.
  *   - dst1data: Data to be written.
  *   - wen: Write enable.
  *
  * The effective address is computed by concatenating the threadID (upper bits) with the register index.
  * The synchronous memory read is delayed one cycle and then registered via RegNext to align with the pipeline.
  */
class RegFileMT2R1WSRAM(width: Int = 32, depth: Int = 32, threads: Int = 8) extends Module {
  // Compute address widths.
  val threadWidth  = log2Ceil(threads)
  val regAddrWidth = log2Ceil(depth)
  
  // The total number of registers is threads * depth.
  val effectiveDepth = threads * depth

  // Define the module IO.
  val io = IO(new Bundle {
    // Single thread ID for all accesses.
    val threadID  = Input(UInt(threadWidth.W))
    // Read Port 1.
    val src1      = Input(UInt(regAddrWidth.W))
    val src1data  = Output(UInt(width.W))
    // Read Port 2.
    val src2      = Input(UInt(regAddrWidth.W))
    val src2data  = Output(UInt(width.W))
    // Write Port.
    val dst1      = Input(UInt(regAddrWidth.W))
    val dst1data  = Input(UInt(width.W))
    val wen       = Input(Bool())
  })

  // Create a synchronous memory (SRAM) with the total effective depth.
  val mem = SyncReadMem(effectiveDepth, UInt(width.W))

  // Form effective addresses by concatenating the threadID (MSBs) and the register index (LSBs).
  val effective_src1_addr = Cat(io.threadID, io.src1)
  val effective_src2_addr = Cat(io.threadID, io.src2)
  val effective_dst1_addr = Cat(io.threadID, io.dst1)

  // Synchronous read: the read data is available one cycle after the address is provided.
  val rdata1 = mem.read(effective_src1_addr)
  val rdata2 = mem.read(effective_src2_addr)
  // Use RegNext to align with the pipeline (adding one extra cycle delay).
  io.src1data := RegNext(rdata1, 0.U(width.W))
  io.src2data := RegNext(rdata2, 0.U(width.W))

  // Write logic: on a rising clock edge, if wen is true write the data into the effective address.
  when (io.wen) {
    mem.write(effective_dst1_addr, io.dst1data)
  }
}
