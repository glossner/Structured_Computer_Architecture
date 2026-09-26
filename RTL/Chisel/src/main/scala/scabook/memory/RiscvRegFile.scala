// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.memory

import chisel3._
import chisel3.util._

/**
  * A 32-register, 2-read, 1-write RISC-V Register File.
  *
  * Complies with the RISC-V unprivileged specification:
  *   - 32 general-purpose registers (x0 to x31).
  *   - Register x0 is hardwired to constant 0:
  *       * Reads from x0 always yield 0.
  *       * Writes to x0 are ignored.
  *   - Dual combinational read ports (rs1, rs2) provide zero-cycle latency.
  *   - Single synchronous write port (rd) updates on the rising clock edge when wen is asserted.
  *
  * @param width Bit-width of each register (default: 32 for RV32, 64 for RV64).
  */
class RiscvRegFile(val width: Int = 32) extends Module {
  val io = IO(new Bundle {
    // Read Port 1
    val rs1      = Input(UInt(5.W))
    val rs1_data = Output(UInt(width.W))

    // Read Port 2
    val rs2      = Input(UInt(5.W))
    val rs2_data = Output(UInt(width.W))

    // Write Port
    val rd       = Input(UInt(5.W))
    val rd_data  = Input(UInt(width.W))
    val wen      = Input(Bool())
  })

  // 32 registers initialized to zero
  val regs = RegInit(VecInit(Seq.fill(32)(0.U(width.W))))

  // Synchronous write logic: guard against writing to register x0
  when(io.wen && (io.rd =/= 0.U)) {
    regs(io.rd) := io.rd_data
  }

  // Combinational read logic: register x0 is hardwired to 0
  io.rs1_data := Mux(io.rs1 === 0.U, 0.U, regs(io.rs1))
  io.rs2_data := Mux(io.rs2 === 0.U, 0.U, regs(io.rs2))
}
