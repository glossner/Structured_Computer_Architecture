// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._
import chisel3.util._

/** Interface for Branch Predictor modules.
  *
  * @param xlen         Data/address path width (32 bits for RV32)
  * @param historyWidth Number of global history register (GHR) bits for Gshare
  */
class BranchPredictorIO(val xlen: Int = 32, val historyWidth: Int = 8) extends Bundle {
  // Query port (evaluated combinationally in IF stage)
  val pc          = Input(UInt(xlen.W))
  val predTaken   = Output(Bool())
  val predTarget  = Output(UInt(xlen.W))
  val predHistory = Output(UInt(historyWidth.W))

  // Update port (synchronous state update from EX stage)
  val updateValid  = Input(Bool())
  val updateIsBr   = Input(Bool())
  val updateIsJal  = Input(Bool())
  val updatePC     = Input(UInt(xlen.W))
  val actualTaken  = Input(Bool())
  val actualTarget = Input(UInt(xlen.W))
  val prevHistory  = Input(UInt(historyWidth.W))
}

/** Parameterized Branch Predictor supporting:
  *   - "none":   Static predict Not-Taken (baseline, 0 bubbles on not-taken, 2 bubbles on taken)
  *   - "btfn":   Static Backward Taken, Forward Not-Taken using a Branch Target Buffer (BTB) in IF
  *   - "gshare": Dynamic two-level adaptive predictor with Global History Register (GHR) XOR PC
  *               indexing a Pattern History Table (PHT) of 2-bit saturating counters with BTB
  *
  * @param xlen          Architecture bit width (32 bits)
  * @param predictorType Predictor architecture: "none", "btfn", or "gshare"
  * @param btbEntries    Number of entries in the Branch Target Buffer (default 32)
  * @param historyWidth  Number of bits in the Global History Register (default 8 -> 256 PHT entries)
  */
class BranchPredictor(
  val xlen: Int = 32,
  val predictorType: String = "none",
  val btbEntries: Int = 32,
  val historyWidth: Int = 8
) extends Module {
  val io = IO(new BranchPredictorIO(xlen, historyWidth))

  val normalizedType = predictorType.trim.toLowerCase

  if (normalizedType == "none") {
    // -------------------------------------------------------------
    // BASELINE: Static Predict-Not-Taken (No tables, zero hardware)
    // -------------------------------------------------------------
    io.predTaken   := false.B
    io.predTarget  := io.pc + 4.U
    io.predHistory := 0.U
  } else {
    // -------------------------------------------------------------
    // BRANCH TARGET BUFFER (BTB) in IF stage
    // -------------------------------------------------------------
    val indexBits = log2Ceil(btbEntries)
    val tagBits   = xlen - indexBits - 2

    val btbValid   = RegInit(VecInit(Seq.fill(btbEntries)(false.B)))
    val btbTags    = Reg(Vec(btbEntries, UInt(tagBits.W)))
    val btbTargets = Reg(Vec(btbEntries, UInt(xlen.W)))
    val btbIsJal   = RegInit(VecInit(Seq.fill(btbEntries)(false.B)))

    // Query in IF stage (bits 1:0 are always 0 in 32-bit instructions)
    val ifIndex = io.pc(indexBits + 1, 2)
    val ifTag   = io.pc(xlen - 1, indexBits + 2)
    val btbHit  = btbValid(ifIndex) && (btbTags(ifIndex) === ifTag)
    val btbTarget = btbTargets(ifIndex)

    // Update in EX stage
    val updateIndex = io.updatePC(indexBits + 1, 2)
    val updateTag   = io.updatePC(xlen - 1, indexBits + 2)

    when(io.updateValid && io.actualTaken) {
      btbValid(updateIndex)   := true.B
      btbTags(updateIndex)    := updateTag
      btbTargets(updateIndex) := io.actualTarget
      btbIsJal(updateIndex)   := io.updateIsJal
    }

    if (normalizedType == "btfn") {
      // -------------------------------------------------------------
      // BTFN: Backward Taken, Forward Not-Taken
      // -------------------------------------------------------------
      // In BTFN:
      // - Backward branches (target < pc) are predicted TAKEN
      // - Forward branches (target >= pc) are predicted NOT TAKEN
      // - Unconditional JAL is always predicted TAKEN once in BTB
      val isBackward = btbTarget < io.pc
      val isJal      = btbIsJal(ifIndex)
      val predTaken  = btbHit && (isBackward || isJal)

      io.predTaken   := predTaken
      io.predTarget  := Mux(predTaken, btbTarget, io.pc + 4.U)
      io.predHistory := 0.U

    } else {
      // -------------------------------------------------------------
      // GSHARE: Two-Level Adaptive Predictor (Scott McFarling, 1993)
      // -------------------------------------------------------------
      val numPhtEntries = 1 << historyWidth
      // Pattern History Table (PHT) of 2-bit saturating counters:
      // 00: Strongly Not-Taken, 01: Weakly Not-Taken, 10: Weakly Taken, 11: Strongly Taken
      val pht = RegInit(VecInit(Seq.fill(numPhtEntries)(2.U(2.W))))
      val ghr = RegInit(0.U(historyWidth.W))

      // Hashing: XOR branch PC with Global History Register (GHR)
      val pcHash = io.pc(historyWidth + 1, 2)
      val phtIndex = pcHash ^ ghr
      val counter = pht(phtIndex)
      val dirTaken = counter(1) // MSB: 1 -> Taken, 0 -> Not-Taken

      val isJal = btbIsJal(ifIndex)
      val predTaken = btbHit && (dirTaken || isJal)

      io.predTaken   := predTaken
      io.predTarget  := Mux(predTaken, btbTarget, io.pc + 4.U)
      io.predHistory := ghr

      // Update PHT and GHR from resolved EX stage
      val updateHash = io.updatePC(historyWidth + 1, 2)
      val updatePhtIndex = updateHash ^ io.prevHistory

      when(io.updateValid && io.updateIsBr) {
        val currCnt = pht(updatePhtIndex)
        when(io.actualTaken) {
          pht(updatePhtIndex) := Mux(currCnt === 3.U, 3.U, currCnt + 1.U)
        }.otherwise {
          pht(updatePhtIndex) := Mux(currCnt === 0.U, 0.U, currCnt - 1.U)
        }

        // Shift actual outcome into Global History Register
        ghr := Cat(ghr(historyWidth - 2, 0), io.actualTaken.asUInt)
      }
    }
  }
}
