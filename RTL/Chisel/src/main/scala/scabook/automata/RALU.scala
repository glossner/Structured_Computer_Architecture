// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.automata
import chisel3._, chisel3.util._, scabook.memory.RiscvRegFile

object RALU {
  object Opcode {
    val ADD = 0.U; val SUB = 1.U; val SLL = 2.U; val SLT = 3.U; val SLTU = 4.U
    val XOR = 5.U; val SRL = 6.U; val SRA = 7.U; val OR  = 8.U; val AND  = 9.U; val PASS_B = 10.U
  }
}
class RALU(val width: Int = 32) extends Module {
  import RALU.Opcode._
  val io = IO(new Bundle {
    val rs1Addr, rs2Addr, rdAddr = Input(UInt(5.W))
    val regWrite, useImm, memToReg = Input(Bool())
    val aluOp = Input(UInt(4.W)); val zeroFlag = Output(Bool())
    val immVal, extData = Input(UInt(width.W))
    val rs1Data, rs2Data, aluResult = Output(UInt(width.W))
  })
  val regFile = Module(new RiscvRegFile(width))
  regFile.io.rs1 := io.rs1Addr; regFile.io.rs2 := io.rs2Addr
  regFile.io.rd  := io.rdAddr;  regFile.io.wen := io.regWrite
  io.rs1Data := regFile.io.rs1_data; io.rs2Data := regFile.io.rs2_data
  val opA = regFile.io.rs1_data; val opB = Mux(io.useImm, io.immVal, regFile.io.rs2_data)
  val shamt = opB(4, 0)
  val aluOut = MuxCase(0.U(width.W), Seq(
    (io.aluOp === ADD)    -> (opA + opB),       (io.aluOp === SUB)  -> (opA - opB),
    (io.aluOp === SLL)    -> (opA << shamt),    (io.aluOp === SRL)  -> (opA >> shamt),
    (io.aluOp === SRA)    -> (opA.asSInt >> shamt).asUInt,
    (io.aluOp === SLT)    -> (opA.asSInt < opB.asSInt).asUInt,
    (io.aluOp === SLTU)   -> (opA < opB).asUInt,
    (io.aluOp === XOR)    -> (opA ^ opB),       (io.aluOp === OR)   -> (opA | opB),
    (io.aluOp === AND)    -> (opA & opB),       (io.aluOp === PASS_B)-> opB
  ))
  io.aluResult := aluOut; io.zeroFlag := (aluOut === 0.U)
  regFile.io.rd_data := Mux(io.memToReg, io.extData, aluOut)
}
