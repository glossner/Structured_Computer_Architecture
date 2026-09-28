// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.riscv

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ArrayBuffer

class RiscvBarrel4TConformanceSpec extends AnyFlatSpec {

  class SimMemory(val baseAddr: Long, val sizeBytes: Int = 4 * 1024 * 1024) {
    val bytes = new Array[Byte](sizeBytes)

    def loadBinary(data: Array[Byte]): Unit = {
      System.arraycopy(data, 0, bytes, 0, math.min(data.length, sizeBytes))
    }

    def clear(): Unit = {
      java.util.Arrays.fill(bytes, 0.toByte)
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

  case class TestCase(
    name: String,
    tohost: Long,
    beginSig: Long,
    endSig: Long,
    binBytes: Array[Byte],
    refLines: Seq[String]
  )

  val metaPath = Paths.get("src/test/resources/conformance/test_metadata.csv")

  if (Files.exists(metaPath)) {
    val metaLines = Files.readAllLines(metaPath).toArray.map(_.toString).toSeq
    val allTests: Seq[TestCase] = metaLines.drop(1).filter(_.trim.nonEmpty).map { line =>
      val parts = line.split(",").map(_.trim)
      val name = parts(0)
      val tohost = java.lang.Long.decode(parts(1)).longValue()
      val beginSig = java.lang.Long.decode(parts(2)).longValue()
      val endSig = java.lang.Long.decode(parts(3)).longValue()

      val binPath = Paths.get(s"src/test/resources/conformance/${name}.bin")
      val refPath = Paths.get(s"src/test/resources/conformance/${name}.reference.sig")
      assert(Files.exists(binPath), s"Binary $binPath not found")
      assert(Files.exists(refPath), s"Reference signature $refPath not found")

      val binBytes = Files.readAllBytes(binPath)
      val refLines = Files.readAllLines(refPath).toArray.map(_.toString.trim).filter(_.nonEmpty).toSeq
      TestCase(name, tohost, beginSig, endSig, binBytes, refLines)
    }


    it should "pass all 42 official architectural conformance tests simultaneously in random order across 4 threads" in {
      val numThreads = 4
      val baseAddr = 0x80000000L
      val maxCycles = 600000

      // Pseudo-randomly shuffle all 42 tests to interleave them concurrently across threads
      val rng = new scala.util.Random(1984)
      val shuffledTests = rng.shuffle(allTests)

      println("=" * 80)
      println(f"Running ${shuffledTests.length}%d Conformance Tests Concurrently on 4-Thread Barrel Core (RiscvBarrel4T)")
      println("All 4 hardware threads execute tests simultaneously in randomized sequence.")
      println("=" * 80)

      val mems = Array.tabulate(numThreads)(_ => new SimMemory(baseAddr, 4 * 1024 * 1024))
      val testQueue = collection.mutable.Queue[TestCase]()
      testQueue.enqueueAll(shuffledTests)

      val currentTest = new Array[Option[TestCase]](numThreads)
      val testsCompleted = new Array[Int](numThreads)
      val cyclesSpent = new Array[Int](numThreads)
      val pendingReset = new Array[Boolean](numThreads)
      val threadActive = new Array[Boolean](numThreads)

      // Initialize the first 4 tests onto the 4 hardware threads
      for (t <- 0 until numThreads) {
        val test = testQueue.dequeue()
        mems(t).clear()
        mems(t).loadBinary(test.binBytes)
        currentTest(t) = Some(test)
        testsCompleted(t) = 0
        cyclesSpent(t) = 0
        pendingReset(t) = false
        threadActive(t) = true
        println(f"  [Thread $t%d Initial Dispatch] -> ${test.name}%-15s (binary: ${test.binBytes.length}%6d bytes)")
      }

      val passedTests = collection.mutable.HashSet[String]()
      var totalGlobalCycles = 0

      simulate(new RiscvBarrel4T(
        xlen = 32,
        initPC = baseAddr,
        numThreads = numThreads,
        enableZmmul = true,
        enableForwarding = true,
        enablePipelinedMul = true
      )) { dut =>
        // Bypass caches for unbuffered architectural memory verification
        dut.io.bypassCaches.poke(true.B)
        dut.io.imemResp.valid.poke(false.B)
        dut.io.imemResp.bits.ready.poke(false.B)
        dut.io.imemResp.bits.readData.poke(0.U)
        dut.io.imemRefill.ready.poke(false.B)
        dut.io.dmemResp.valid.poke(false.B)
        dut.io.dmemResp.bits.ready.poke(false.B)
        dut.io.dmemResp.bits.readData.poke(0.U)
        dut.io.dmemRefill.ready.poke(false.B)

        dut.io.threadReset.poke(0.U)
        for (i <- 0 until numThreads) {
          dut.io.threadResetPC(i).poke(baseAddr.U)
        }

        // Global Reset
        dut.reset.poke(true.B)
        dut.clock.step(5)
        dut.reset.poke(false.B)

        while (passedTests.size < shuffledTests.length && totalGlobalCycles < maxCycles) {
          // 1. Apply any pending per-thread dynamic resets
          var resetMask = 0
          for (t <- 0 until numThreads) {
            if (pendingReset(t)) {
              resetMask |= (1 << t)
            }
          }
          dut.io.threadReset.poke(resetMask.U)
          for (t <- 0 until numThreads) {
            dut.io.threadResetPC(t).poke(baseAddr.U)
          }

          // Clear pending reset flags for threads being pulsed this cycle
          for (t <- 0 until numThreads) {
            if (pendingReset(t)) {
              pendingReset(t) = false
            }
          }

          // 2. Service Instruction Fetch for the currently fetching thread
          val curT = dut.io.currentThread.peek().litValue.toInt
          val fetchPC = dut.io.rawImem.addr.peek().litValue.toLong
          val fetchedInst = mems(curT).read32(fetchPC)
          dut.io.rawImem.inst.poke(fetchedInst.U(32.W))

          // 3. Service Data Memory for the currently retiring / committing thread
          val retT = dut.io.retiredThread.peek().litValue.toInt
          val dmemAddr = dut.io.rawDmem.addr.peek().litValue.toLong
          val dmemFunct3 = dut.io.rawDmem.funct3.peek().litValue.toInt
          val memRead = dut.io.rawDmem.memRead.peek().litToBoolean
          val memWrite = dut.io.rawDmem.memWrite.peek().litToBoolean
          val writeData = dut.io.rawDmem.writeData.peek().litValue.toLong

          if (memRead) {
            val rdata = mems(retT).readFormatted(dmemAddr, dmemFunct3)
            dut.io.rawDmem.readData.poke(rdata.U(32.W))
          } else {
            dut.io.rawDmem.readData.poke(0.U(32.W))
          }

          if (memWrite) {
            mems(retT).writeFormatted(dmemAddr, writeData, dmemFunct3)

            // Check for tohost write completion
            currentTest(retT).foreach { test =>
              if (dmemAddr == test.tohost && writeData != 0) {
                assert(writeData == 1L, s"[FAILURE] Thread $retT failed test ${test.name} with tohost = 0x${writeData.toHexString}")
                val dutSig = mems(retT).dumpSignature(test.beginSig, test.endSig)
                assert(
                  dutSig == test.refLines,
                  s"[SIGNATURE MISMATCH] Thread $retT in test ${test.name}! DUT words: ${dutSig.length}, REF words: ${test.refLines.length}"
                )

                passedTests += test.name
                testsCompleted(retT) += 1
                println(f"  [PASS] Thread $retT completed ${test.name}%-15s (${dutSig.length}%3d sig words) in ${cyclesSpent(retT)}%5d thread-cycles (${passedTests.size}%2d/${shuffledTests.length}%2d total)")

                // Reset cycle counter for this thread
                cyclesSpent(retT) = 0

                // If more tests are in the queue, immediately schedule the next test on this thread
                if (testQueue.nonEmpty) {
                  val nextTest = testQueue.dequeue()
                  mems(retT).clear()
                  mems(retT).loadBinary(nextTest.binBytes)
                  currentTest(retT) = Some(nextTest)
                  pendingReset(retT) = true
                } else {
                  // No more tests left for this thread: install an idle spin loop (j .)
                  mems(retT).clear()
                  mems(retT).writeFormatted(baseAddr, 0x0000006fL, 2) // jal x0, 0 (j .)
                  currentTest(retT) = None
                  pendingReset(retT) = true
                  threadActive(retT) = false
                }
              }
            }
          }

          // Track thread activity
          if (threadActive(retT) && dut.io.instRetired.peek().litToBoolean) {
            cyclesSpent(retT) += 1
          }

          dut.clock.step(1)
          totalGlobalCycles += 1
        }

        println("=" * 80)
        println(f"All ${passedTests.size}%d Conformance Tests Passed Successfully in $totalGlobalCycles%d Global Core Cycles!")
        println("Multi-Threaded Test Distribution Across Hardware Threads:")
        for (t <- 0 until numThreads) {
          println(f"  Thread $t%d executed ${testsCompleted(t)}%2d tests.")
        }
        println("=" * 80)

        assert(passedTests.size == shuffledTests.length, s"Only ${passedTests.size} of ${shuffledTests.length} tests completed before maxCycles timeout!")
      }
    }
  }
}
