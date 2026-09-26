// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.addersubtractors

import chisel3._
import chisel3.util._

// Opcodes:
// b3: Add/Sub, b2: Signed/Unsigned, b1b0: 8/16/32/64 bits
object MultifunctionAdderSubtractor64 {

  // Define Opcodes within the companion object
  object Opcode {
    val ADD_U8  = "b0000".U
    val ADD_U16 = "b0001".U
    val ADD_U32 = "b0010".U
    val ADD_U64 = "b0011".U
    val SUB_U8  = "b1000".U
    val SUB_U16 = "b1001".U
    val SUB_U32 = "b1010".U
    val SUB_U64 = "b1011".U
    val ADD_S8  = "b0100".U
    val ADD_S16 = "b0101".U
    val ADD_S32 = "b0110".U
    val ADD_S64 = "b0111".U
    val SUB_S8  = "b1100".U
    val SUB_S16 = "b1101".U
    val SUB_S32 = "b1110".U
    val SUB_S64 = "b1111".U
  }

  // Companion object factory method
  def apply(
    a: UInt,
    b: UInt,
    opcode: UInt,
    carryIn: Option[UInt] = None
  ): MultifunctionAdderSubtractor64 = {
    val module = Module(new MultifunctionAdderSubtractor64)
    module.io.a := a
    module.io.b := b
    module.io.opcode := opcode
    carryIn match {
      case Some(v) => module.io.carryIn := v
      case None    => module.io.carryIn := 0.U
    }
    module
  }
}

class MultifunctionAdderSubtractor64
  extends MultifunctionAdderSubtractor(64) {

  // Decode control signals
  // b3: 1 for subtraction, b2: 1 for signed
  // b1b0: 00 (8-bit), 01 (16-bit), 10 (32-bit), 11 (64-bit)
  val isSub       = io.opcode(3) === 1.U
  val isSigned    = io.opcode(2)
  val operandSize = io.opcode(1, 0)

  // Determine effective width based on operand size
  val width = MuxCase(64.U, Seq(
    (operandSize === "b00".U) -> 8.U,
    (operandSize === "b01".U) -> 16.U,
    (operandSize === "b10".U) -> 32.U,
    (operandSize === "b11".U) -> 64.U
  ))

  val mask = MuxCase("hffffffffffffffff".U, Seq(
    (operandSize === "b00".U) -> "h00000000000000ff".U,
    (operandSize === "b01".U) -> "h000000000000ffff".U,
    (operandSize === "b10".U) -> "h00000000ffffffff".U,
    (operandSize === "b11".U) -> "hffffffffffffffff".U
  ))

  // Mask inputs to the appropriate width
  val aEffective = io.a & mask
  val bEffective = io.b & mask
  val bAdjusted  = Mux(isSub, (~bEffective + 1.U), bEffective)

  // Arithmetic operations
  val fullArithmeticResult = Mux(isSigned,
    (aEffective.asSInt +& bAdjusted.asSInt).asUInt,
    (aEffective +& bAdjusted)
  )
  val truncatedResult = fullArithmeticResult & mask
  io.result := truncatedResult

  // Carry and Borrow Flags
  val isCarry = MuxCase(false.B, Seq(
    (width === 8.U)  -> fullArithmeticResult(8),
    (width === 16.U) -> fullArithmeticResult(16),
    (width === 32.U) -> fullArithmeticResult(32),
    (width === 64.U) -> fullArithmeticResult(64)
  ))

  val isBorrow = Mux(isSub && !isSigned,
    aEffective < bEffective, false.B)

  io.carryOut := MuxCase(0.U, Seq(
    (!isSigned && !isSub) -> isCarry,
    (!isSigned &&  isSub) -> isBorrow
  ))

  // Overflow Flag (Signed Arithmetic)
  val aSign   = WireDefault(false.B)
  val bSign   = WireDefault(false.B)
  val sumSign = WireDefault(false.B)

  aSign := MuxCase(false.B, Seq(
    (width === 8.U)  -> aEffective(7),
    (width === 16.U) -> aEffective(15),
    (width === 32.U) -> aEffective(31),
    (width === 64.U) -> aEffective(63)
  ))

  bSign := MuxCase(false.B, Seq(
    (width === 8.U)  -> bAdjusted(7),
    (width === 16.U) -> bAdjusted(15),
    (width === 32.U) -> bAdjusted(31),
    (width === 64.U) -> bAdjusted(63)
  ))

  sumSign := MuxCase(false.B, Seq(
    (width === 8.U)  -> fullArithmeticResult(7),
    (width === 16.U) -> fullArithmeticResult(15),
    (width === 32.U) -> fullArithmeticResult(31),
    (width === 64.U) -> fullArithmeticResult(63)
  ))

  io.overflowFlag := (aSign === bSign) && (aSign =/= sumSign)
  io.zeroFlag     := !truncatedResult.orR
  io.negativeFlag := Mux(isSigned, sumSign, false.B)
}
