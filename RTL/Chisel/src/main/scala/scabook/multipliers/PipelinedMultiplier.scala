// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.multipliers

import chisel3._
import chisel3.util._
import scabook.riscv.AluOp

/**
 * Stage 1 of the 2-Stage Pipelined Multiplier.
 *
 * Implements Radix-4 Modified Booth recoding (17 partial products)
 * followed by a 6-level Carry-Save Compressor (Wallace) Tree reduction.
 * Reduces 17 partial products down to two 64-bit intermediate vectors:
 * sumOut and carryOut, with zero carry propagation.
 */
class PipelinedMultiplierStage1(width: Int = 32) extends Module {
  val resWidth = 2 * width
  val numGroups = (width + 2) / 2 // 17 groups for 32-bit

  val io = IO(new Bundle {
    val a        = Input(UInt(width.W))
    val b        = Input(UInt(width.W))
    val mulOp    = Input(UInt(5.W))
    val sumOut   = Output(UInt(resWidth.W))
    val carryOut = Output(UInt(resWidth.W))
  })

  val isSignedA = (io.mulOp === AluOp.MULH) || (io.mulOp === AluOp.MULHSU)
  val isSignedB = (io.mulOp === AluOp.MULH)

  val aExt = Wire(UInt(resWidth.W))
  when(isSignedA) {
    aExt := Cat(Fill(width, io.a(width - 1)), io.a)
  }.otherwise {
    aExt := Cat(0.U(width.W), io.a)
  }

  val bExt = Wire(UInt((width + 2).W))
  when(isSignedB) {
    bExt := Cat(Fill(2, io.b(width - 1)), io.b)
  }.otherwise {
    bExt := Cat(0.U(2.W), io.b)
  }

  val bPadded = Cat(bExt, 0.U(1.W)) // {bExt, b_{-1} = 0}

  val ppList = Wire(Vec(numGroups, UInt(resWidth.W)))

  for (i <- 0 until numGroups) {
    val window = bPadded(2 * i + 2, 2 * i) // 3-bit window

    val singleA = aExt
    val doubleA = aExt << 1

    val ppVal = WireDefault(0.U(resWidth.W))
    switch(window) {
      is("b000".U) { ppVal := 0.U }
      is("b001".U) { ppVal := singleA }
      is("b010".U) { ppVal := singleA }
      is("b011".U) { ppVal := doubleA }
      is("b100".U) { ppVal := (~doubleA).asUInt + 1.U }
      is("b101".U) { ppVal := (~singleA).asUInt + 1.U }
      is("b110".U) { ppVal := (~singleA).asUInt + 1.U }
      is("b111".U) { ppVal := 0.U }
    }

    ppList(i) := (ppVal << (2 * i))(resWidth - 1, 0)
  }

  // 2D Carry-Save Tree reduction
  val initialCols = Array.fill(resWidth)(collection.mutable.ArrayBuffer[Bool]())
  for (i <- 0 until numGroups) {
    for (c <- 0 until resWidth) {
      initialCols(c).append(ppList(i)(c))
    }
  }

  var currentCols = initialCols.map(_.toSeq).toSeq
  def maxColHeight(cols: Seq[Seq[Bool]]): Int = cols.map(_.size).max

  while (maxColHeight(currentCols) > 2) {
    val nextCols = Array.fill(resWidth)(collection.mutable.ArrayBuffer[Bool]())
    val carriesFromPrev = Array.fill(resWidth)(collection.mutable.ArrayBuffer[Bool]())

    for (c <- 0 until resWidth) {
      val colBits = currentCols(c)
      var idx = 0
      while (idx + 2 < colBits.size) {
        val faSum = colBits(idx) ^ colBits(idx + 1) ^ colBits(idx + 2)
        val faCarry = (colBits(idx) & colBits(idx + 1)) |
                      (colBits(idx + 1) & colBits(idx + 2)) |
                      (colBits(idx) & colBits(idx + 2))
        nextCols(c).append(faSum)
        if (c + 1 < resWidth) carriesFromPrev(c + 1).append(faCarry)
        idx += 3
      }
      if (idx + 1 < colBits.size) {
        val haSum = colBits(idx) ^ colBits(idx + 1)
        val haCarry = colBits(idx) & colBits(idx + 1)
        nextCols(c).append(haSum)
        if (c + 1 < resWidth) carriesFromPrev(c + 1).append(haCarry)
        idx += 2
      }
      if (idx < colBits.size) {
        nextCols(c).append(colBits(idx))
        idx += 1
      }
    }

    for (c <- 0 until resWidth) {
      nextCols(c).appendAll(carriesFromPrev(c))
    }

    currentCols = nextCols.map(_.toSeq).toSeq
  }

  val vecSum = Wire(Vec(resWidth, Bool()))
  val vecCarry = Wire(Vec(resWidth, Bool()))

  for (c <- 0 until resWidth) {
    vecSum(c) := (if (currentCols(c).nonEmpty) currentCols(c)(0) else false.B)
    vecCarry(c) := (if (currentCols(c).size > 1) currentCols(c)(1) else false.B)
  }

  io.sumOut   := vecSum.asUInt
  io.carryOut := vecCarry.asUInt
}

/**
 * Stage 2 of the 2-Stage Pipelined Multiplier.
 *
 * Receives the intermediate sum and carry vectors from the pipeline register,
 * performs final vector-merging carry-propagate addition, and selects the
 * appropriate 32-bit slice (lower 32 bits for MUL, upper 32 bits for MULH*).
 */
class PipelinedMultiplierStage2(width: Int = 32) extends Module {
  val resWidth = 2 * width

  val io = IO(new Bundle {
    val sumIn   = Input(UInt(resWidth.W))
    val carryIn = Input(UInt(resWidth.W))
    val mulOp   = Input(UInt(5.W))
    val result  = Output(UInt(width.W))
  })

  val fullProduct = io.sumIn + io.carryIn

  io.result := Mux(
    io.mulOp === AluOp.MUL,
    fullProduct(width - 1, 0),
    fullProduct(resWidth - 1, width)
  )
}

/**
 * Complete 2-Stage Synchronous Pipelined Multiplier.
 *
 * Latency: 2 clock cycles.
 * Throughput: 1 multiplication per clock cycle.
 * Contains internal pipeline register between Stage 1 and Stage 2.
 */
class PipelinedMultiplier(width: Int = 32) extends Module {
  val io = IO(new Bundle {
    val a      = Input(UInt(width.W))
    val b      = Input(UInt(width.W))
    val mulOp  = Input(UInt(5.W))
    val result = Output(UInt(width.W))
  })

  val stage1 = Module(new PipelinedMultiplierStage1(width))
  stage1.io.a     := io.a
  stage1.io.b     := io.b
  stage1.io.mulOp := io.mulOp

  val regSum   = RegNext(stage1.io.sumOut)
  val regCarry = RegNext(stage1.io.carryOut)
  val regOp    = RegNext(io.mulOp)

  val stage2 = Module(new PipelinedMultiplierStage2(width))
  stage2.io.sumIn   := regSum
  stage2.io.carryIn := regCarry
  stage2.io.mulOp   := regOp

  io.result := stage2.io.result
}
