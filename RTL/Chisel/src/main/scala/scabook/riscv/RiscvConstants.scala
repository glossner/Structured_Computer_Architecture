// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chisel3.util._

/** Standard RISC-V 7-bit primary opcodes (RV32I). */
object RiscvOpcodes {
  val OP     = "b0110011".U(7.W) // R-type: register-register ALU & Zmmul
  val OP_IMM = "b0010011".U(7.W) // I-type: register-immediate ALU
  val LOAD   = "b0000011".U(7.W) // I-type: loads (LB, LH, LW, LBU, LHU)
  val STORE  = "b0100011".U(7.W) // S-type: stores (SB, SH, SW)
  val BRANCH = "b1100011".U(7.W) // B-type: conditional branches
  val JAL    = "b1101111".U(7.W) // J-type: jump and link
  val JALR   = "b1100111".U(7.W) // I-type: jump and link register
  val LUI    = "b0110111".U(7.W) // U-type: load upper immediate
  val AUIPC  = "b0010111".U(7.W) // U-type: add upper immediate to PC
  val SYSTEM = "b1110011".U(7.W) // I-type: ECALL, EBREAK, CSR
}

/** Direct-wire 5-bit ALU control opcodes: {isMul, isAlt, funct3}.
  * Bit 4: isMul (funct7[0] = 1 for Zmmul extension)
  * Bit 3: isAlt (funct7[5] = 1 for SUB/SRA alternate operations)
  * Bits 2..0: funct3
  */
object AluOp {
  // Base RV32I ALU operations (isMul = 0)
  val ADD    = "b00000".U(5.W)
  val SUB    = "b01000".U(5.W)
  val SLL    = "b00001".U(5.W)
  val SLT    = "b00010".U(5.W)
  val SLTU   = "b00011".U(5.W)
  val XOR    = "b00100".U(5.W)
  val SRL    = "b00101".U(5.W)
  val SRA    = "b01101".U(5.W)
  val OR     = "b00110".U(5.W)
  val AND    = "b00111".U(5.W)

  // Zmmul extension multiplication operations (isMul = 1)
  val MUL    = "b10000".U(5.W) // Lower 32 bits (signed x signed)
  val MULH   = "b10001".U(5.W) // Upper 32 bits (signed x signed)
  val MULHSU = "b10010".U(5.W) // Upper 32 bits (signed x unsigned)
  val MULHU  = "b10011".U(5.W) // Upper 32 bits (unsigned x unsigned)
}

/** Synthesizable object-oriented view of RISC-V 32-bit instruction fields. */
class RiscvFields(val raw: UInt) {
  def opcode: UInt = raw(6, 0)
  def rd:     UInt = raw(11, 7)
  def funct3: UInt = raw(14, 12)
  def rs1:    UInt = raw(19, 15)
  def rs2:    UInt = raw(24, 20)
  def funct7: UInt = raw(31, 25)
  def isMul:  Bool = raw(25) // funct7[0] (Zmmul extension flag)
  def isAlt:  Bool = raw(30) // funct7[5] (SUB / SRA flag)
}

object RiscvFields {
  def apply(raw: UInt): RiscvFields = new RiscvFields(raw)
}

/** Structured control signals produced by the instruction decoder. */
class DecodedControl extends Bundle {
  val regWrite = Bool()
  val memRead  = Bool()
  val memWrite = Bool()
  val branch   = Bool()
  val jump     = Bool()
  val aluSrc   = Bool() // false: rs2, true: immediate
  val memToReg = Bool()
  val aluOp    = UInt(5.W)
}

/** Comprehensive decoded instruction bundle holding fields, immediate, and control. */
class DecodedInstruction extends Bundle {
  val rd      = UInt(5.W)
  val rs1     = UInt(5.W)
  val rs2     = UInt(5.W)
  val imm     = UInt(32.W)
  val control = new DecodedControl
}
