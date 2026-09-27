// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.multipliers

import chisel3._
import chisel3.util._

/**
 * Redundant Binary Adder (RBA) Cell.
 *
 * Adds two redundant binary digits X = (xPlus, xMinus) and Y = (yPlus, yMinus)
 * with digit values in {-1, 0, 1}.
 * Uses adjacent lower digit condition (pPrev = (x_{i-1} + y_{i-1} > 0)) to select
 * intermediate carry and sum, guaranteeing carry-free addition in strictly O(1) time.
 */
class RedundantBinaryAdder(width: Int) extends Module {
  val io = IO(new Bundle {
    val xPlus  = Input(UInt(width.W))
    val xMinus = Input(UInt(width.W))
    val yPlus  = Input(UInt(width.W))
    val yMinus = Input(UInt(width.W))
    val zPlus  = Output(UInt((width + 1).W))
    val zMinus = Output(UInt((width + 1).W))
  })

  // p(i) indicates whether the lower digit (i-1) sum is strictly positive
  val p = Wire(Vec(width + 1, Bool()))
  p(0) := false.B
  for (i <- 0 until width) {
    val sumPos = Wire(UInt(2.W))
    val sumNeg = Wire(UInt(2.W))
    sumPos := io.xPlus(i).asUInt +& io.yPlus(i).asUInt
    sumNeg := io.xMinus(i).asUInt +& io.yMinus(i).asUInt
    p(i + 1) := sumPos > sumNeg
  }

  val cPos = Wire(Vec(width + 1, Bool()))
  val cNeg = Wire(Vec(width + 1, Bool()))
  val sPos = Wire(Vec(width, Bool()))
  val sNeg = Wire(Vec(width, Bool()))

  cPos(0) := false.B
  cNeg(0) := false.B

  for (i <- 0 until width) {
    val sp = io.xPlus(i).asUInt +& io.yPlus(i).asUInt
    val sn = io.xMinus(i).asUInt +& io.yMinus(i).asUInt
    val pi = p(i)

    val cp = WireDefault(false.B)
    val cn = WireDefault(false.B)
    val spOut = WireDefault(false.B)
    val snOut = WireDefault(false.B)

    when(sp === 2.U && sn === 0.U) {
      cp := true.B
    }.elsewhen(sp === 0.U && sn === 2.U) {
      cn := true.B
    }.elsewhen((sp === 1.U && sn === 0.U) || (sp === 2.U && sn === 1.U)) {
      when(pi) {
        cp := true.B
        snOut := true.B
      }.otherwise {
        spOut := true.B
      }
    }.elsewhen((sp === 0.U && sn === 1.U) || (sp === 1.U && sn === 2.U)) {
      when(pi) {
        snOut := true.B
      }.otherwise {
        cn := true.B
        spOut := true.B
      }
    }

    cPos(i + 1) := cp
    cNeg(i + 1) := cn
    sPos(i)     := spOut
    sNeg(i)     := snOut
  }

  // Second stage: Combine intermediate sum s(i) and intermediate carry c(i)
  val zP = Wire(Vec(width + 1, Bool()))
  val zM = Wire(Vec(width + 1, Bool()))

  for (i <- 0 to width) {
    val sp_i = if (i < width) sPos(i) else false.B
    val sn_i = if (i < width) sNeg(i) else false.B
    val cp_i = cPos(i)
    val cn_i = cNeg(i)

    val inPos = sp_i || cp_i
    val inNeg = sn_i || cn_i

    zP(i) := inPos && !inNeg
    zM(i) := inNeg && !inPos
  }

  io.zPlus  := zP.asUInt
  io.zMinus := zM.asUInt
}

/**
 * 32-bit Redundant Binary Multiplier (RBM).
 *
 * Employs a Redundant Binary Signed-Digit (RBSD) representation with digit set {-1, 0, 1}.
 * All partial products are mapped into redundant binary format and reduced via a balanced
 * binary tree of Redundant Binary Adders (RBAs).
 * Because RBA addition is strictly carry-free (O(1) logic depth per tree level),
 * the reduction tree exhibits perfect O(log2 N) delay and modular, highly regular layout
 * ideal for high-performance DSP designs.
 * A final high-speed subtractor/adder converts the root RB pair (P+, P-) to standard binary.
 */
class RedundantBinaryMultiplier(width: Int = 32) extends Multiplier(width) {
  val resWidth = 2 * width
  val aExt = Wire(UInt(resWidth.W))

  when(io.isSigned === 1.U) {
    aExt := Cat(Fill(width, io.a(width - 1)), io.a)
  }.otherwise {
    aExt := Cat(0.U(width.W), io.a)
  }

  val ppPlus  = Wire(Vec(width, UInt(resWidth.W)))
  val ppMinus = Wire(Vec(width, UInt(resWidth.W)))

  for (i <- 0 until width - 1) {
    ppPlus(i)  := Mux(io.b(i), (aExt << i)(resWidth - 1, 0), 0.U)
    ppMinus(i) := 0.U
  }

  val lastRow = WireDefault(0.U(resWidth.W))
  when(io.b(width - 1)) {
    lastRow := (aExt << (width - 1))(resWidth - 1, 0)
  }

  when(io.isSigned === 1.U) {
    ppPlus(width - 1)  := 0.U
    ppMinus(width - 1) := lastRow
  }.otherwise {
    ppPlus(width - 1)  := lastRow
    ppMinus(width - 1) := 0.U
  }

  // Binary RBA Reduction Tree
  case class RBPair(p: UInt, m: UInt)

  var currentLevel: Seq[RBPair] = (0 until width).map(i => RBPair(ppPlus(i), ppMinus(i)))

  while (currentLevel.size > 1) {
    val nextLevel = collection.mutable.ArrayBuffer[RBPair]()
    var idx = 0
    while (idx < currentLevel.size) {
      if (idx + 1 < currentLevel.size) {
        val rba = Module(new RedundantBinaryAdder(resWidth))
        rba.io.xPlus  := currentLevel(idx).p
        rba.io.xMinus := currentLevel(idx).m
        rba.io.yPlus  := currentLevel(idx + 1).p
        rba.io.yMinus := currentLevel(idx + 1).m
        nextLevel.append(RBPair(rba.io.zPlus(resWidth - 1, 0), rba.io.zMinus(resWidth - 1, 0)))
        idx += 2
      } else {
        nextLevel.append(currentLevel(idx))
        idx += 1
      }
    }
    currentLevel = nextLevel.toSeq
  }

  // Phase 3: Final Redundant Binary to Two's Complement Converter
  val finalPlus  = currentLevel.head.p
  val finalMinus = currentLevel.head.m
  val finalProduct = finalPlus - finalMinus

  io.result := finalProduct
}

object RedundantBinaryMultiplier {
  def apply(a: UInt, b: UInt, isSigned: UInt, width: Int = 32): UInt = {
    val mul = Module(new RedundantBinaryMultiplier(width))
    mul.io.a := a
    mul.io.b := b
    mul.io.isSigned := isSigned
    mul.io.result
  }
}
