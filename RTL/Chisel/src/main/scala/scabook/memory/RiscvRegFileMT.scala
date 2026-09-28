// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.memory

import chisel3._
import chisel3.util._

/**
  * Multithreaded RISC-V Register File (2-read, 1-write).
  *
  * Supports `threads` independent architectural register contexts (default 4).
  * Each thread has 32 registers (x0..x31), where x0 is hardwired to 0.
  *
  * In a barrel processor:
  *   - Read accesses use the thread ID currently in the ID stage.
  *   - Write accesses use the thread ID currently in the MEM/WB stage.
  */
class RiscvRegFileMT(val width: Int = 32, val threads: Int = 4) extends Module {
  val threadWidth = log2Ceil(threads)

  val io = IO(new Bundle {
    // Read Ports (sampled in ID stage for the current thread)
    val readThreadId = Input(UInt(threadWidth.W))
    val rs1          = Input(UInt(5.W))
    val rs1_data     = Output(UInt(width.W))
    val rs2          = Input(UInt(5.W))
    val rs2_data     = Output(UInt(width.W))

    // Write Port (sampled in MEM/WB stage for the committing thread)
    val writeThreadId = Input(UInt(threadWidth.W))
    val rd            = Input(UInt(5.W))
    val rd_data       = Input(UInt(width.W))
    val wen           = Input(Bool())
  })

  // Register array: threads * 32 registers
  val regs = RegInit(VecInit(Seq.fill(threads * 32)(0.U(width.W))))

  // Compute flattened indices: (threadId * 32) + regIndex
  val writeIdx = Cat(io.writeThreadId, io.rd)
  val readIdx1 = Cat(io.readThreadId, io.rs1)
  val readIdx2 = Cat(io.readThreadId, io.rs2)

  // Synchronous write logic: guard against writing to x0 of any thread
  when(io.wen && (io.rd =/= 0.U)) {
    regs(writeIdx) := io.rd_data
  }

  // Combinational read logic: register x0 is hardwired to 0 for all threads
  io.rs1_data := Mux(io.rs1 === 0.U, 0.U, regs(readIdx1))
  io.rs2_data := Mux(io.rs2 === 0.U, 0.U, regs(readIdx2))
}
