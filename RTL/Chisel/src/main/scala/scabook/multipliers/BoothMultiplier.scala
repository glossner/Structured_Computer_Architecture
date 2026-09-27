// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.multipliers

import chisel3._
import chisel3.util._

/**
 * Parameterized Radix-4 Modified Booth Multiplier.
 *
 * Employs Radix-4 Booth recoding with overlapping 3-bit windows
 * to halve the number of partial products from N to (N+2)/2.
 * Supports both signed and unsigned integer multiplication.
 */
class BoothMultiplier(width: Int = 32) extends Multiplier(width) {
  val resWidth = 2 * width
  val numGroups = (width + 2) / 2 // 17 groups for 32-bit (handles both signed and unsigned)

  val aExt = Wire(UInt(resWidth.W))
  val bExt = Wire(UInt((width + 2).W))

  when(io.isSigned === 1.U) {
    aExt := Cat(Fill(width, io.a(width - 1)), io.a)
    bExt := Cat(Fill(2, io.b(width - 1)), io.b)
  }.otherwise {
    aExt := Cat(0.U(width.W), io.a)
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

  // Sum all Booth partial products
  val sumTree = Wire(Vec(numGroups, UInt(resWidth.W)))
  sumTree(0) := ppList(0)
  for (i <- 1 until numGroups) {
    sumTree(i) := sumTree(i - 1) + ppList(i)
  }

  io.result := sumTree(numGroups - 1)
}

object BoothMultiplier {
  def apply(a: UInt, b: UInt, isSigned: UInt, width: Int = 32): UInt = {
    val mul = Module(new BoothMultiplier(width))
    mul.io.a := a
    mul.io.b := b
    mul.io.isSigned := isSigned
    mul.io.result
  }
}
