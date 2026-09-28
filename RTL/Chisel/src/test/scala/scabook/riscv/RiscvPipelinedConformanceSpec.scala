// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.riscv

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ArrayBuffer

class RiscvPipelinedConformanceSpec extends AnyFlatSpec {

  class SimMemory(val baseAddr: Long, val sizeBytes: Int = 4 * 1024 * 1024) {
    val bytes = new Array[Byte](sizeBytes)

    def loadBinary(data: Array[Byte]): Unit = {
      System.arraycopy(data, 0, bytes, 0, math.min(data.length, sizeBytes))
    }

    def offset(addr: Long): Int = (addr - baseAddr).toInt

    def readByte(addr: Long): Byte = {
      val off = offset(addr)
      if (off >= 0 && off < sizeBytes) bytes(off) else 0.toByte
    }

    def read32(addr: Long): Long = {
      val b0 = readByte(addr) & 0xffL
      val b1 = readByte(addr + 1) & 0xffL
      val b2 = readByte(addr + 2) & 0xffL
      val b3 = readByte(addr + 3) & 0xffL
      b0 | (b1 << 8) | (b2 << 16) | (b3 << 24)
    }

    def readFormatted(addr: Long, funct3: Int): Long = {
      funct3 match {
        case 0 => // LB
          val b = readByte(addr).toLong
          b & 0xFFFFFFFFL
        case 1 => // LH
          val b0 = readByte(addr) & 0xffL
          val b1 = readByte(addr + 1) & 0xffL
          val h = (b0 | (b1 << 8)).toShort.toLong
          h & 0xFFFFFFFFL
        case 2 => // LW
          read32(addr) & 0xFFFFFFFFL
        case 4 => // LBU
          (readByte(addr) & 0xffL) & 0xFFFFFFFFL
        case 5 => // LHU
          val b0 = readByte(addr) & 0xffL
          val b1 = readByte(addr + 1) & 0xffL
          (b0 | (b1 << 8)) & 0xFFFFFFFFL
        case _ =>
          read32(addr) & 0xFFFFFFFFL
      }
    }

    def writeByte(addr: Long, value: Byte): Unit = {
      val off = offset(addr)
      if (off >= 0 && off < sizeBytes) {
        bytes(off) = value
      }
    }

    def writeFormatted(addr: Long, data: Long, funct3: Int): Unit = {
      funct3 match {
        case 0 => // SB
          writeByte(addr, (data & 0xff).toByte)
        case 1 => // SH
          writeByte(addr, (data & 0xff).toByte)
          writeByte(addr + 1, ((data >> 8) & 0xff).toByte)
        case 2 => // SW
          writeByte(addr, (data & 0xff).toByte)
          writeByte(addr + 1, ((data >> 8) & 0xff).toByte)
          writeByte(addr + 2, ((data >> 16) & 0xff).toByte)
          writeByte(addr + 3, ((data >> 24) & 0xff).toByte)
        case _ =>
      }
    }

    def dumpSignature(beginAddr: Long, endAddr: Long): Seq[String] = {
      val sig = ArrayBuffer[String]()
      var a = beginAddr
      while (a < endAddr) {
        val word = read32(a)
        sig += f"${word & 0xFFFFFFFFL}%08x"
        a += 4
      }
      sig.toSeq
    }
  }

  def runTest(testName: String, tohostAddr: Long, beginSig: Long, endSig: Long, maxCycles: Int = 150000, enableZmmul: Boolean = true, branchPredictor: String = "none", enableForwarding: Boolean = true): Int = {
    val binPath = Paths.get(s"src/test/resources/conformance/${testName}.bin")
    val refPath = Paths.get(s"src/test/resources/conformance/${testName}.reference.sig")
    assert(Files.exists(binPath), s"Test binary $binPath not found")
    assert(Files.exists(refPath), s"Reference signature $refPath not found")

    val binBytes = Files.readAllBytes(binPath)
    val refLines = Files.readAllLines(refPath).toArray.map(_.toString.trim).filter(_.nonEmpty).toSeq

    val baseAddr = 0x80000000L
    val mem = new SimMemory(baseAddr, 4 * 1024 * 1024)
    mem.loadBinary(binBytes)

    var cyclesRan = 0

    simulate(new RiscvPipelined(xlen = 32, initPC = baseAddr, enableZmmul = enableZmmul, enableForwarding = enableForwarding, branchPredictor = branchPredictor)) { dut =>
      dut.reset.poke(true.B)
      dut.clock.step(5)
      dut.reset.poke(false.B)

      var cycles = 0
      var completed = false
      var tohostVal = 0L

      while (cycles < maxCycles && !completed) {
        val pc = dut.io.imem.addr.peek().litValue.toLong
        val inst = mem.read32(pc)
        dut.io.imem.inst.poke(inst.U(32.W))

        val dmemAddr   = dut.io.dmem.addr.peek().litValue.toLong
        val dmemFunct3 = dut.io.dmem.funct3.peek().litValue.toInt
        val memRead    = dut.io.dmem.memRead.peek().litToBoolean
        val memWrite   = dut.io.dmem.memWrite.peek().litToBoolean
        val writeData  = dut.io.dmem.writeData.peek().litValue.toLong

        if (memRead) {
          val rdata = mem.readFormatted(dmemAddr, dmemFunct3)
          dut.io.dmem.readData.poke(rdata.U(32.W))
        } else {
          dut.io.dmem.readData.poke(0.U(32.W))
        }

        if (memWrite) {
          mem.writeFormatted(dmemAddr, writeData, dmemFunct3)
          if (dmemAddr == tohostAddr && writeData != 0) {
            tohostVal = writeData
            completed = true
          }
        }

        dut.clock.step(1)
        cycles += 1
      }

      assert(completed, s"Simulation timed out after $cycles cycles without writing to tohost (PC was 0x${dut.io.imem.addr.peek().litValue.toString(16)})")
      assert(tohostVal == 1L, s"Test $testName reported failure with tohost = 0x${tohostVal.toHexString}")

      val dutSig = mem.dumpSignature(beginSig, endSig)
      assert(dutSig == refLines, s"Signature mismatch in $testName! DUT had ${dutSig.length} lines, REF had ${refLines.length} lines")
      println(f"  [PASS] $testName%-15s in $cycles%6d cycles (${dutSig.length} signature words verified)")
      cyclesRan = cycles
    }
    cyclesRan
  }

  // Load test suite metadata
  val metaPath = Paths.get("src/test/resources/conformance/test_metadata.csv")
  if (Files.exists(metaPath)) {
    val lines = Files.readAllLines(metaPath).toArray.map(_.toString).toSeq
    val testEntries = lines.drop(1).filter(_.trim.nonEmpty).map { line =>
      val parts = line.split(",").map(_.trim)
      val name = parts(0)
      val tohost = java.lang.Long.decode(parts(1)).longValue()
      val beginSig = java.lang.Long.decode(parts(2)).longValue()
      val endSig = java.lang.Long.decode(parts(3)).longValue()
      (name, tohost, beginSig, endSig)
    }

    val rv32iEntries = testEntries.filter(!_._1.contains("mul"))
    val zmmulEntries = testEntries.filter(_._1.contains("mul"))

    "RiscvPipelined" should "pass all 38 official RV32I architectural conformance tests (enableZmmul = false)" in {
      println(s"\nExecuting ${rv32iEntries.length} official RV32I architectural conformance tests on RiscvPipelined (pure RV32I, no multiplier):")
      var totalCycles = 0
      for ((name, tohost, beginSig, endSig) <- rv32iEntries) {
        val c = runTest(name, tohost, beginSig, endSig, enableZmmul = false)
        totalCycles += c
      }
      println(f"\nAll ${rv32iEntries.length} RV32I conformance tests passed! Total pipelined cycles: $totalCycles")
    }

    it should "pass all 4 official Zmmul architectural conformance tests (enableZmmul = true)" in {
      println(s"\nExecuting ${zmmulEntries.length} official Zmmul architectural conformance tests on RiscvPipelined (with multiplier):")
      var totalCycles = 0
      for ((name, tohost, beginSig, endSig) <- zmmulEntries) {
        val c = runTest(name, tohost, beginSig, endSig, enableZmmul = true)
        totalCycles += c
      }
      println(f"\nAll ${zmmulEntries.length} Zmmul conformance tests passed! Total pipelined cycles: $totalCycles")
    }

    it should "pass control transfer conformance tests with BTFN branch prediction" in {
      println(s"\nExecuting branch and jump conformance tests with BTFN predictor:")
      val branchTests = rv32iEntries.filter(e => e._1.startsWith("b") || e._1.startsWith("jal"))
      for ((name, tohost, beginSig, endSig) <- branchTests) {
        runTest(name, tohost, beginSig, endSig, enableZmmul = false, branchPredictor = "btfn")
      }
    }

    it should "pass control transfer conformance tests with Gshare branch prediction" in {
      println(s"\nExecuting branch and jump conformance tests with Gshare predictor:")
      val branchTests = rv32iEntries.filter(e => e._1.startsWith("b") || e._1.startsWith("jal"))
      for ((name, tohost, beginSig, endSig) <- branchTests) {
        runTest(name, tohost, beginSig, endSig, enableZmmul = false, branchPredictor = "gshare")
      }
    }

    it should "pass all 38 official RV32I architectural conformance tests without forwarding (enableForwarding = false)" in {
      println(s"\nExecuting ${rv32iEntries.length} official RV32I architectural conformance tests on RiscvPipelined without forwarding (interlock stall-based hazard detection):")
      var totalCycles = 0
      for ((name, tohost, beginSig, endSig) <- rv32iEntries) {
        val c = runTest(name, tohost, beginSig, endSig, enableZmmul = false, enableForwarding = false)
        totalCycles += c
      }
      println(f"\nAll ${rv32iEntries.length} RV32I conformance tests passed on No-Forwarding core! Total pipelined cycles: $totalCycles")
    }
  }
}

