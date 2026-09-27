// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.adders

import chisel3._
import chisel3.util._

/**
 * 32-bit Kogge-Stone Parallel Prefix Adder.
 *
 * Computes addition in log2(width) = 5 stages with uniform fan-out of 2,
 * delivering minimum logic depth for high-speed integer execution units.
 */
class KoggeStoneAdder(val width: Int = 32) extends Module {
  require(isPow2(width), "Width must be a power of 2 for standard Kogge-Stone tree")

  val io = IO(new Bundle {
    val a        = Input(UInt(width.W))
    val b        = Input(UInt(width.W))
    val carryIn  = Input(Bool())
    val sum      = Output(UInt(width.W))
    val carryOut = Output(Bool())
  })

  // Phase 1: Pre-computation of bit-level Generate and Propagate
  val g0 = Wire(Vec(width, Bool()))
  val p0 = Wire(Vec(width, Bool()))
  for (i <- 0 until width) {
    g0(i) := io.a(i) & io.b(i)
    p0(i) := io.a(i) ^ io.b(i)
  }

  // Phase 2: Prefix Network (log2(width) stages)
  val numStages = log2Ceil(width)
  val gStages = Wire(Vec(numStages + 1, Vec(width, Bool())))
  val pStages = Wire(Vec(numStages + 1, Vec(width, Bool())))

  gStages(0) := g0
  pStages(0) := p0

  for (s <- 1 to numStages) {
    val stride = 1 << (s - 1)
    for (i <- 0 until width) {
      if (i >= stride) {
        gStages(s)(i) := gStages(s - 1)(i) | (pStages(s - 1)(i) & gStages(s - 1)(i - stride))
        pStages(s)(i) := pStages(s - 1)(i) & pStages(s - 1)(i - stride)
      } else {
        gStages(s)(i) := gStages(s - 1)(i)
        pStages(s)(i) := pStages(s - 1)(i)
      }
    }
  }

  // Phase 3: Post-computation of Carries and Sum bits
  val carries = Wire(Vec(width + 1, Bool()))
  carries(0) := io.carryIn
  for (i <- 1 to width) {
    carries(i) := gStages(numStages)(i - 1) | (pStages(numStages)(i - 1) & io.carryIn)
  }

  val sumBits = Wire(Vec(width, Bool()))
  for (i <- 0 until width) {
    sumBits(i) := p0(i) ^ carries(i)
  }

  io.sum      := sumBits.asUInt
  io.carryOut := carries(width)
}

object KoggeStoneAdder {
  def apply(a: UInt, b: UInt, carryIn: Bool = false.B, width: Int = 32): (UInt, Bool) = {
    val adder = Module(new KoggeStoneAdder(width))
    adder.io.a := a
    adder.io.b := b
    adder.io.carryIn := carryIn
    (adder.io.sum, adder.io.carryOut)
  }
}
