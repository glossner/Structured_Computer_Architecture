// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.adders

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class AdderSuiteSpec extends AnyFlatSpec {

  val testVectors: Seq[(Long, Long, Boolean)] = Seq(
    (0L, 0L, false),
    (0L, 0L, true),
    (12345L, 67890L, false),
    (12345L, 67890L, true),
    (0xFFFFFFFFL, 1L, false),
    (0xFFFFFFFFL, 1L, true),
    (0xAAAAAAAAL, 0x55555555L, false),
    (0xAAAAAAAAL, 0x55555555L, true),
    (0x12345678L, 0x9abcdef0L, false),
    (0x12345678L, 0x9abcdef0L, true),
    (0xFFFFFFFFL, 0xFFFFFFFFL, false),
    (0xFFFFFFFFL, 0xFFFFFFFFL, true),
    (0x80000000L, 0x80000000L, false),
    (0x7FFFFFFFL, 1L, false)
  )

  def checkAdder(name: String, dutCreate: () => Module {
    val io: Bundle {
      val a: UInt
      val b: UInt
      val carryIn: Bool
      val sum: UInt
      val carryOut: Bool
    }
  }): Unit = {
    name should "correctly compute 32-bit addition across all test vectors" in {
      simulate(dutCreate()) { dut =>
        for ((a, b, cin) <- testVectors) {
          dut.io.a.poke(a.U(32.W))
          dut.io.b.poke(b.U(32.W))
          dut.io.carryIn.poke(cin.B)
          dut.clock.step()

          val expectedFull = a + b + (if (cin) 1L else 0L)
          val expectedSum  = expectedFull & 0xFFFFFFFFL
          val expectedCout = (expectedFull >> 32) & 1L

          val actualSum  = dut.io.sum.peek().litValue.toLong
          val actualCout = if (dut.io.carryOut.peek().litToBoolean) 1L else 0L

          assert(actualSum == expectedSum, f"Mismatch in $name sum: $a%x + $b%x + $cin%b = expected $expectedSum%x, got $actualSum%x")
          assert(actualCout == expectedCout, f"Mismatch in $name cout: $a%x + $b%x + $cin%b = expected $expectedCout, got $actualCout")
        }
      }
    }
  }

  checkAdder("KoggeStoneAdder", () => new KoggeStoneAdder(32))
  checkAdder("BrentKungAdder", () => new BrentKungAdder(32))
  checkAdder("CarrySelectAdder", () => new CarrySelectAdder(32, 4))
  checkAdder("CarryLookAheadAdder", () => new CarryLookAheadAdder(32))
}
