// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.memory

import chisel3._
import chisel3.util._

/**
  * A 2-read, 1-write register file implemented using a synchronous memory (SyncReadMem).
  *
  * The register file contains `depth` registers (default 32) each of `width` bits (default 32).
  *   - `src1` and `src2` are used for the read addresses.
  *   - `src1data` and `src2data` are the synchronous read outputs (available one clock cycle after the read addresses are provided).
  *   - `dst1` is the write address.
  *   - `dst1data` is the write data.
  *   - `wen` is the write enable.
  *
  * Since the processor pipeline guarantees that the writeback stage never coincides with the read stage,
  * we can use a synchronous memory without requiring bypass logic.
  */
class RegFile2R1WSRAM(width: Int = 32, depth: Int = 32) extends Module {
  // Calculate the number of bits needed for addressing.
  val addrWidth = log2Ceil(depth)

  // Define the module IO with the specified port names.
  val io = IO(new Bundle {
    val src1     = Input(UInt(addrWidth.W))  // Read port 1 address.
    val src2     = Input(UInt(addrWidth.W))  // Read port 2 address.
    val dst1     = Input(UInt(addrWidth.W))  // Write port address.
    val wen      = Input(Bool())             // Write enable.
    val dst1data = Input(UInt(width.W))      // Write data.
    val src1data = Output(UInt(width.W))     // Read port 1 output data.
    val src2data = Output(UInt(width.W))     // Read port 2 output data.
  })

  // Create a synchronous memory (SRAM) for the register file.
  val mem = SyncReadMem(depth, UInt(width.W))

  // Synchronous read: note that the read data is available one clock cycle after the address is provided.
  val rdata1 = mem.read(io.src1)
  val rdata2 = mem.read(io.src2)

  // Register the read outputs to align with the pipeline.
  io.src1data := RegNext(rdata1, 0.U)
  io.src2data := RegNext(rdata2, 0.U)

  // Write logic: when io.wen is true, write the value at io.dst1data to the memory at address io.dst1.
  when (io.wen) {
    mem.write(io.dst1, io.dst1data)
  }
}
