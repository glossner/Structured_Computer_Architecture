// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.adders

import chisel3._
import chisel3.util._

/**
 * 32-bit Carry Select Adder (CSA).
 *
 * Partitions addition into 4-bit blocks. For blocks 1..7, two additions
 * are evaluated concurrently (assuming Cin=0 and Cin=1). A multiplexer
 * selects the correct sum and carry-out once the preceding carry arrives.
 */
class CarrySelectAdder(val width: Int = 32, val blockSize: Int = 4) extends Module {
  require(width % blockSize == 0, "Width must be a multiple of blockSize")
  val numBlocks = width / blockSize

  val io = IO(new Bundle {
    val a        = Input(UInt(width.W))
    val b        = Input(UInt(width.W))
    val carryIn  = Input(Bool())
    val sum      = Output(UInt(width.W))
    val carryOut = Output(Bool())
  })

  val blockCarries = Wire(Vec(numBlocks + 1, Bool()))
  blockCarries(0) := io.carryIn

  val sumBlocks = Wire(Vec(numBlocks, UInt(blockSize.W)))

  // Helper 4-bit ripple adder
  def add4(a: UInt, b: UInt, cin: Bool): (UInt, Bool) = {
    val extA = Cat(0.U(1.W), a)
    val extB = Cat(0.U(1.W), b)
    val res  = extA + extB + cin.asUInt
    (res(blockSize - 1, 0), res(blockSize))
  }

  // Block 0: single addition with actual carryIn
  val a0 = io.a(blockSize - 1, 0)
  val b0 = io.b(blockSize - 1, 0)
  val (s0, c0) = add4(a0, b0, io.carryIn)
  sumBlocks(0)    := s0
  blockCarries(1) := c0

  // Blocks 1 until numBlocks: dual speculative adders
  for (k <- 1 until numBlocks) {
    val aK = io.a((k + 1) * blockSize - 1, k * blockSize)
    val bK = io.b((k + 1) * blockSize - 1, k * blockSize)

    val (sumCin0, cout0) = add4(aK, bK, false.B)
    val (sumCin1, cout1) = add4(aK, bK, true.B)

    val cin = blockCarries(k)
    sumBlocks(k)        := Mux(cin, sumCin1, sumCin0)
    blockCarries(k + 1) := Mux(cin, cout1, cout0)
  }

  io.sum      := sumBlocks.asUInt
  io.carryOut := blockCarries(numBlocks)
}

object CarrySelectAdder {
  def apply(a: UInt, b: UInt, carryIn: Bool = false.B, width: Int = 32): (UInt, Bool) = {
    val adder = Module(new CarrySelectAdder(width))
    adder.io.a := a
    adder.io.b := b
    adder.io.carryIn := carryIn
    (adder.io.sum, adder.io.carryOut)
  }
}
