// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._
import circt.stage.ChiselStage

class SevenSegmentDisplayMux extends Module {
  val io = IO(new Bundle {
    val binIn = Input(UInt(4.W))  // 4-bit binary input
    val segOut = Output(UInt(7.W)) // 7-bit output for seven-segment display
  })

  io.segOut := Mux(io.binIn === 0.U, "b1111110".U(7.W), // 0: a, b, c, d, e, f
               Mux(io.binIn === 1.U, "b0110000".U(7.W), // 1: b, c
               Mux(io.binIn === 2.U, "b1101101".U(7.W), // 2: a, b, g, e, d
               Mux(io.binIn === 3.U, "b1111001".U(7.W), // 3: a, b, g, c, d
               Mux(io.binIn === 4.U, "b0110011".U(7.W), // 4: f, g, b, c
               Mux(io.binIn === 5.U, "b1011011".U(7.W), // 5: a, f, g, c, d
               Mux(io.binIn === 6.U, "b1011111".U(7.W), // 6: a, f, g, e, d, c
               Mux(io.binIn === 7.U, "b1110000".U(7.W), // 7: a, b, c
               Mux(io.binIn === 8.U, "b1111111".U(7.W), // 8: All segments
               Mux(io.binIn === 9.U, "b1111011".U(7.W), // 9: a, f, b, g, c, d
                   "b0000000".U(7.W) // Default (all segments off)
               ))))))))))
}

object SevenSegmentDisplayMux extends App {
   ChiselStage.emitSystemVerilog(
    new SevenSegmentDisplayMux, 
    Array("-disable-all-randomization", "-strip-debug-info"))
}
