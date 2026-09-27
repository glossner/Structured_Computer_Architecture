// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.adders

import chisel3._
import chisel3.util._

/**
 * 32-bit Hierarchical Carry Lookahead Adder (CLA).
 *
 * Decomposes 32 bits into 8 4-bit CLA blocks. Within each block, carries are
 * evaluated combinationally using parallel lookahead equations. A second-level
 * lookahead generator evaluates the block-level carries (c4, c8, ..., c32) in parallel.
 */
class CarryLookAheadAdder(val width: Int = 32) extends Module {
  require(width % 4 == 0, "Width must be a multiple of 4 for 4-bit block CLA")
  val numBlocks = width / 4

  val io = IO(new Bundle {
    val a        = Input(UInt(width.W))
    val b        = Input(UInt(width.W))
    val carryIn  = Input(Bool())
    val sum      = Output(UInt(width.W))
    val carryOut = Output(Bool())
  })

  // Bit-level generate and propagate
  val g = Wire(Vec(width, Bool()))
  val p = Wire(Vec(width, Bool()))
  for (i <- 0 until width) {
    g(i) := io.a(i) & io.b(i)
    p(i) := io.a(i) ^ io.b(i)
  }

  // Block generate and propagate for each 4-bit block
  val gBlk = Wire(Vec(numBlocks, Bool()))
  val pBlk = Wire(Vec(numBlocks, Bool()))

  for (k <- 0 until numBlocks) {
    val b = k * 4
    pBlk(k) := p(b + 3) & p(b + 2) & p(b + 1) & p(b)
    gBlk(k) := g(b + 3) |
               (p(b + 3) & g(b + 2)) |
               (p(b + 3) & p(b + 2) & g(b + 1)) |
               (p(b + 3) & p(b + 2) & p(b + 1) & g(b))
  }

  // Block-level carry computation (Lookahead Carry Generator)
  val blkCarries = Wire(Vec(numBlocks + 1, Bool()))
  blkCarries(0) := io.carryIn
  for (k <- 1 to numBlocks) {
    // c_k = gBlk(k-1) | (pBlk(k-1) & c_{k-1})
    blkCarries(k) := gBlk(k - 1) | (pBlk(k - 1) & blkCarries(k - 1))
  }

  // Intra-block carries
  val carries = Wire(Vec(width + 1, Bool()))
  for (k <- 0 until numBlocks) {
    val b = k * 4
    val c0 = blkCarries(k)
    carries(b)     := c0
    carries(b + 1) := g(b) | (p(b) & c0)
    carries(b + 2) := g(b + 1) | (p(b + 1) & g(b)) | (p(b + 1) & p(b) & c0)
    carries(b + 3) := g(b + 2) | (p(b + 2) & g(b + 1)) | (p(b + 2) & p(b + 1) & g(b)) | (p(b + 2) & p(b + 1) & p(b) & c0)
  }
  carries(width) := blkCarries(numBlocks)

  // Sum bits
  val sumBits = Wire(Vec(width, Bool()))
  for (i <- 0 until width) {
    sumBits(i) := p(i) ^ carries(i)
  }

  io.sum      := sumBits.asUInt
  io.carryOut := carries(width)
}

object CarryLookAheadAdder {
  def apply(a: UInt, b: UInt, carryIn: Bool = false.B, width: Int = 32): (UInt, Bool) = {
    val adder = Module(new CarryLookAheadAdder(width))
    adder.io.a := a
    adder.io.b := b
    adder.io.carryIn := carryIn
    (adder.io.sum, adder.io.carryOut)
  }
}
