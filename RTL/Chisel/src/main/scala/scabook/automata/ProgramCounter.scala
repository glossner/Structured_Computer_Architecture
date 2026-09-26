// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.automata

import chisel3._
import chisel3.util._

/**
  * Program Counter (PC) Control Automaton for RISC-V Architectures.
  *
  * The Program Counter is a sequential circuit that generates instruction memory
  * addresses. It maintains the current execution address and computes the next PC
  * based on control flow mode:
  *   1. Plus4:   Sequential execution (PC + 4)
  *   2. Branch:  PC-relative target for conditional branches and JAL (PC + branchImm)
  *   3. Jalr:    Register-indirect target (jalrTarget with LSB cleared per RISC-V spec)
  *   4. Stall:   Freeze PC during pipeline bubbles or memory stalls (PC)
  *
  * @param width  Bit-width of the program counter (default: 32 for RV32).
  * @param initPC Initial reset address (default: 0x00000000).
  */
object ProgramCounter {
  object Mode extends ChiselEnum {
    val Plus4, Branch, Jalr, Stall = Value
  }
}

class ProgramCounter(val width: Int = 32, val initPC: BigInt = 0) extends Module {
  import ProgramCounter._

  val io = IO(new Bundle {
    val mode       = Input(Mode())
    val branchImm  = Input(UInt(width.W)) // Relative branch/jump offset
    val jalrTarget = Input(UInt(width.W)) // Target base + offset from rs1
    val pc         = Output(UInt(width.W)) // Current registered PC
    val nextPC     = Output(UInt(width.W)) // Next PC output (combinational)
  })

  val pcReg = RegInit(initPC.U(width.W))
  val nextPCWire = WireDefault(pcReg + 4.U)

  switch(io.mode) {
    is(Mode.Plus4) {
      nextPCWire := pcReg + 4.U
    }
    is(Mode.Branch) {
      nextPCWire := pcReg + io.branchImm
    }
    is(Mode.Jalr) {
      // RISC-V unprivileged specification requires the least-significant bit to be zeroed
      nextPCWire := Cat(io.jalrTarget(width - 1, 1), 0.U(1.W))
    }
    is(Mode.Stall) {
      nextPCWire := pcReg
    }
  }

  pcReg := nextPCWire
  io.pc := pcReg
  io.nextPC := nextPCWire
}
