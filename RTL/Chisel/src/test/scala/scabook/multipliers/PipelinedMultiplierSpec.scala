// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.multipliers

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scabook.riscv.AluOp
import scala.util.Random

class PipelinedMultiplierSpec extends AnyFlatSpec with Matchers {

  def expectedResult(a: Long, b: Long, op: BigInt): Long = {
    val a32 = a & 0xFFFFFFFFL
    val b32 = b & 0xFFFFFFFFL
    val aSigned = if (a32 > 0x7FFFFFFFL) a32 - 0x100000000L else a32
    val bSigned = if (b32 > 0x7FFFFFFFL) b32 - 0x100000000L else b32
    val aUnsigned = BigInt(a32)
    val bUnsigned = BigInt(b32)

    val prod: BigInt = op.toInt match {
      case 0x10 => // MUL (AluOp.MUL = 16)
        aUnsigned * bUnsigned
      case 0x11 => // MULH (AluOp.MULH = 17)
        (BigInt(aSigned) * BigInt(bSigned)) >> 32
      case 0x12 => // MULHSU (AluOp.MULHSU = 18)
        (BigInt(aSigned) * bUnsigned) >> 32
      case 0x13 => // MULHU (AluOp.MULHU = 19)
        (aUnsigned * bUnsigned) >> 32
    }

    (prod & 0xFFFFFFFFL).toLong
  }

  "PipelinedMultiplier" should "correctly compute MUL, MULH, MULHSU, and MULHU with 1-cycle latency and 1-per-cycle throughput" in {
    simulate(new PipelinedMultiplier(32)) { dut =>
      val ops = Seq(AluOp.MUL, AluOp.MULH, AluOp.MULHSU, AluOp.MULHU)
      val sampleValues = Seq(
        0L, 1L, 2L, 100L, 65535L, 0x12345678L, 0x7FFFFFFFL,
        0x80000000L, 0xFFFFFFFFL, 0xAAAAAAAAAL, 0x55555555L
      )

      // Test combinations
      for (op <- ops) {
        for (a <- sampleValues) {
          for (b <- sampleValues) {
            dut.io.a.poke((a & 0xFFFFFFFFL).U(32.W))
            dut.io.b.poke((b & 0xFFFFFFFFL).U(32.W))
            dut.io.mulOp.poke(op)
            dut.clock.step(1)

            val actual = dut.io.result.peek().litValue.toLong & 0xFFFFFFFFL
            val expected = expectedResult(a, b, op.litValue) & 0xFFFFFFFFL
            assert(actual == expected, f"Mismatch for op=${op.litValue}%x a=0x$a%08x b=0x$b%08x: actual=0x$actual%08x expected=0x$expected%08x")
          }
        }
      }

      // Randomized streaming throughput test
      val rng = new Random(12345)
      case class Req(a: Long, b: Long, op: BigInt, expected: Long)
      val reqs = (0 until 100).map { _ =>
        val a = rng.nextInt().toLong & 0xFFFFFFFFL
        val b = rng.nextInt().toLong & 0xFFFFFFFFL
        val op = ops(rng.nextInt(ops.length))
        val exp = expectedResult(a, b, op.litValue) & 0xFFFFFFFFL
        Req(a, b, op.litValue, exp)
      }

      // Stream requests back-to-back: latency 1 clock cycle
      // Cycle 0: drive req(0)
      dut.io.a.poke(reqs(0).a.U(32.W))
      dut.io.b.poke(reqs(0).b.U(32.W))
      dut.io.mulOp.poke(reqs(0).op.U(5.W))
      dut.clock.step(1)

      for (i <- 1 until reqs.length) {
        // Read response for req(i-1)
        val actual = dut.io.result.peek().litValue.toLong & 0xFFFFFFFFL
        assert(actual == reqs(i - 1).expected, f"Streaming mismatch at index ${i - 1}: actual=0x$actual%08x expected=0x${reqs(i - 1).expected}%08x")

        // Drive req(i)
        dut.io.a.poke(reqs(i).a.U(32.W))
        dut.io.b.poke(reqs(i).b.U(32.W))
        dut.io.mulOp.poke(reqs(i).op.U(5.W))
        dut.clock.step(1)
      }

      // Final response
      val finalActual = dut.io.result.peek().litValue.toLong & 0xFFFFFFFFL
      assert(finalActual == reqs.last.expected, f"Final streaming mismatch: actual=0x$finalActual%08x expected=0x${reqs.last.expected}%08x")
    }
  }
}
