// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.memory

import chisel3._
import chisel3.util._


/**
  * A 2-read, 1-write register file.
  *
  *   The register file contains `depth` registers (default 32) each of `width` bits (default 32).
  *   src1, src2 for read addresses
  *       src1data, src2data for the read data outputs
  *   dst1 for the write address
  *       dst1data for the write data; and
  *  
  *   Read outputs are generated combinationally (i.e. they are not latched).
  *   Writes occur only when io.wen is true.
  *   There is no bypass or guard logic, since writeback is guaranteed not to occur during reads.
  */

class RegFile2R1WVec(width: Int = 32, depth: Int = 32) extends Module {
  // Calculate the number of bits needed to address the registers.
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

  // Create the register file as a vector of registers, all initialized to zero.
  val regs = RegInit(VecInit(Seq.fill(depth)(0.U(width.W))))

  // Write logic: write to the register file if io.wen is true.
  when(io.wen) {
    regs(io.dst1) := io.dst1data
  }

  // Read logic: the outputs are generated combinationally.
  io.src1data := regs(io.src1)
  io.src2data := regs(io.src2)
}


