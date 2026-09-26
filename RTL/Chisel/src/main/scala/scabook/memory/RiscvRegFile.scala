// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.memory

import chisel3._
import chisel3.util._

/**
  * 32-register, 2-read, 1-write architectural RISC-V Register File.
  * Hardwires register x0 to 0 on both read and write per the RV32 specification.
  */
class RiscvRegFile(val width: Int = 32) extends Module {
  val io = IO(new Bundle {
    val rs1      = Input(UInt(5.W))          // Read port 1 address
    val rs1_data = Output(UInt(width.W))     // Read port 1 data
    val rs2      = Input(UInt(5.W))          // Read port 2 address
    val rs2_data = Output(UInt(width.W))     // Read port 2 data
    val rd       = Input(UInt(5.W))          // Write port address
    val rd_data  = Input(UInt(width.W))      // Write port data
    val wen      = Input(Bool())             // Write enable
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
