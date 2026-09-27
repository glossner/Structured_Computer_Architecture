// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.multipliers

import chisel3._
import chisel3.util._

/**
 * Parameterized Array Multiplier.
 *
 * Implements combinational array multiplication using a linear chain
 * of additions to accumulate partial products.
 * Demonstrates the baseline O(N) carry-propagation latency.
 */
class ArrayMultiplier(width: Int = 32) extends Multiplier(width) {
  val resWidth = 2 * width
  val aExt = Wire(UInt(resWidth.W))

  when(io.isSigned === 1.U) {
    aExt := Cat(Fill(width, io.a(width - 1)), io.a)
  }.otherwise {
    aExt := Cat(0.U(width.W), io.a)
  }

  val pp = Wire(Vec(width, UInt(resWidth.W)))
  for (i <- 0 until width - 1) {
    pp(i) := Mux(io.b(i), (aExt << i)(resWidth - 1, 0), 0.U)
  }

  val lastRow = WireDefault(0.U(resWidth.W))
  when(io.b(width - 1)) {
    when(io.isSigned === 1.U) {
      lastRow := ((~aExt).asUInt + 1.U) << (width - 1)
    }.otherwise {
      lastRow := aExt << (width - 1)
    }
  }
  pp(width - 1) := lastRow(resWidth - 1, 0)

  // Accumulate partial products through linear array stages
  val sumAcc = Wire(Vec(width, UInt(resWidth.W)))
  sumAcc(0) := pp(0)
  for (i <- 1 until width) {
    sumAcc(i) := sumAcc(i - 1) + pp(i)
  }

  io.result := sumAcc(width - 1)
}

object ArrayMultiplier {
  def apply(a: UInt, b: UInt, isSigned: UInt, width: Int = 32): UInt = {
    val mul = Module(new ArrayMultiplier(width))
    mul.io.a := a
    mul.io.b := b
    mul.io.isSigned := isSigned
    mul.io.result
  }
}
