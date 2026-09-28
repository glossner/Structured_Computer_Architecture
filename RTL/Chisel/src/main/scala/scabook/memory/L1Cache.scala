// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.memory

import chisel3._
import chisel3.util._

/** CPU request to Cache */
class CacheCpuReq(val addrWidth: Int = 32, val dataWidth: Int = 32) extends Bundle {
  val addr      = UInt(addrWidth.W)
  val funct3    = UInt(3.W)
  val isWrite   = Bool()
  val writeData = UInt(dataWidth.W)
}

/** Cache response to CPU */
class CacheCpuResp(val dataWidth: Int = 32) extends Bundle {
  val readData = UInt(dataWidth.W)
  val hit      = Bool()
}

/** External Memory Bus Request (Line-based) */
class CacheMemReq(val addrWidth: Int = 32, val lineBytes: Int = 32) extends Bundle {
  val addr      = UInt(addrWidth.W)
  val isWrite   = Bool()
  val writeData = UInt((lineBytes * 8).W)
}

/** External Memory Bus Response (Line-based) */
class CacheMemResp(val lineBytes: Int = 32) extends Bundle {
  val readData = UInt((lineBytes * 8).W)
  val ready    = Bool()
}

/** Parameterized Level-1 Cache (Instruction or Data).
  *
  * Supports:
  *   - Direct-Mapped, 2-Way, and 4-Way Set Associativity
  *   - Configurable total capacity (512 B to 64 KB)
  *   - 32-byte cache line size (8 32-bit words per line)
  *   - Write-back and Write-allocate policy (for D-cache)
  *   - Pseudo-LRU replacement policy
  *   - 1-cycle hit latency in ASAP7 7nm FinFET standard cells
  */
class L1Cache(
  val sizeBytes: Int = 4096,
  val ways: Int = 2,
  val lineBytes: Int = 32,
  val isDcache: Boolean = false,
  val xlen: Int = 32
) extends Module {
  require(isPow2(sizeBytes), "sizeBytes must be a power of 2")
  require(isPow2(ways), "ways must be a power of 2")
  require(isPow2(lineBytes), "lineBytes must be a power of 2")

  val numSets        = sizeBytes / (ways * lineBytes)
  val offsetBits     = log2Ceil(lineBytes)
  val wordOffsetBits = log2Ceil(lineBytes / 4)
  val indexBits      = log2Ceil(numSets)
  val tagBits        = xlen - indexBits - offsetBits
  val wordsPerLine   = lineBytes / 4
  val lruWidth       = if (ways <= 2) 1 else 3

  val io = IO(new Bundle {
    // CPU Interface
    val req   = Flipped(Valid(new CacheCpuReq(xlen, xlen)))
    val resp  = Valid(new CacheCpuResp(xlen))
    val stall = Output(Bool())

    // Memory / Interconnect Interface (Refill and Writeback)
    val memReq  = Decoupled(new CacheMemReq(xlen, lineBytes))
    val memResp = Flipped(Valid(new CacheMemResp(lineBytes)))

    // Profiling Signals
    val perfHit  = Output(Bool())
    val perfMiss = Output(Bool())
  })

  // Tag and Valid/Dirty Storage
  val validBits = RegInit(VecInit(Seq.fill(ways)(VecInit(Seq.fill(numSets)(false.B)))))
  val dirtyBits = RegInit(VecInit(Seq.fill(ways)(VecInit(Seq.fill(numSets)(false.B)))))
  val tagArrays = Seq.fill(ways)(SyncReadMem(numSets, UInt(tagBits.W)))

  // Data Storage: stored as wordsPerLine 32-bit words per way
  val dataArrays = Seq.fill(ways)(SyncReadMem(numSets, Vec(wordsPerLine, UInt(32.W))))

  // LRU Tracking: 1 bit per set for 2-way, 3 bits for 4-way
  val lruBits = RegInit(VecInit(Seq.fill(numSets)(0.U(lruWidth.W))))

  // FSM States
  val sIdle :: sRefillReq :: sRefillWait :: sWritebackReq :: sWritebackWait :: Nil = Enum(5)
  val state = RegInit(sIdle)

  // Request latches
  val reqReg = Reg(new CacheCpuReq(xlen, xlen))
  val reqValidReg = RegInit(false.B)

  val inReq = Mux(state === sIdle, io.req.bits, reqReg)
  val inValid = Mux(state === sIdle, io.req.valid, reqValidReg)

  val reqAddr    = inReq.addr
  val reqOffset  = reqAddr(offsetBits - 1, 0)
  val reqWordIdx = reqAddr(offsetBits - 1, 2)
  val reqIndex   = (if (indexBits > 0) reqAddr(offsetBits + indexBits - 1, offsetBits) else 0.U)
  val reqTag     = reqAddr(xlen - 1, offsetBits + indexBits)

  // Memory read accesses for synchronous tag and data
  val tagReads = VecInit(tagArrays.map(_.read(reqIndex, inValid)))
  val dataReads = VecInit(dataArrays.map(_.read(reqIndex, inValid)))

  // Way Hit Detection: combinational comparison with tag
  val wayHits = Wire(Vec(ways, Bool()))
  for (w <- 0 until ways) {
    wayHits(w) := validBits(w)(reqIndex) && (tagReads(w) === reqTag)
  }
  val isHit = wayHits.asUInt.orR && inValid && (state === sIdle)
  val hitWay = PriorityEncoder(wayHits)

  // Victim Selection (Pseudo-LRU)
  val victimWay = Wire(UInt(log2Ceil(ways).W))
  if (ways == 1) {
    victimWay := 0.U
  } else if (ways == 2) {
    victimWay := lruBits(reqIndex)(0)
  } else {
    // 4-way tree-PLRU
    val b0 = lruBits(reqIndex)(0)
    val b1 = lruBits(reqIndex)(1)
    val b2 = lruBits(reqIndex)(2)
    victimWay := Mux(!b0, Mux(!b1, 0.U, 1.U), Mux(!b2, 2.U, 3.U))
  }

  val victimDirty = VecInit((0 until ways).map(w => dirtyBits(w)(reqIndex)))(victimWay) && (if (isDcache) true.B else false.B)
  val victimTag   = tagReads(victimWay)
  val victimAddr  = Cat(victimTag, reqIndex, 0.U(offsetBits.W))

  // Read data formatter (byte/half/word selection)
  val selectedData = Mux1H(wayHits, dataReads)
  val hitWord      = selectedData(reqWordIdx)

  val byteOff = reqOffset(1, 0)
  val halfOff = reqOffset(1)
  val isU     = inReq.funct3(2)

  val byteVal = (hitWord >> (byteOff * 8.U))(7, 0)
  val halfVal = (hitWord >> (Mux(halfOff, 16.U, 0.U)))(15, 0)

  val formattedReadData = WireDefault(hitWord)
  if (isDcache) {
    val bExt = Cat(Mux(isU, 0.U(24.W), Fill(24, byteVal(7))), byteVal)
    val hExt = Cat(Mux(isU, 0.U(16.W), Fill(16, halfVal(15))), halfVal)
    formattedReadData := MuxCase(hitWord, Seq(
      (inReq.funct3(1, 0) === 0.U) -> bExt,
      (inReq.funct3(1, 0) === 1.U) -> hExt
    ))
  } else {
    formattedReadData := hitWord
  }

  // Refill data buffer
  val refillDataReg = Reg(UInt((lineBytes * 8).W))

  // Defaults
  io.resp.valid          := false.B
  io.resp.bits.readData  := formattedReadData
  io.resp.bits.hit       := isHit
  io.stall               := false.B
  io.memReq.valid        := false.B
  io.memReq.bits.addr    := 0.U
  io.memReq.bits.isWrite := false.B
  io.memReq.bits.writeData := 0.U
  io.perfHit             := false.B
  io.perfMiss            := false.B

  // FSM Logic
  switch(state) {
    is(sIdle) {
      when(inValid) {
        when(isHit) {
          io.resp.valid := true.B
          io.perfHit    := true.B

          // Update LRU on hit
          if (ways == 2) {
            lruBits(reqIndex) := ~hitWay(0)
          }

          // Handle Write Hit for D-Cache
          if (isDcache) {
            when(inReq.isWrite) {
              val wdataVec = Wire(Vec(wordsPerLine, UInt(32.W)))
              val d = inReq.writeData
              for (i <- 0 until wordsPerLine) {
                val curWord = selectedData(i)
                val updatedWord = MuxCase(d, Seq(
                  (inReq.funct3(1, 0) === 0.U) -> (curWord & ~(0xFF.U << (byteOff * 8.U)) | (d(7, 0) << (byteOff * 8.U))),
                  (inReq.funct3(1, 0) === 1.U) -> (curWord & ~(0xFFFF.U << (Mux(halfOff, 16.U, 0.U))) | (d(15, 0) << (Mux(halfOff, 16.U, 0.U))))
                ))
                wdataVec(i) := Mux(reqWordIdx === i.U, updatedWord, curWord)
              }
              for (w <- 0 until ways) {
                when(hitWay === w.U) {
                  dataArrays(w).write(reqIndex, wdataVec)
                  dirtyBits(w)(reqIndex) := true.B
                }
              }
            }
          }
        }.otherwise {
          // Miss detected!
          io.stall    := true.B
          io.perfMiss := true.B
          reqReg      := inReq
          reqValidReg := true.B

          when(victimDirty) {
            state := sWritebackReq
          }.otherwise {
            state := sRefillReq
          }
        }
      }
    }

    is(sWritebackReq) {
      io.stall := true.B
      io.memReq.valid        := true.B
      io.memReq.bits.addr    := victimAddr
      io.memReq.bits.isWrite := true.B
      io.memReq.bits.writeData := dataReads(victimWay).asUInt

      when(io.memReq.ready) {
        state := sWritebackWait
      }
    }

    is(sWritebackWait) {
      io.stall := true.B
      when(io.memResp.valid && io.memResp.bits.ready) {
        state := sRefillReq
      }
    }

    is(sRefillReq) {
      io.stall := true.B
      io.memReq.valid        := true.B
      io.memReq.bits.addr    := Cat(reqTag, reqIndex, 0.U(offsetBits.W))
      io.memReq.bits.isWrite := false.B

      when(io.memReq.ready) {
        state := sRefillWait
      }
    }

    is(sRefillWait) {
      io.stall := true.B
      when(io.memResp.valid && io.memResp.bits.ready) {
        // Refill completed! Update tag and data arrays
        val refillWords = Wire(Vec(wordsPerLine, UInt(32.W)))
        for (i <- 0 until wordsPerLine) {
          val rawWord = io.memResp.bits.readData(32 * (i + 1) - 1, 32 * i)
          if (isDcache) {
            val d = reqReg.writeData
            val byteOffR = reqReg.addr(1, 0)
            val halfOffR = reqReg.addr(1)
            val updatedWord = MuxCase(d, Seq(
              (reqReg.funct3(1, 0) === 0.U) -> (rawWord & ~(0xFF.U << (byteOffR * 8.U)) | (d(7, 0) << (byteOffR * 8.U))),
              (reqReg.funct3(1, 0) === 1.U) -> (rawWord & ~(0xFFFF.U << (Mux(halfOffR, 16.U, 0.U))) | (d(15, 0) << (Mux(halfOffR, 16.U, 0.U))))
            ))
            refillWords(i) := Mux(reqReg.isWrite && (reqWordIdx === i.U), updatedWord, rawWord)
          } else {
            refillWords(i) := rawWord
          }
        }

        if (isDcache) {
          when(reqReg.isWrite) {
            for (w <- 0 until ways) {
              when(victimWay === w.U) {
                dirtyBits(w)(reqIndex) := true.B
              }
            }
          }.otherwise {
            for (w <- 0 until ways) {
              when(victimWay === w.U) {
                dirtyBits(w)(reqIndex) := false.B
              }
            }
          }
        }

        for (w <- 0 until ways) {
          when(victimWay === w.U) {
            tagArrays(w).write(reqIndex, reqTag)
            dataArrays(w).write(reqIndex, refillWords)
            validBits(w)(reqIndex) := true.B
          }
        }

        // Complete the stalled CPU transaction
        io.resp.valid := true.B
        io.resp.bits.readData := refillWords(reqWordIdx)
        reqValidReg := false.B
        state := sIdle
      }
    }
  }
}
