// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.memory

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class L1CacheSpec extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "L1Cache"

  it should "detect cold miss, request refill, and hit on subsequent reads" in {
    test(new L1Cache(sizeBytes = 1024, ways = 2, lineBytes = 32, isDcache = false)) { dut =>
      // 1. Initial access to 0x80000000 (cold miss)
      dut.io.req.valid.poke(true.B)
      dut.io.req.bits.addr.poke("h80000000".U)
      dut.io.req.bits.funct3.poke(2.U)
      dut.io.req.bits.isWrite.poke(false.B)
      dut.io.req.bits.writeData.poke(0.U)
      dut.clock.step(1)

      // Miss asserted
      dut.io.stall.expect(true.B)
      dut.io.memReq.valid.expect(true.B)
      dut.io.memReq.bits.addr.expect("h80000000".U)

      // 2. Memory responds with refill data (8 words = 32 bytes)
      // Word 0 = 0x11111111, Word 1 = 0x22222222, etc.
      val refillData = BigInt("8888888877777777666666665555555544444444333333332222222211111111", 16)
      dut.io.memReq.ready.poke(true.B)
      dut.clock.step(1)

      dut.io.memReq.ready.poke(false.B)
      dut.io.memResp.valid.poke(true.B)
      dut.io.memResp.bits.ready.poke(true.B)
      dut.io.memResp.bits.readData.poke(refillData.U)
      dut.clock.step(1)

      // 3. Refill complete -> Response delivered
      dut.io.stall.expect(false.B)
      dut.io.resp.valid.expect(true.B)
      dut.io.resp.bits.readData.expect("h11111111".U)

      // Deassert memory response
      dut.io.memResp.valid.poke(false.B)

      // 4. Query 0x80000004 (Word 1 in same cache line) -> MUST HIT in 1 cycle!
      dut.io.req.valid.poke(true.B)
      dut.io.req.bits.addr.poke("h80000004".U)
      dut.io.req.bits.funct3.poke(2.U)
      dut.clock.step(1)

      dut.io.stall.expect(false.B)
      dut.io.resp.valid.expect(true.B)
      dut.io.resp.bits.readData.expect("h22222222".U)
      dut.io.resp.bits.hit.expect(true.B)
    }
  }

  it should "handle D-Cache writes, write-allocate on miss, and writeback on eviction" in {
    test(new L1Cache(sizeBytes = 256, ways = 1, lineBytes = 32, isDcache = true)) { dut =>
      // Direct mapped with 256 bytes / 32 bytes = 8 sets
      // Address 0x80000000 maps to set 0. Address 0x80000100 also maps to set 0 (aliasing/conflict!)

      // 1. Read miss at 0x80000000
      dut.io.req.valid.poke(true.B)
      dut.io.req.bits.addr.poke("h80000000".U)
      dut.io.req.bits.funct3.poke(2.U)
      dut.io.req.bits.isWrite.poke(false.B)
      dut.clock.step(1)

      dut.io.memReq.ready.poke(true.B)
      dut.clock.step(1)

      dut.io.memReq.ready.poke(false.B)
      dut.io.memResp.valid.poke(true.B)
      dut.io.memResp.bits.ready.poke(true.B)
      dut.io.memResp.bits.readData.poke(BigInt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 16).U)
      dut.clock.step(1)

      dut.io.memResp.valid.poke(false.B)

      // 2. Write to 0x80000000 (Store Word: 0x12345678) -> Cache line becomes DIRTY
      dut.io.req.valid.poke(true.B)
      dut.io.req.bits.addr.poke("h80000000".U)
      dut.io.req.bits.funct3.poke(2.U)
      dut.io.req.bits.isWrite.poke(true.B)
      dut.io.req.bits.writeData.poke("h12345678".U)
      dut.clock.step(1)

      dut.io.resp.bits.hit.expect(true.B)

      // 3. Read 0x80000100 (conflicts with set 0) -> Eviction of dirty line at 0x80000000!
      dut.io.req.valid.poke(true.B)
      dut.io.req.bits.addr.poke("h80000100".U)
      dut.io.req.bits.funct3.poke(2.U)
      dut.io.req.bits.isWrite.poke(false.B)
      dut.clock.step(1)

      // Must request writeback to 0x80000000 with the modified word!
      dut.io.stall.expect(true.B)
      dut.io.memReq.valid.expect(true.B)
      dut.io.memReq.bits.addr.expect("h80000000".U)
      dut.io.memReq.bits.isWrite.expect(true.B)
    }
  }
}
