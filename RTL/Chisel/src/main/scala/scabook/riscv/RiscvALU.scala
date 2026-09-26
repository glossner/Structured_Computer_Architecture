// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chisel3.util._

/** Complete RV32I_Zmmul Arithmetic Logic Unit with direct-wire 5-bit opcodes. */
class RiscvALU(val width: Int = 32) extends Module {
  val io = IO(new Bundle {
    val opA = Input(UInt(width.W)); val opB = Input(UInt(width.W))
    val aluOp = Input(UInt(5.W));   val result = Output(UInt(width.W))
    val zero = Output(Bool());      val lessThan = Output(Bool()); val lessThanU = Output(Bool())
  })

  val shamt = io.opB(4, 0); val sA = io.opA.asSInt; val sB = io.opB.asSInt
  val uB = Cat(0.U(1.W), io.opB).asSInt
  val hi = 2 * width - 1

  val aluOut = MuxCase(0.U(width.W), Seq(
    (io.aluOp === AluOp.ADD)    -> (io.opA + io.opB),
    (io.aluOp === AluOp.SUB)    -> (io.opA - io.opB),
    (io.aluOp === AluOp.SLL)    -> (io.opA << shamt),
    (io.aluOp === AluOp.SLT)    -> Mux(sA < sB, 1.U, 0.U),
    (io.aluOp === AluOp.SLTU)   -> Mux(io.opA < io.opB, 1.U, 0.U),
    (io.aluOp === AluOp.XOR)    -> (io.opA ^ io.opB),
    (io.aluOp === AluOp.SRL)    -> (io.opA >> shamt),
    (io.aluOp === AluOp.SRA)    -> (sA >> shamt).asUInt,
    (io.aluOp === AluOp.OR)     -> (io.opA | io.opB),
    (io.aluOp === AluOp.AND)    -> (io.opA & io.opB),
    (io.aluOp === AluOp.MUL)    -> (io.opA * io.opB)(width - 1, 0),
    (io.aluOp === AluOp.MULH)   -> (sA * sB)(hi, width).asUInt,
    (io.aluOp === AluOp.MULHSU) -> (sA * uB)(hi, width).asUInt,
    (io.aluOp === AluOp.MULHU)  -> (io.opA * io.opB)(hi, width)
  ))

  io.result    := aluOut
  io.zero      := (aluOut === 0.U)
  io.lessThan  := (sA < sB)
  io.lessThanU := (io.opA < io.opB)
}
