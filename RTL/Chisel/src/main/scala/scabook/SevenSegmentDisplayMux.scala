// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._

class SevenSegmentDisplayMux extends Module {
  val io = IO(new Bundle {
    val binIn = Input(UInt(4.W))  // 4-bit binary input
    val segOut = Output(UInt(7.W)) // 7-bit output for seven-segment display
  })

  // Assign names to the binary I/O bits
  val B0 = io.binIn(0).suggestName("B0")
  val B1 = io.binIn(1).suggestName("B1")
  val B2 = io.binIn(2).suggestName("B2")
  val B3 = io.binIn(3).suggestName("B3")
  val a = io.segOut(6).suggestName("a")
  val b = io.segOut(5).suggestName("b")
  val c = io.segOut(4).suggestName("c")
  val d = io.segOut(3).suggestName("d")
  val e = io.segOut(2).suggestName("e")
  val f = io.segOut(1).suggestName("f")
  val g = io.segOut(0).suggestName("g")

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

