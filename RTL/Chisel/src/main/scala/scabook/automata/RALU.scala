// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.automata

import chisel3._
import chisel3.util._
import scabook.memory.RiscvRegFile

/**
  * Registers with Arithmetic and Logic Unit (RALU) Automaton.
  *
  * The RALU represents a canonical 2-OS subsystem closing the execution loop:
  *   1. The register file provides operand data (rs1, rs2).
  *   2. Combinational multiplexers select between register operands and immediates.
  *   3. The Arithmetic & Logic Unit (ALU) computes the arithmetic or logical result.
  *   4. The result (or external memory data) is fed back to the register write port.
  *
  * This Chisel module implements the RALU structure defined in Chapter 2.10
  * (mirroring A10_RALU.sv) customized for the 32-bit RISC-V ISA execution stage.
  *
  * @param width Bit-width of datapath and registers (default: 32 for RV32).
  */
object RALU {
  object Opcode {
    val ADD    = 0.U(4.W)
    val SUB    = 1.U(4.W)
    val SLL    = 2.U(4.W)
    val SLT    = 3.U(4.W)
    val SLTU   = 4.U(4.W)
    val XOR    = 5.U(4.W)
    val SRL    = 6.U(4.W)
    val SRA    = 7.U(4.W)
    val OR     = 8.U(4.W)
    val AND    = 9.U(4.W)
    val PASS_B = 10.U(4.W)
  }
}

class RALU(val width: Int = 32) extends Module {
  import RALU.Opcode._

  val io = IO(new Bundle {
    // Register address selection
    val rs1Addr  = Input(UInt(5.W))
    val rs2Addr  = Input(UInt(5.W))
    val rdAddr   = Input(UInt(5.W))
    val regWrite = Input(Bool())

    // ALU control and operand selection
    val aluOp    = Input(UInt(4.W))
    val useImm   = Input(Bool())
    val immVal   = Input(UInt(width.W))
    val extData  = Input(UInt(width.W))
    val memToReg = Input(Bool())

    // Outputs for datapath and branch evaluation
    val rs1Data   = Output(UInt(width.W))
    val rs2Data   = Output(UInt(width.W))
    val aluResult = Output(UInt(width.W))
    val zeroFlag  = Output(Bool())
  })

  // Instantiate 32-register RISC-V register file
  val regFile = Module(new RiscvRegFile(width))
  regFile.io.rs1 := io.rs1Addr
  regFile.io.rs2 := io.rs2Addr
  regFile.io.rd  := io.rdAddr
  regFile.io.wen := io.regWrite

  io.rs1Data := regFile.io.rs1_data
  io.rs2Data := regFile.io.rs2_data

  // Operand selection
  val opA = regFile.io.rs1_data
  val opB = Mux(io.useImm, io.immVal, regFile.io.rs2_data)

  // 32-bit ALU operation
  val aluOut = WireDefault(0.U(width.W))
  val shamt  = opB(4, 0) // Shift amount is 5 bits for 32-bit width

  switch(io.aluOp) {
    is(ADD)    { aluOut := opA + opB }
    is(SUB)    { aluOut := opA - opB }
    is(SLL)    { aluOut := opA << shamt }
    is(SLT)    { aluOut := Mux(opA.asSInt < opB.asSInt, 1.U, 0.U) }
    is(SLTU)   { aluOut := Mux(opA < opB, 1.U, 0.U) }
    is(XOR)    { aluOut := opA ^ opB }
    is(SRL)    { aluOut := opA >> shamt }
    is(SRA)    { aluOut := (opA.asSInt >> shamt).asUInt }
    is(OR)     { aluOut := opA | opB }
    is(AND)    { aluOut := opA & opB }
    is(PASS_B) { aluOut := opB }
  }

  io.aluResult := aluOut
  io.zeroFlag  := (aluOut === 0.U)

  // Write-back multiplexer: select between ALU output and external data (e.g., memory read)
  val wbData = Mux(io.memToReg, io.extData, aluOut)
  regFile.io.rd_data := wbData
}
