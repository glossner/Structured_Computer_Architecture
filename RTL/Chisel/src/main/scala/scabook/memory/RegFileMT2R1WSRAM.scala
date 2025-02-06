// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.memory

import chisel3._
import chisel3.util._

/**
  * A barrel-threaded multithreaded 2-read, 1-write register file implemented using SyncReadMem.
  *
  * The register file contains (threads * depth) registers:
  *  - Each thread has a separate register file of size 'depth' (default 32).
  *  - There are two read ports and one write port.
  *
  * Port organization:
  *  - Read Port 1:
  *      - src1Thread: thread ID for read port 1.
  *      - src1: register index for read port 1.
  *      - src1data: read data output.
  *
  *  - Read Port 2:
  *      - src2Thread: thread ID for read port 2.
  *      - src2: register index for read port 2.
  *      - src2data: read data output.
  *
  *  - Write Port:
  *      - dst1Thread: thread ID for the write port.
  *      - dst1: register index for the write port.
  *      - dst1data: data to be written.
  *      - wen: write enable.
  *
  * The effective SRAM address is formed by concatenating the thread ID (most significant bits)
  * with the register index (least significant bits). The SRAM array is sized to (threads * depth).
  *
  * Note: Because the processor pipeline guarantees that the writeback stage never coincides with the read stage,
  * no conflict resolution or bypass logic is required.
  */
class RegFileMT2R1WSRAM(width: Int = 32, depth: Int = 32, threads: Int = 8) extends Module {
  // Compute widths.
  val threadWidth  = log2Ceil(threads)
  val regAddrWidth = log2Ceil(depth)

  // The SRAM memory has a total depth of threads * depth.
  val effectiveDepth = threads * depth

  // Define the module IO.
  val io = IO(new Bundle {
    // Read Port 1
    val src1Thread = Input(UInt(threadWidth.W))  // Thread ID for read port 1.
    val src1       = Input(UInt(regAddrWidth.W)) // Register index for read port 1.
    val src1data   = Output(UInt(width.W))       // Data output for read port 1.
    // Read Port 2
    val src2Thread = Input(UInt(threadWidth.W))  // Thread ID for read port 2.
    val src2       = Input(UInt(regAddrWidth.W)) // Register index for read port 2.
    val src2data   = Output(UInt(width.W))       // Data output for read port 2.
    // Write Port
    val dst1Thread = Input(UInt(threadWidth.W))  // Thread ID for the write port.
    val dst1       = Input(UInt(regAddrWidth.W)) // Register index for the write port.
    val dst1data   = Input(UInt(width.W))        // Data to be written.
    val wen        = Input(Bool())               // Write enable.
  })

  // Create the SRAM register file as a synchronous memory.
  // Memory size is (threads * depth) and each entry is 'width' bits.
  val mem = SyncReadMem(effectiveDepth, UInt(width.W))

  // Form effective addresses by concatenating thread ID (MSBs) and register index (LSBs).
  // Alternatively, one could shift left by regAddrWidth.
  val effective_src1_addr = Cat(io.src1Thread, io.src1)
  val effective_src2_addr = Cat(io.src2Thread, io.src2)
  val effective_dst1_addr = Cat(io.dst1Thread, io.dst1)

  // Synchronous read: the read data is available one cycle after the address is provided.
  val rdata1 = mem.read(effective_src1_addr)
  val rdata2 = mem.read(effective_src2_addr)
  // Use RegNext to align with pipeline timing.
  io.src1data := RegNext(rdata1, 0.U(width.W))
  io.src2data := RegNext(rdata2, 0.U(width.W))

  // Write logic: perform write on the rising clock edge when wen is true.
  when(io.wen) {
    mem.write(effective_dst1_addr, io.dst1data)
  }
}
