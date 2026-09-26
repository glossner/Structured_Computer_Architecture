// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.ALUs

import chisel3._
import chisel3.util._

import scabook.addersubtractors.MultifunctionAdderSubtractor64

// Opcodes:
// b5: arithmetic/logic, b1b0: 8/16/32/64 bits
object ALU64 {
  object Opcode {
    // Arithmetic Operations: b4: Unused, b3: Add/Sub, b2: U/S
    val ADD_U8  = "b000000".U
    val ADD_U16 = "b000001".U
    val ADD_U32 = "b000010".U
    val ADD_U64 = "b000011".U
    val SUB_U8  = "b001000".U
    val SUB_U16 = "b001001".U
    val SUB_U32 = "b001010".U
    val SUB_U64 = "b001011".U
    val ADD_S8  = "b000100".U
    val ADD_S16 = "b000101".U
    val ADD_S32 = "b000110".U
    val ADD_S64 = "b000111".U
    val SUB_S8  = "b001100".U
    val SUB_S16 = "b001101".U
    val SUB_S32 = "b001110".U
    val SUB_S64 = "b001111".U

    // Logical Operations: b4b3b2: operation
    val AND_U8  = "b100000".U
    val AND_U16 = "b100001".U
    val AND_U32 = "b100010".U
    val AND_U64 = "b100011".U
    val OR_U8   = "b100100".U
    val OR_U16  = "b100101".U
    val OR_U32  = "b100110".U
    val OR_U64  = "b100111".U
    val XOR_U8  = "b101000".U
    val XOR_U16 = "b101001".U
    val XOR_U32 = "b101010".U
    val XOR_U64 = "b101011".U
    val SLL_U8  = "b101100".U
    val SLL_U16 = "b101101".U
    val SLL_U32 = "b101110".U
    val SLL_U64 = "b101111".U
    val SRL_U8  = "b110000".U
    val SRL_U16 = "b110001".U
    val SRL_U32 = "b110010".U
    val SRL_U64 = "b110011".U
    val SRA_U8  = "b110000".U
    val SRA_U16 = "b110001".U
    val SRA_U32 = "b110010".U
    val SRA_U64 = "b110011".U
  }
}

class ALU64 extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(64.W))
    val b = Input(UInt(64.W))
    val result = Output(UInt(64.W))
    val opcode = Input(UInt(6.W)) 
    val carryOutFlag = Output(UInt(1.W))  // Carry / Borrow
    val overflowFlag = Output(UInt(1.W))  // V
    val zeroFlag     = Output(UInt(1.W))  // Z
    val negativeFlag = Output(UInt(1.W))  // N
  })

  // Decode control signals
  val isArithmetic = io.opcode(5) === 0.U
  val isLogical    = io.opcode(5) === 1.U
  val isSub        = io.opcode(3) === 1.U && isArithmetic
  val isSigned     = io.opcode(2) && isArithmetic
  val operandSize  = io.opcode(1, 0)

  // Determine effective width based on operand size
  val width = WireDefault(64.U)
  val mask  = WireDefault("hffffffffffffffff".U)
  switch(operandSize) {
    is("b00".U) { width := 8.U;  mask := "h00000000000000ff".U }
    is("b01".U) { width := 16.U; mask := "h000000000000ffff".U }
    is("b10".U) { width := 32.U; mask := "h00000000ffffffff".U }
    is("b11".U) { width := 64.U; mask := "hffffffffffffffff".U }
  }

  // Mask inputs to the appropriate width
  val aEffective = io.a & mask
  val bEffective = io.b & mask
  val bAdjusted  = Mux(isSub, (~bEffective + 1.U), bEffective)

  // Arithmetic operations via MultifunctionAdderSubtractor64
  val adderSubtractor = Module(
    new MultifunctionAdderSubtractor64
  )
  adderSubtractor.io.a := aEffective
  adderSubtractor.io.b := bEffective
  adderSubtractor.io.carryIn := 0.U(1.W)
  adderSubtractor.io.opcode := io.opcode(3, 0)

  // Logical operations
  val shamt = bEffective(5, 0) & (width - 1.U)
  val logicalResult = MuxCase(0.U(64.W), Seq(
    (io.opcode(4, 2) === "b000".U) -> (aEffective & bEffective),
    (io.opcode(4, 2) === "b001".U) -> (aEffective | bEffective),
    (io.opcode(4, 2) === "b010".U) -> (aEffective ^ bEffective),
    (io.opcode(4, 2) === "b011".U) ->
      ((aEffective << shamt).asUInt & mask),
    (io.opcode(4, 2) === "b100".U) ->
      ((aEffective >> shamt).asUInt & mask),
    (io.opcode(4, 2) === "b101".U) ->
      ((aEffective.asSInt >> shamt).asUInt & mask)
  ))

  // Assign results to outputs
  io.result := Mux(
    isArithmetic, adderSubtractor.io.result, logicalResult
  )
  io.carryOutFlag := Mux(
    isArithmetic, adderSubtractor.io.carryOut, 0.U
  )
  io.overflowFlag := Mux(
    isArithmetic, adderSubtractor.io.overflowFlag, 0.U
  )
  io.zeroFlag := Mux(
    isArithmetic, adderSubtractor.io.zeroFlag, 0.U
  )
  io.negativeFlag := Mux(
    isArithmetic, adderSubtractor.io.negativeFlag, 0.U
  )
}
