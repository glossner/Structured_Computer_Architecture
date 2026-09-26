// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.memory

import chisel3._, chisel3.util._

/** RISC-V Byte-Addressable Synchronous Data Memory. */
class RiscvDataMemory(val depthWords: Int = 1024) extends Module {
  val io = IO(new Bundle {
    val addr      = Input(UInt(32.W));  val funct3   = Input(UInt(3.W))
    val memWrite  = Input(Bool());      val memRead  = Input(Bool())
    val writeData = Input(UInt(32.W));  val readData = Output(UInt(32.W))
  })

  val mem      = SyncReadMem(depthWords, Vec(4, UInt(8.W)))
  val wordAddr = io.addr(log2Ceil(depthWords) + 1, 2)
  val byteOff  = io.addr(1, 0); val h = byteOff(1)
  val wmask    = WireDefault(VecInit(Seq.fill(4)(false.B)))
  val wdata    = WireDefault(VecInit(Seq.fill(4)(0.U(8.W))))
  val d        = io.writeData; val isU = io.funct3(2)

  when(io.funct3 === "b000".U) { // SB
    wmask(byteOff) := true.B; wdata(byteOff) := d(7, 0)
  }.elsewhen(io.funct3 === "b001".U) { // SH
    wmask(Cat(h, 0.U)) := true.B; wdata(Cat(h, 0.U)) := d(7, 0)
    wmask(Cat(h, 1.U)) := true.B; wdata(Cat(h, 1.U)) := d(15, 8)
  }.elsewhen(io.funct3 === "b010".U) { // SW
    for (i <- 0 until 4) { wmask(i) := true.B; wdata(i) := d(8*i+7, 8*i) }
  }
  when(io.memWrite) { mem.write(wordAddr, wdata, wmask) }
  val r    = mem.read(wordAddr, io.memRead)
  val b    = r(byteOff); val half = Mux(h, Cat(r(3), r(2)), Cat(r(1), r(0)))
  val bExt = Cat(Mux(isU, 0.U(24.W), Fill(24, b(7))), b)
  val hExt = Cat(Mux(isU, 0.U(16.W), Fill(16, half(15))), half)

  io.readData := MuxCase(Cat(r(3), r(2), r(1), r(0)), Seq(
    (io.funct3(1, 0) === 0.U) -> bExt,
    (io.funct3(1, 0) === 1.U) -> hExt
  ))
}
