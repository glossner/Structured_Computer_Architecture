// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.adders

import chisel3._
import chisel3.util._

/**
 * 32-bit Brent-Kung Parallel Prefix Adder.
 *
 * Implements a balanced prefix tree using an upward reduction phase followed by
 * a downward distribution phase (9 total logic stages for 32 bits),
 * minimizing wiring tracks and fan-out at the expense of slightly deeper logic depth.
 */
class BrentKungAdder(val width: Int = 32) extends Module {
  require(width == 32, "This reference Brent-Kung implementation specializes width = 32")

  val io = IO(new Bundle {
    val a        = Input(UInt(width.W))
    val b        = Input(UInt(width.W))
    val carryIn  = Input(Bool())
    val sum      = Output(UInt(width.W))
    val carryOut = Output(Bool())
  })

  // Prefix operator definition
  def dot(g2: Bool, p2: Bool, g1: Bool, p1: Bool): (Bool, Bool) = {
    val g = g2 | (p2 & g1)
    val p = p2 & p1
    (g, p)
  }

  // Stage 0: Bit-level Generate and Propagate
  val g0 = Wire(Vec(32, Bool()))
  val p0 = Wire(Vec(32, Bool()))
  for (i <- 0 until 32) {
    g0(i) := io.a(i) & io.b(i)
    p0(i) := io.a(i) ^ io.b(i)
  }


  // Stage 1: stride 1 on odd indices (1, 3, 5, ..., 31)
  val g1 = Wire(Vec(32, Bool()))
  val p1 = Wire(Vec(32, Bool()))
  for (i <- 0 until 32) {
    if (i % 2 == 1) {
      val (ng, np) = dot(g0(i), p0(i), g0(i - 1), p0(i - 1))
      g1(i) := ng; p1(i) := np
    } else {
      g1(i) := g0(i); p1(i) := p0(i)
    }
  }

  // Stage 2: stride 2 on indices 3, 7, 11, 15, 19, 23, 27, 31
  val g2 = Wire(Vec(32, Bool()))
  val p2 = Wire(Vec(32, Bool()))
  for (i <- 0 until 32) {
    if ((i + 1) % 4 == 0) {
      val (ng, np) = dot(g1(i), p1(i), g1(i - 2), p1(i - 2))
      g2(i) := ng; p2(i) := np
    } else {
      g2(i) := g1(i); p2(i) := p1(i)
    }
  }

  // Stage 3: stride 4 on indices 7, 15, 23, 31
  val g3 = Wire(Vec(32, Bool()))
  val p3 = Wire(Vec(32, Bool()))
  for (i <- 0 until 32) {
    if ((i + 1) % 8 == 0) {
      val (ng, np) = dot(g2(i), p2(i), g2(i - 4), p2(i - 4))
      g3(i) := ng; p3(i) := np
    } else {
      g3(i) := g2(i); p3(i) := p2(i)
    }
  }

  // Stage 4: stride 8 on indices 15, 31
  val g4 = Wire(Vec(32, Bool()))
  val p4 = Wire(Vec(32, Bool()))
  for (i <- 0 until 32) {
    if ((i + 1) % 16 == 0) {
      val (ng, np) = dot(g3(i), p3(i), g3(i - 8), p3(i - 8))
      g4(i) := ng; p4(i) := np
    } else {
      g4(i) := g3(i); p4(i) := p3(i)
    }
  }

  // Stage 5: stride 16 on index 31
  val g5 = Wire(Vec(32, Bool()))
  val p5 = Wire(Vec(32, Bool()))
  for (i <- 0 until 32) {
    if (i == 31) {
      val (ng, np) = dot(g4(31), p4(31), g4(15), p4(15))
      g5(i) := ng; p5(i) := np
    } else {
      g5(i) := g4(i); p5(i) := p4(i)
    }
  }

  // Downward distribution tree
  // Stage 6: compute index 23
  val g6 = Wire(Vec(32, Bool()))
  val p6 = Wire(Vec(32, Bool()))
  for (i <- 0 until 32) {
    if (i == 23) {
      val (ng, np) = dot(g5(23), p5(23), g5(15), p5(15))
      g6(i) := ng; p6(i) := np
    } else {
      g6(i) := g5(i); p6(i) := p5(i)
    }
  }

  // Stage 7: compute indices 11, 19, 27
  val g7 = Wire(Vec(32, Bool()))
  val p7 = Wire(Vec(32, Bool()))
  for (i <- 0 until 32) {
    if (i == 11) {
      val (ng, np) = dot(g6(11), p6(11), g6(7), p6(7))
      g7(i) := ng; p7(i) := np
    } else if (i == 19) {
      val (ng, np) = dot(g6(19), p6(19), g6(15), p6(15))
      g7(i) := ng; p7(i) := np
    } else if (i == 27) {
      val (ng, np) = dot(g6(27), p6(27), g6(23), p6(23))
      g7(i) := ng; p7(i) := np
    } else {
      g7(i) := g6(i); p7(i) := p6(i)
    }
  }

  // Stage 8: compute indices 5, 9, 13, 17, 21, 25, 29
  val g8 = Wire(Vec(32, Bool()))
  val p8 = Wire(Vec(32, Bool()))
  val stage8Targets = Seq(5 -> 3, 9 -> 7, 13 -> 11, 17 -> 15, 21 -> 19, 25 -> 23, 29 -> 27)
  val stage8Map = stage8Targets.toMap
  for (i <- 0 until 32) {
    if (stage8Map.contains(i)) {
      val src = stage8Map(i)
      val (ng, np) = dot(g7(i), p7(i), g7(src), p7(src))
      g8(i) := ng; p8(i) := np
    } else {
      g8(i) := g7(i); p8(i) := p7(i)
    }
  }

  // Stage 9: compute all remaining even indices: 2, 4, 6, ..., 30
  val g9 = Wire(Vec(32, Bool()))
  val p9 = Wire(Vec(32, Bool()))
  for (i <- 0 until 32) {
    if (i > 0 && i % 2 == 0) {
      val (ng, np) = dot(g8(i), p8(i), g8(i - 1), p8(i - 1))
      g9(i) := ng; p9(i) := np
    } else {
      g9(i) := g8(i); p9(i) := p8(i)
    }
  }

  // Compute carries
  val carries = Wire(Vec(33, Bool()))
  carries(0) := io.carryIn
  for (i <- 1 to 32) {
    carries(i) := g9(i - 1) | (p9(i - 1) & io.carryIn)
  }

  // Compute sum bits
  val sumBits = Wire(Vec(32, Bool()))
  for (i <- 0 until 32) {
    sumBits(i) := p0(i) ^ carries(i)
  }

  io.sum      := sumBits.asUInt
  io.carryOut := carries(32)
}

object BrentKungAdder {
  def apply(a: UInt, b: UInt, carryIn: Bool = false.B, width: Int = 32): (UInt, Bool) = {
    val adder = Module(new BrentKungAdder(width))
    adder.io.a := a
    adder.io.b := b
    adder.io.carryIn := carryIn
    (adder.io.sum, adder.io.carryOut)
  }
}
