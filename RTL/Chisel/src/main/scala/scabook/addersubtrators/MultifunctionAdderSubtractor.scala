// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._
import chisel3.util._


// Opcodes
// b3: Add/Sub
// b2: Signed/Unsigned
// b1b0: 8/16/32/64 bits
object MultifunctionAdder {
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
}

class MultifunctionAdderSubtractor extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(64.W))
    val b = Input(UInt(64.W))
    val result = Output(UInt(64.W))
    val opcode = Input(UInt(4.W)) // 4-bit opcode signal (MSB: Add/Sub, Bit 2: Signed/Unsigned, Bits 1-0: Operand size)
    val carryOut = Output(Bool())
  })

  // Decode control signals
  val isSub = io.opcode(3) // MSB: 1 for Subtraction, 0 for Addition
  val isAdd = !isSub
  val isSigned = io.opcode(2) // Bit 2: 1 for Signed, 0 for Unsigned
  val operandSize = io.opcode(1, 0) // Bits 1-0: 00 for 8-bit, 01 for 16-bit, 10 for 32-bit, 11 for 64-bit

  // Determine effective width based on operand size
  val width = WireDefault(64.U)
  switch(operandSize) {
    is("b00".U) { width := 8.U }
    is("b01".U) { width := 16.U }
    is("b10".U) { width := 32.U }
    is("b11".U) { width := 64.U }
  }

  // Mask inputs to the appropriate width
  val mask = ((1.U << width) - 1.U)
  val aEffective = io.a & mask
  val bEffective = io.b & mask

  // Compute effective b for subtraction
  val bAdjusted = Mux(isSub, ~bEffective + 1.U, bEffective)

  // Perform addition or subtraction
  val fullResult = Mux(isSigned,
    (aEffective.asSInt +& bAdjusted.asSInt).asUInt, // Signed operation
    (aEffective +& bAdjusted)                       // Unsigned operation
  )

  // Clip it to the datatype width
  val truncatedResult = fullResult & mask

   // Extend the result to 64 bits
  val extendedResult = Mux(isSigned,
    truncatedResult.asSInt.pad(64).asUInt, // Sign-extend for signed operations
    truncatedResult.pad(64)                // Zero-extend for unsigned operations
  )

  // Assign the extended result to output
  io.result := truncatedResult

   // Carry out logic
  // For unsigned operations: carryOut is set if there's an overflow (addition) or borrow (subtraction)
  io.carryOut := Mux(isSigned, false.B, 
    Mux(isAdd, fullResult > mask, aEffective < bEffective) // Overflow for addition, borrow for subtraction
    )
}
