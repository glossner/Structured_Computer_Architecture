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

  val baseAddr = 0x80000000L
  val tohostAddr = 0x80001000L
  val maxCycles = 500000

  def runBenchmark(binPath: java.nio.file.Path, enableZmmul: Boolean, benchName: String,
                   tClkSingle: Double, tClkPipe: Double,
                   tohostAddr: Long = 0x80001000L, maxCycles: Int = 500000): Unit = {
    assert(Files.exists(binPath), s"Benchmark binary $binPath not found")
    val binBytes = Files.readAllBytes(binPath)

    // 1. Run Single-Cycle Core (RiscvFetchExecute)
    val memSingle = new SimMemory(baseAddr)
    memSingle.loadBinary(binBytes)
    var cyclesSingle = 0
    var resultSingle = 0L

    simulate(new RiscvFetchExecute(xlen = 32, initPC = baseAddr, enableZmmul = enableZmmul)) { dut =>
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
      assert(completed, s"Single-cycle simulation for $benchName timed out after $cyclesSingle cycles (last PC = 0x${dut.io.imem.addr.peek().litValue.toLong.toHexString})")
    }

    // Helper to run pipelined core with or without forwarding
    def runPipe(enableFwd: Boolean): (Int, Long) = {
      val memPipe = new SimMemory(baseAddr)
      memPipe.loadBinary(binBytes)
      var cycles = 0
      var result = 0L

      simulate(new RiscvPipelined(xlen = 32, initPC = baseAddr, enableZmmul = enableZmmul, enableForwarding = enableFwd)) { dut =>
        dut.reset.poke(true.B)
        dut.clock.step(5)
        dut.reset.poke(false.B)

        var completed = false
        while (cycles < maxCycles && !completed) {
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
              result = writeData
              completed = true
            }
          }

          dut.clock.step(1)
          cycles += 1
        }
        assert(completed, s"Pipelined (fwd=$enableFwd) simulation for $benchName timed out after $cycles cycles")
      }
      (cycles, result)
    }

    // 2. Run 4-Stage Pipelined Core without Forwarding
    val (cyclesPipeNoFwd, resultPipeNoFwd) = runPipe(false)

    // 3. Run 4-Stage Pipelined Core with Forwarding (Bypassing)
    val (cyclesPipeFwd, resultPipeFwd) = runPipe(true)

    // Verification
    assert(resultSingle == resultPipeNoFwd, s"Result mismatch in $benchName (NoFwd)! Single=$resultSingle, Pipe=$resultPipeNoFwd")
    assert(resultSingle == resultPipeFwd, s"Result mismatch in $benchName (Fwd)! Single=$resultSingle, Pipe=$resultPipeFwd")

    // Performance Metrics
    val instCount     = cyclesSingle
    val cpiSingle     = 1.0
    val cpiPipeNoFwd  = cyclesPipeNoFwd.toDouble / instCount.toDouble
    val cpiPipeFwd    = cyclesPipeFwd.toDouble / instCount.toDouble

    val execTimeSingle    = cyclesSingle * tClkSingle
    val execTimePipeNoFwd = cyclesPipeNoFwd * tClkPipe
    val execTimePipeFwd   = cyclesPipeFwd * tClkPipe
    val speedupNoFwd      = execTimeSingle / execTimePipeNoFwd
    val speedupFwd        = execTimeSingle / execTimePipeFwd
    val speedupFwdVsNoFwd = execTimePipeNoFwd / execTimePipeFwd

    println("=======================================================================")
    println(f"        BENCHMARK RESULTS: $benchName%-40s")
    println("=======================================================================")
    println(f"  Instructions Executed:          $instCount%8d")
    println(f"  Benchmark Result (tohost):       0x$resultSingle%08x ($resultSingle%d)")
    println("-----------------------------------------------------------------------")
    println(f"  Single-Cycle Cycles:            $cyclesSingle%8d   (CPI = $cpiSingle%.2f)")
    println(f"  4-Stage Pipe (No Forwarding):   $cyclesPipeNoFwd%8d   (CPI = $cpiPipeNoFwd%.2f)")
    println(f"  4-Stage Pipe (With Bypassing):  $cyclesPipeFwd%8d   (CPI = $cpiPipeFwd%.2f)")
    println("-----------------------------------------------------------------------")
    println(f"  Stalls (No Forwarding):         ${cyclesPipeNoFwd - cyclesSingle}%8d cycles (${(cpiPipeNoFwd - 1.0)*100}%.1f%% overhead)")
    println(f"  Stalls (With Bypassing):        ${cyclesPipeFwd - cyclesSingle}%8d cycles (${(cpiPipeFwd - 1.0)*100}%.1f%% overhead)")
    println(f"  Stall Elimination:              ${cyclesPipeNoFwd - cyclesPipeFwd}%8d stalls saved (${(cyclesPipeNoFwd - cyclesPipeFwd).toDouble / (cyclesPipeNoFwd - cyclesSingle).toDouble * 100}%.1f%% of all stalls eliminated)")
    println("-----------------------------------------------------------------------")
    println(f"  ASAP7 7nm Clock Period:         Single (FE)=$tClkSingle%.3f ns (${1000.0/tClkSingle}%.1f MHz), Pipe=$tClkPipe%.3f ns (${1000.0/tClkPipe}%.1f MHz)")
    println(f"  Execution Time:                 Single=$execTimeSingle%.2f ns, NoFwd=$execTimePipeNoFwd%.2f ns, Fwd=$execTimePipeFwd%.2f ns")
    println(f"  Speedup vs Single-Cycle:        No Forwarding = $speedupNoFwd%.2fx, With Bypassing = $speedupFwd%.2fx")
    println(f"  Speedup from Bypassing:         $speedupFwdVsNoFwd%.2fx over unforwarded pipeline")
    println("=======================================================================\n")
  }

  "RiscvBenchmark" should "execute pure RV32I benchmark on Single-Cycle and 4-Stage cores" in {
    val rv32iBin = Paths.get("src/test/resources/benchmark/rv32i_bench.bin")
    // ASAP7 7nm 4-step pipeline delays:
    // T_fetch = 0.550 ns, T_decode = 0.540 ns, T_execute = 1.363 ns, T_wb = 0.600 ns
    // Single-cycle FE: T_clk = 0.550 + 0.540 + 1.363 + 0.600 = 3.053 ns (327.9 MHz)
    // 4-stage pipeline: T_clk = max(0.550, 0.540, 1.363, 0.600) = 1.363 ns (733.7 MHz)
    runBenchmark(rv32iBin, enableZmmul = false, "Pure RV32I Benchmark (DSP Kernel with Soft Multiply)", 3.053, 1.363)
  }

  it should "execute RV32I_Zmmul benchmark on Single-Cycle and 4-Stage cores" in {
    val zmmulBin = Paths.get("src/test/resources/benchmark/dsp_bench.bin")
    // ASAP7 7nm 4-step pipeline delays with Zmmul hardware multiplier:
    // T_fetch = 0.550 ns, T_decode = 0.540 ns, T_execute = 2.462 ns, T_wb = 0.600 ns
    // Single-cycle FE: T_clk = 0.550 + 0.540 + 2.462 + 0.600 = 4.152 ns (240.8 MHz)
    // 4-stage pipeline: T_clk = max(0.550, 0.540, 2.462, 0.600) = 2.462 ns (406.2 MHz)
    runBenchmark(zmmulBin, enableZmmul = true, "RV32I_Zmmul Benchmark (DSP Kernel with Hardware Multiply)", 4.152, 2.462)
  }

  it should "execute Dhrystone 2.1 benchmark on Single-Cycle and 4-Stage cores" in {
    val dhryBin = Paths.get("src/test/resources/benchmark/dhrystone.bin")
    // Single-cycle FE: 3.053 ns (327.9 MHz), 4-stage pipeline: 1.363 ns (733.7 MHz)
    runBenchmark(dhryBin, enableZmmul = false, "Dhrystone 2.1 Benchmark (20 runs)", 3.053, 1.363, tohostAddr = 0x80040000L, maxCycles = 500000)
  }

  it should "execute EEMBC CoreMark benchmark on Single-Cycle and 4-Stage cores" in {
    val coremarkBin = Paths.get("src/test/resources/benchmark/coremark.bin")
    // Single-cycle FE: 3.053 ns (327.9 MHz), 4-stage pipeline: 1.363 ns (733.7 MHz)
    runBenchmark(coremarkBin, enableZmmul = false, "EEMBC CoreMark 1.0 Benchmark (1 iteration)", 3.053, 1.363, tohostAddr = 0x80040000L, maxCycles = 2000000)
  }
}
