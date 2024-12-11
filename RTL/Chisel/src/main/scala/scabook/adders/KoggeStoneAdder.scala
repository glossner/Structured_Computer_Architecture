// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.adders

import chisel3._
import chisel3.util._

class KoggeStoneAdder(width: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(width.W))
    val b = Input(UInt(width.W))
    val sum = Output(UInt(width.W))
    val carryOut = Output(Bool())
  })

  // Generate the propagate and generate signals
  val propagate = Wire(Vec(width, Bool()))
  val generate = Wire(Vec(width, Bool()))

  for (i <- 0 until width) {
    propagate(i) := io.a(i) ^ io.b(i)
    generate(i) := io.a(i) & io.b(i)
  }

  // Create a 2D array for carry computation
  val carry = Array.fill(log2Ceil(width) + 1)(Wire(Vec(width, Bool())))

  // Base case: Initial carry signals
  for (i <- 0 until width) {
    carry(0)(i) := generate(i)
  }

  // Recursive carry computation
  for (level <- 1 to log2Ceil(width)) {
    for (i <- 0 until width) {
      if (i < (1 << (level - 1))) {
        carry(level)(i) := carry(level - 1)(i)
      } else {
        val previousCarry = carry(level - 1)(i)
        val propagatedCarry = carry(level - 1)(i - (1 << (level - 1))) & propagate(i)
        carry(level)(i) := carry(level - 1)(i) | propagatedCarry
      }
    }
  }

  // Compute the sum bits
  val finalCarry = carry(log2Ceil(width))
  val sum = Wire(Vec(width, Bool()))

  for (i <- 0 until width) {
    sum(i) := propagate(i) ^ (if (i == 0) false.B else finalCarry(i - 1))
  }

  // Output the results
  io.sum := sum.asUInt
  io.carryOut := finalCarry(width - 1)
}

// Testbench wrapper
object KoggeStoneAdder extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new KoggeStoneAdder(8))
}
