// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.automata

import chisel3._
import scabook.memory.RiscvRegFile
import scabook.riscv.{AluOp, RiscvALU}

object RALU {
  val Opcode = AluOp
}

/** 2-OS Execution Engine loop-coupling RiscvRegFile and RiscvALU. */
class RALU(val width: Int = 32) extends Module {
  val io = IO(new Bundle {
    val rs1Addr, rs2Addr, rdAddr    = Input(UInt(5.W))
    val regWrite, useImm, memToReg  = Input(Bool())
    val aluOp                       = Input(UInt(5.W))
    val immVal, extData             = Input(UInt(width.W))
    val rs1Data, rs2Data, aluResult = Output(UInt(width.W))
    val zeroFlag                    = Output(Bool())
  })

  val regFile = Module(new RiscvRegFile(width))
  val alu     = Module(new RiscvALU(width))

  regFile.io.rs1 := io.rs1Addr
  regFile.io.rs2 := io.rs2Addr
  regFile.io.rd  := io.rdAddr
  regFile.io.wen := io.regWrite

  io.rs1Data := regFile.io.rs1_data
  io.rs2Data := regFile.io.rs2_data

  alu.io.opA   := regFile.io.rs1_data
  alu.io.opB   := Mux(io.useImm, io.immVal, regFile.io.rs2_data)
  alu.io.aluOp := io.aluOp

  io.aluResult := alu.io.result
  io.zeroFlag  := alu.io.zero
  regFile.io.rd_data := Mux(io.memToReg, io.extData, alu.io.result)
}
