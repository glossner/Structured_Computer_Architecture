// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.riscv

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import java.nio.file.{Files, Paths}

class RiscvBenchmarkSpec extends AnyFlatSpec {

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
  }

  val binPath = Paths.get("src/test/resources/benchmark/dsp_bench.bin")
  val baseAddr = 0x80000000L
  val tohostAddr = 0x80001000L
  val maxCycles = 100000

  "RiscvBenchmark" should "execute DSP benchmark on both Single-Cycle and 4-Stage cores and compare performance" in {
    assert(Files.exists(binPath), s"Benchmark binary $binPath not found")
    val binBytes = Files.readAllBytes(binPath)

    // 1. Run Single-Cycle Core (RiscvFetchExecute)
    val memSingle = new SimMemory(baseAddr)
    memSingle.loadBinary(binBytes)
    var cyclesSingle = 0
    var resultSingle = 0L

    simulate(new RiscvFetchExecute(xlen = 32, initPC = baseAddr)) { dut =>
      dut.reset.poke(true.B)
      dut.clock.step(5)
      dut.reset.poke(false.B)

      var completed = false
      while (cyclesSingle < maxCycles && !completed) {
        val pc = dut.io.imem.addr.peek().litValue.toLong
        val inst = memSingle.read32(pc)
        dut.io.imem.inst.poke(inst.U(32.W))

        val dmemAddr   = dut.io.dmem.addr.peek().litValue.toLong
        val dmemFunct3 = dut.io.dmem.funct3.peek().litValue.toInt
        val memRead    = dut.io.dmem.memRead.peek().litToBoolean
        val memWrite   = dut.io.dmem.memWrite.peek().litToBoolean
        val writeData  = dut.io.dmem.writeData.peek().litValue.toLong

        if (memRead) {
          dut.io.dmem.readData.poke(memSingle.readFormatted(dmemAddr, dmemFunct3).U(32.W))
        } else {
          dut.io.dmem.readData.poke(0.U(32.W))
        }

        if (memWrite) {
          memSingle.writeFormatted(dmemAddr, writeData, dmemFunct3)
          if (dmemAddr == tohostAddr && writeData != 0) {
            resultSingle = writeData
            completed = true
          }
        }

        dut.clock.step(1)
        cyclesSingle += 1
      }
      assert(completed, "Single-cycle simulation timed out")
    }

    // 2. Run 4-Stage Pipelined Core (RiscvPipelined)
    val memPipe = new SimMemory(baseAddr)
    memPipe.loadBinary(binBytes)
    var cyclesPipe = 0
    var resultPipe = 0L

    simulate(new RiscvPipelined(xlen = 32, initPC = baseAddr)) { dut =>
      dut.reset.poke(true.B)
      dut.clock.step(5)
      dut.reset.poke(false.B)

      var completed = false
      while (cyclesPipe < maxCycles && !completed) {
        val pc = dut.io.imem.addr.peek().litValue.toLong
        val inst = memPipe.read32(pc)
        dut.io.imem.inst.poke(inst.U(32.W))

        val dmemAddr   = dut.io.dmem.addr.peek().litValue.toLong
        val dmemFunct3 = dut.io.dmem.funct3.peek().litValue.toInt
        val memRead    = dut.io.dmem.memRead.peek().litToBoolean
        val memWrite   = dut.io.dmem.memWrite.peek().litToBoolean
        val writeData  = dut.io.dmem.writeData.peek().litValue.toLong

        if (memRead) {
          dut.io.dmem.readData.poke(memPipe.readFormatted(dmemAddr, dmemFunct3).U(32.W))
        } else {
          dut.io.dmem.readData.poke(0.U(32.W))
        }

        if (memWrite) {
          memPipe.writeFormatted(dmemAddr, writeData, dmemFunct3)
          if (dmemAddr == tohostAddr && writeData != 0) {
            resultPipe = writeData
            completed = true
          }
        }

        dut.clock.step(1)
        cyclesPipe += 1
      }
      assert(completed, "Pipelined simulation timed out")
    }

    // Verification
    assert(resultSingle == resultPipe, s"Result mismatch! Single=$resultSingle, Pipe=$resultPipe")

    // Performance Metrics
    val instCount = cyclesSingle // In single-cycle core, each retired instruction takes exactly 1 cycle
    val cpiSingle = 1.0
    val cpiPipe   = cyclesPipe.toDouble / instCount.toDouble

    // SkyWater 130nm synthesis results:
    // Single-cycle: T_clk = 13.64 ns (f_max = 73.31 MHz)
    // 4-stage pipeline: T_clk = 13.25 ns (f_max = 75.47 MHz)
    val tClkSingle = 13.64 // ns
    val tClkPipe   = 13.25 // ns

    val execTimeSingle = cyclesSingle * tClkSingle // ns
    val execTimePipe   = cyclesPipe * tClkPipe     // ns

    val speedupActual = execTimeSingle / execTimePipe
    val speedupCycleOnly = cyclesSingle.toDouble / cyclesPipe.toDouble

    println("=======================================================================")
    println("        REAL PROGRAM BENCHMARK RESULTS (FIR FILTER + MATRIX-VEC)       ")
    println("=======================================================================")
    println(f"  Instructions Executed:      $instCount%6d")
    println(f"  Benchmark Result (tohost):   0x$resultSingle%08x ($resultSingle%d)")
    println("-----------------------------------------------------------------------")
    println(f"  Single-Cycle Cycles:        $cyclesSingle%6d   (CPI = $cpiSingle%.2f)")
    println(f"  4-Stage Pipelined Cycles:   $cyclesPipe%6d   (CPI = $cpiPipe%.2f)")
    println(f"  Pipeline Stall Overhead:    ${cyclesPipe - cyclesSingle}%6d cycles (${(cpiPipe - 1.0)*100}%.1f%% penalty)")
    println("-----------------------------------------------------------------------")
    println(f"  SkyWater 130nm T_clk (Single):   $tClkSingle%.2f ns  (f_max = ${1000.0/tClkSingle}%.2f MHz)")
    println(f"  SkyWater 130nm T_clk (4-Stage):  $tClkPipe%.2f ns  (f_max = ${1000.0/tClkPipe}%.2f MHz)")
    println(f"  Execution Time (Single-Cycle):   $execTimeSingle%.2f ns")
    println(f"  Execution Time (4-Stage Pipe):   $execTimePipe%.2f ns")
    println("=======================================================================")
  }
}
