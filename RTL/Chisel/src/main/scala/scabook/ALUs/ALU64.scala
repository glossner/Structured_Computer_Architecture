// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.ALUs

import chisel3._
import chisel3.util._


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

  //######################
  // Arithmetic operations
  //######################
  val fullArithmeticResult = Mux(isSigned,
    (aEffective.asSInt +& bAdjusted.asSInt).asUInt, // Signed operation
    (aEffective +& bAdjusted)                       // Unsigned operation
  )
  val arithmeticResult = fullArithmeticResult & mask
  
  if(printDebugInfo) {
    printf(p"  fullArithmeticResult = 0x${Hexadecimal(fullArithmeticResult)}\n")
    printf(p"  arithmeticResult =     0x${Hexadecimal(arithmeticResult)}\n")
  }

  //###################
  // Logical operations
  //###################
  // b4b3b2: logical operation
  val logicalResult = WireDefault(0.U(64.W))
  when(io.opcode(4, 2) === "b000".U) { // AND
    logicalResult := aEffective & bEffective
  }.elsewhen(io.opcode(4, 2) === "b001".U) { // OR
    logicalResult := aEffective | bEffective
  }.elsewhen(io.opcode(4, 2) === "b010".U) { // XOR
    logicalResult := aEffective ^ bEffective
  }.elsewhen(io.opcode(4, 2) === "b011".U) { // SLL (Shift Left Logical)
    logicalResult := (aEffective << (bEffective(5, 0) & (width - 1.U))).asUInt & mask
  }.elsewhen(io.opcode(4, 2) === "b100".U) { // SRL (Shift Right Logical)
    logicalResult := (aEffective >> (bEffective(5, 0) & (width - 1.U))).asUInt & mask
  }.elsewhen(io.opcode(4, 2) === "b101".U) { // SRA (Shift Right Arithmetic)
    logicalResult := (aEffective.asSInt >> (bEffective(5, 0) & (width - 1.U))).asUInt & mask
  }.otherwise { 
    // Handle unsupported opcodes explicitly
    logicalResult := 0.U // Default result for unsupported operations
  }
  if(printDebugInfo) printf(p"  logicalResult =     0x${Hexadecimal(logicalResult)}\n")

  //##############
  // Final result
  //##############
  io.result := Mux(isArithmetic, arithmeticResult, logicalResult)
  if(printDebugInfo) printf(p"  io.result =     0x${Hexadecimal(io.result)}\n")

  //##############
  // Compute Flags
  //##############

  // Carry Flag: Unsigned Arithmetic

  // Chisel considers width a dynamic construct
  //   Unfortunately, every width must be explicit

  val isCarry = WireDefault(false.B)
  when(width === 8.U) {
    isCarry := fullArithmeticResult(8)
  }.elsewhen(width === 16.U) {
    isCarry := fullArithmeticResult(16)
  }.elsewhen(width === 32.U) {
    isCarry := fullArithmeticResult(32)
  }.elsewhen(width === 64.U) {
    isCarry := fullArithmeticResult(64)
  }

 // Borrow
  val isBorrow = WireDefault(false.B)
  when(isSub && !isSigned) {
    isBorrow := aEffective < bEffective // Borrow for unsigned subtraction
}

  if(printDebugInfo) printf(p"  isCarry =     0x${Hexadecimal(isCarry)}\n")
  if(printDebugInfo) printf(p"  isBorrow =    0x${Hexadecimal(isBorrow)}\n")

  io.carryOutFlag := MuxCase(0.U, Seq(
    (isArithmetic && !isSigned && !isSub) -> isCarry, 
    (isArithmetic && !isSigned &&  isSub) -> isBorrow,
    isLogical -> 0.U // No carryOut for logical operations
  )) 

  if(printDebugInfo) printf(p"  io.carryOut =     0x${Hexadecimal(io.carryOutFlag)}\n")

  // Overflow: Signed Arithmetic
  // Adding with different signs can't cause overflow
  // Overflow = both operand signs the same, result sign different
  val aSign = WireDefault(false.B)
  val bSign = WireDefault(false.B)
  val sumSign = WireDefault(false.B)

  when(width === 8.U) {
    aSign := aEffective(7)
    bSign := bAdjusted(7)
    sumSign := fullArithmeticResult(7)
  }.elsewhen(width === 16.U) {
    aSign := aEffective(15)
    bSign := bAdjusted(15)
    sumSign := fullArithmeticResult(15)
  }.elsewhen(width === 32.U) {
    aSign := aEffective(31)
    bSign := bAdjusted(31)
    sumSign := fullArithmeticResult(31)
  }.elsewhen(width === 64.U) {
    aSign := aEffective(63)
    bSign := bAdjusted(63)
    sumSign := fullArithmeticResult(63)
  }

  val isOverflow = (aSign === bSign) && (aSign =/= sumSign)
  if (printDebugInfo) printf(p"  overflowFlag =     0x${Hexadecimal(isOverflow)}\n")
  io.overflowFlag := isOverflow

  val isZero = !arithmeticResult.orR // Reduction OR to check if any bit in `io.result` is 1
  if (printDebugInfo) printf(p"  zeroFlag =     0x${Hexadecimal(isZero)}\n")
  io.zeroFlag := isZero

  val isNegative = WireDefault(false.B)
  when(isSigned) { 
    isNegative := sumSign // Use := for assigning values in Chisel
  }.otherwise {
    isNegative := false.B
  }
  io.negativeFlag := isNegative
}
