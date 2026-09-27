// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.multipliers

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.Random

class MultiplierSuiteSpec extends AnyFlatSpec with Matchers {
  val testVectors = Seq(
    // (a, b, isSigned)
    (0L, 0L, 0),
    (0L, 12345678L, 0),
    (12345678L, 0L, 0),
    (1L, 1L, 0),
    (1L, 0xFFFFFFFFL, 0),
    (0xFFFFFFFFL, 1L, 0),
    (100L, 200L, 0),
    (123456L, 654321L, 0),
    (0x12345678L, 0x87654321L, 0),
    (0xFFFFFFFFL, 0xFFFFFFFFL, 0),
    (0x7FFFFFFFL, 0x7FFFFFFFL, 0),
    (0x55555555L, 0xAAAAAAAAAL, 0),
    // Signed tests
    (0L, 0L, 1),
    (1L, 1L, 1),
    (100L, -200L, 1),
    (-100L, 200L, 1),
    (-100L, -200L, 1),
    (-1L, -1L, 1),
    (-1L, 123456L, 1),
    (0x7FFFFFFFL, 2L, 1),
    (-0x80000000L, 1L, 1),
    (-0x80000000L, -1L, 1),
    (-123456L, 654321L, 1),
    (0x12345678L, -0x12345678L, 1)
  )

  def expectedProduct(a: Long, b: Long, isSigned: Int): BigInt = {
    if (isSigned == 1) {
      val aSigned = if (a > 0x7FFFFFFFL) a - 0x100000000L else a
      val bSigned = if (b > 0x7FFFFFFFL) b - 0x100000000L else b
      val prod = BigInt(aSigned) * BigInt(bSigned)
      // Represent in 64-bit unsigned BigInt
      if (prod < 0) (BigInt(1) << 64) + prod else prod
    } else {
      (BigInt(a) & 0xFFFFFFFFL) * (BigInt(b) & 0xFFFFFFFFL)
    }
  }

  def testMultiplier(name: String, createDut: => Multiplier): Unit = {
    name should "correctly compute 32-bit signed and unsigned multiplication across test vectors" in {
      simulate(createDut) { dut =>
        for ((a, b, isSigned) <- testVectors) {
          dut.io.a.poke((a & 0xFFFFFFFFL).U(32.W))
          dut.io.b.poke((b & 0xFFFFFFFFL).U(32.W))
          dut.io.isSigned.poke(isSigned.U(1.W))

          val actual = dut.io.result.peek().litValue
          val expected = expectedProduct(a, b, isSigned)
          assert(actual == expected, s"$name mismatch: a=$a, b=$b, isSigned=$isSigned: actual=$actual, expected=$expected")
        }

        // Random tests
        val rng = new Random(42)
        for (_ <- 0 until 50) {
          val a = rng.nextInt().toLong & 0xFFFFFFFFL
          val b = rng.nextInt().toLong & 0xFFFFFFFFL
          val isSigned = rng.nextInt(2)

          dut.io.a.poke(a.U(32.W))
          dut.io.b.poke(b.U(32.W))
          dut.io.isSigned.poke(isSigned.U(1.W))

          val actual = dut.io.result.peek().litValue
          val expected = expectedProduct(a, b, isSigned)
          assert(actual == expected, s"$name random mismatch: a=$a, b=$b, isSigned=$isSigned: actual=$actual, expected=$expected")
        }
      }
    }
  }

  testMultiplier("ArrayMultiplier", new ArrayMultiplier(32))
  testMultiplier("BoothMultiplier", new BoothMultiplier(32))
  testMultiplier("WallaceTreeMultiplier", new WallaceTreeMultiplier(32))
  testMultiplier("RedundantBinaryMultiplier", new RedundantBinaryMultiplier(32))
}
