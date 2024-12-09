// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._

class SevenSegmentDisplay extends Module {
  val io = IO(new Bundle {
    val binIn = Input(UInt(4.W))  // 4-bit binary input
    val segOut = Output(UInt(7.W)) // 7-bit output for seven-segment display
  })

    io.segOut := "b0000000".U(7.W) // Default: All segments off

    when(       io.binIn === 0.U) {io.segOut :=  "b1111110".U(7.W)  // 0: a, b, c, d, e, f
    } .elsewhen(io.binIn === 1.U) { io.segOut := "b0110000".U(7.W)  // 1: b, c
    } .elsewhen(io.binIn === 2.U) { io.segOut := "b1101101".U(7.W)  // 2: a, b, g, e, d
    } .elsewhen(io.binIn === 3.U) { io.segOut := "b1111001".U(7.W)  // 3: a, b, g, c, d
    } .elsewhen(io.binIn === 4.U) { io.segOut := "b0110011".U(7.W)  // 4: f, g, b, c
    } .elsewhen(io.binIn === 5.U) { io.segOut := "b1011011".U(7.W)  // 5: a, f, g, c, d
    } .elsewhen(io.binIn === 6.U) { io.segOut := "b1011111".U(7.W)  // 6: a, f, g, e, d, c
    } .elsewhen(io.binIn === 7.U) { io.segOut := "b1110000".U(7.W)  // 7: a, b, c
    } .elsewhen(io.binIn === 8.U) { io.segOut := "b1111111".U(7.W)  // 8: All segments on
    } .elsewhen(io.binIn === 9.U) { io.segOut := "b1111011".U(7.W)  // 9: a, f, b, g, c, d
    }
}