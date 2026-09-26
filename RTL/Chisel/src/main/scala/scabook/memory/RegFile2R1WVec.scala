// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.memory

import chisel3._
import chisel3.util._

/**
  * 2-read, 1-write register file using a vector of registers (combinational read).
  */
class RegFile2R1WVec(width: Int = 32, depth: Int = 32) extends Module {
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

  // Register file as a vector of registers initialized to zero
  val regs = RegInit(VecInit(Seq.fill(depth)(0.U(width.W))))

  // Write logic: update register at dst1 when wen is asserted
  when(io.wen) {
    regs(io.dst1) := io.dst1data
  }

  // Read logic: combinational zero-cycle read
  io.src1data := regs(io.src1)
  io.src2data := regs(io.src2)
}
