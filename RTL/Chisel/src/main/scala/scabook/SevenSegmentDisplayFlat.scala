package scabook

import chisel3._
import chisel3.util._

class SevenSegmentDisplayFlat extends Module {
  val io = IO(new Bundle {
    //In
    val binIn_B0 = Input(Bool())
    val binIn_B1 = Input(Bool())
    val binIn_B2 = Input(Bool())
    val binIn_B3 = Input(Bool())

    // Out
    val segOut_a = Output(Bool())
    val segOut_b = Output(Bool())
    val segOut_c = Output(Bool())
    val segOut_d = Output(Bool())
    val segOut_e = Output(Bool())
    val segOut_f = Output(Bool())
    val segOut_g = Output(Bool())
  })

  // Combine input signals into a UInt for internal logic
  val binIn = Wire(UInt(4.W))
  binIn := Cat(io.binIn_B3, io.binIn_B2, io.binIn_B1, io.binIn_B0)

  // Create a UInt for output signals and split it back into individual signals
  val segOut = Wire(UInt(7.W))
  io.segOut_a := segOut(6)
  io.segOut_b := segOut(5)
  io.segOut_c := segOut(4)
  io.segOut_d := segOut(3)
  io.segOut_e := segOut(2)
  io.segOut_f := segOut(1)
  io.segOut_g := segOut(0)

    // Output decoding
    // 0: a, b, c, d, e, f
    // 1: b, c
    // 2: a, b, g, e, d
    // 3: a, b, g, c, d
    // 4: f, g, b, c
    // 5: a, f, g, c, d
    // 6: a, f, g, e, d, c
    // 7: a, b, c
    // 8: All segments on
    // 9: a, f, b, g, c, d
    // Default: All segments off

  // Default: All segments off
  segOut := "b0000000".U

  // Seven-segment display logic
  when(     binIn === 0.U) { segOut := "b1111110".U }
  .elsewhen(binIn === 1.U) { segOut := "b0110000".U }
  .elsewhen(binIn === 2.U) { segOut := "b1101101".U }
  .elsewhen(binIn === 3.U) { segOut := "b1111001".U }
  .elsewhen(binIn === 4.U) { segOut := "b0110011".U }
  .elsewhen(binIn === 5.U) { segOut := "b1011011".U }
  .elsewhen(binIn === 6.U) { segOut := "b1011111".U }
  .elsewhen(binIn === 7.U) { segOut := "b1110000".U }
  .elsewhen(binIn === 8.U) { segOut := "b1111111".U }
  .elsewhen(binIn === 9.U) { segOut := "b1111011".U }
}
