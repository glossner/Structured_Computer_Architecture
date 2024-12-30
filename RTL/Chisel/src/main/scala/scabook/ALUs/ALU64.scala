// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.ALUs

import chisel3._
import chisel3.util._

import scabook.addersubtractors.MultifunctionAdderSubtractor64

// Opcodes
// b5: arithmetic/logic
// b1b0: 8/16/32/64 bits
object ALU64 {
  object Opcode {
    // Arithmetic Operations
    // b4: Unused
    // b3: Add/Sub
    // b2: Unsigned/Signed
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

    // Logical Operations
    // b4b3b2: operation
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
    val carryOutFlag = Output(UInt(1.W))  //Carry and Borrow
    val overflowFlag = Output(UInt(1.W))  //V
    val zeroFlag = Output(UInt(1.W))      //Z
    val negativeFlag = Output(UInt(1.W))  //N
  })

  val printDebugInfo = false

  // Debug internals of ALU
  if(printDebugInfo) printf(p"[Inside ALU64] --\n") 

  // Decode control signals
  // b5: arithetic / logic
  // b4b3b2: logic ope
  // b3: if arith: Add/Sub
  // b2: if arith: Signed/Unsigned
  // b1b0: 8/16/32/64 bits
  // Decode control signals
  val isArithmetic = io.opcode(5) === 0.U // MSB: 0 for arithmetic
  val isLogical = io.opcode(5) === 1.U // MSB: 1 for logical operations
  val isSub = io.opcode(3) === 1.U && isArithmetic // b3: 1 for subtraction
  val isSigned = io.opcode(2) && isArithmetic // b2: 1 for signed
  val operandSize = io.opcode(1, 0) // b1b0: 00 for 8-bit, 01 for 16-bit, 10 for 32-bit, 11 for 64-bit

  // Determine effective width based on operand size
  val width = WireDefault(64.U)
  val mask = WireDefault("hffffffffffffffff".U)
  switch(operandSize) {
    is("b00".U) { width := 8.U;  mask := "h00000000000000ff".U }
    is("b01".U) { width := 16.U; mask := "h000000000000ffff".U }
    is("b10".U) { width := 32.U; mask := "h00000000ffffffff".U }
    is("b11".U) { width := 64.U; mask := "hffffffffffffffff".U }
  }

  // Mask inputs to the appropriate width
  val aEffective = io.a & mask
  val bEffective = io.b & mask
  val bAdjusted = Mux(isSub, (~bEffective + 1.U), bEffective)

  if(printDebugInfo){
    printf(p"  width = ${width}, isSigned = ${isSigned}, isSub = ${isSub}\n")
    printf(p"  mask =       0x${Hexadecimal(mask)}\n")
    printf(p"  a =          0x${Hexadecimal(io.a)}\n")
    printf(p"  b =          0x${Hexadecimal(io.b)}\n")
    printf(p"  aEffective = 0x${Hexadecimal(aEffective)},\n")
    printf(p"  bEffective = 0x${Hexadecimal(bEffective)},\n")
    printf(p"  bAdjusted =  0x${Hexadecimal(bAdjusted)},\n")
  }

  //***********************
  // Arithmetic operations
  //***********************
  val adderSubtractor = Module(new MultifunctionAdderSubtractor64)

  // Connect inputs
  adderSubtractor.io.a := aEffective
  adderSubtractor.io.b := bEffective
  adderSubtractor.io.carryIn := 0.U(1.W)
  adderSubtractor.io.opcode := io.opcode(3, 0)

  // Connect outputs to the ALU64 outputs
  io.result := adderSubtractor.io.result
  io.carryOutFlag := adderSubtractor.io.carryOut
  io.overflowFlag := adderSubtractor.io.overflowFlag
  io.zeroFlag := adderSubtractor.io.zeroFlag
  io.negativeFlag := adderSubtractor.io.negativeFlag
  

  //********************
  // Logical operations
  //********************
  // b4b3b2: logical operation
  val logicalResult = MuxCase(0.U(64.W), Seq(
    (io.opcode(4, 2) === "b000".U) -> (aEffective & bEffective),                               // AND
    (io.opcode(4, 2) === "b001".U) -> (aEffective | bEffective),                               // OR
    (io.opcode(4, 2) === "b010".U) -> (aEffective ^ bEffective),                               // XOR
    (io.opcode(4, 2) === "b011".U) -> ((aEffective << (bEffective(5, 0) & (width - 1.U))).asUInt & mask), // SLL
    (io.opcode(4, 2) === "b100".U) -> ((aEffective >> (bEffective(5, 0) & (width - 1.U))).asUInt & mask), // SRL
    (io.opcode(4, 2) === "b101".U) -> ((aEffective.asSInt >> (bEffective(5, 0) & (width - 1.U))).asUInt & mask) // SRA
  ))

  if(printDebugInfo) printf(p"  logicalResult =     0x${Hexadecimal(logicalResult)}\n")

  //**************************
  // Assign results to outputs
  //**************************
  io.result := Mux(isArithmetic, adderSubtractor.io.result, logicalResult)
  io.carryOutFlag := Mux(isArithmetic, adderSubtractor.io.carryOut, 0.U)
  io.overflowFlag := Mux(isArithmetic, adderSubtractor.io.overflowFlag, 0.U)
  io.zeroFlag := Mux(isArithmetic, adderSubtractor.io.zeroFlag, 0.U)
  io.negativeFlag := Mux(isArithmetic, adderSubtractor.io.negativeFlag, 0.U)

}
