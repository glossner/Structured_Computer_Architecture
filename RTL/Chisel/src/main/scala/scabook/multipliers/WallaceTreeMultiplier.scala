// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.multipliers

import chisel3._
import chisel3.util._

/**
 * 32-bit Wallace Tree Multiplier.
 *
 * Employs multi-stage 3:2 carry-save compressors (Full Adders) and 2:2 compressors
 * (Half Adders) to reduce N partial products down to two vectors in O(log_{1.5} N)
 * logic depth, followed by a final carry-propagate addition.
 */
class WallaceTreeMultiplier(width: Int = 32) extends Multiplier(width) {
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

  // Generate 2D bit grid of partial products
  // columns(k) holds the list of bits contributing to weight 2^k
  val initialCols = Array.fill(resWidth)(collection.mutable.ArrayBuffer[Bool]())

  for (i <- 0 until width) {
    for (c <- 0 until resWidth) {
      initialCols(c).append(pp(i)(c))
    }
  }

  // Iterative Wallace tree reduction
  var currentCols = initialCols.map(_.toSeq).toSeq

  def maxColHeight(cols: Seq[Seq[Bool]]): Int = cols.map(_.size).max

  while (maxColHeight(currentCols) > 2) {
    val nextCols = Array.fill(resWidth)(collection.mutable.ArrayBuffer[Bool]())
    val carriesFromPrev = Array.fill(resWidth)(collection.mutable.ArrayBuffer[Bool]())

    for (c <- 0 until resWidth) {
      val colBits = currentCols(c)
      var idx = 0
      while (idx + 2 < colBits.size) {
        // 3:2 Compressor (Full Adder)
        val faSum = colBits(idx) ^ colBits(idx + 1) ^ colBits(idx + 2)
        val faCarry = (colBits(idx) & colBits(idx + 1)) |
                      (colBits(idx + 1) & colBits(idx + 2)) |
                      (colBits(idx) & colBits(idx + 2))
        nextCols(c).append(faSum)
        if (c + 1 < resWidth) carriesFromPrev(c + 1).append(faCarry)
        idx += 3
      }
      if (idx + 1 < colBits.size) {
        // 2:2 Compressor (Half Adder)
        val haSum = colBits(idx) ^ colBits(idx + 1)
        val haCarry = colBits(idx) & colBits(idx + 1)
        nextCols(c).append(haSum)
        if (c + 1 < resWidth) carriesFromPrev(c + 1).append(haCarry)
        idx += 2
      }
      if (idx < colBits.size) {
        // Passthrough bit
        nextCols(c).append(colBits(idx))
        idx += 1
      }
    }

    // Merge carries into nextCols
    for (c <- 0 until resWidth) {
      nextCols(c).appendAll(carriesFromPrev(c))
    }

    currentCols = nextCols.map(_.toSeq).toSeq
  }

  // Exactly two rows remain (Sum vector and Carry vector)
  val vecSum = Wire(Vec(resWidth, Bool()))
  val vecCarry = Wire(Vec(resWidth, Bool()))

  for (c <- 0 until resWidth) {
    vecSum(c) := (if (currentCols(c).nonEmpty) currentCols(c)(0) else false.B)
    vecCarry(c) := (if (currentCols(c).size > 1) currentCols(c)(1) else false.B)
  }

  val finalProduct = vecSum.asUInt + vecCarry.asUInt
  io.result := finalProduct
}

object WallaceTreeMultiplier {
  def apply(a: UInt, b: UInt, isSigned: UInt, width: Int = 32): UInt = {
    val mul = Module(new WallaceTreeMultiplier(width))
    mul.io.a := a
    mul.io.b := b
    mul.io.isSigned := isSigned
    mul.io.result
  }
}
