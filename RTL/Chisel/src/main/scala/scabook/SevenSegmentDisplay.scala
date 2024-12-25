// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook

import chisel3._

class SevenSegmentDisplay extends Module {
  val io = IO(new Bundle {
    val binIn = Input(UInt(4.W))  // 4-bit binary input
    val segOut = Output(UInt(7.W)) // 7-bit output for seven-segment display
  })

    // Assign names to the binary I/O bits
    val B0 = WireDefault(io.binIn(0)).suggestName("B0")
    val B1 = WireDefault(io.binIn(1)).suggestName("B1")
    val B2 = WireDefault(io.binIn(2)).suggestName("B2")
    val B3 = WireDefault(io.binIn(3)).suggestName("B3")

    // Assign names to the 7-segment output bits
    val a = WireDefault(io.segOut(6)).suggestName("a")
    val b = WireDefault(io.segOut(5)).suggestName("b")
    val c = WireDefault(io.segOut(4)).suggestName("c")
    val d = WireDefault(io.segOut(3)).suggestName("d")
    val e = WireDefault(io.segOut(2)).suggestName("e")
    val f = WireDefault(io.segOut(1)).suggestName("f")
    val g = WireDefault(io.segOut(0)).suggestName("g")

    io.segOut := "b0000000".U(7.W) // Default: All segments off

    when(       io.binIn === 0.U) { io.segOut := "b1111110".U(7.W)
    } .elsewhen(io.binIn === 1.U) { io.segOut := "b0110000".U(7.W)
    } .elsewhen(io.binIn === 2.U) { io.segOut := "b1101101".U(7.W)
    } .elsewhen(io.binIn === 3.U) { io.segOut := "b1111001".U(7.W)
    } .elsewhen(io.binIn === 4.U) { io.segOut := "b0110011".U(7.W)
    } .elsewhen(io.binIn === 5.U) { io.segOut := "b1011011".U(7.W)
    } .elsewhen(io.binIn === 6.U) { io.segOut := "b1011111".U(7.W)
    } .elsewhen(io.binIn === 7.U) { io.segOut := "b1110000".U(7.W)
    } .elsewhen(io.binIn === 8.U) { io.segOut := "b1111111".U(7.W) 
    } .elsewhen(io.binIn === 9.U) { io.segOut := "b1111011".U(7.W) 
    }
}