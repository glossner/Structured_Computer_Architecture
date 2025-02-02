// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.memory

import chisel3._

class Register(n: Int) extends Module {
  require(n > 1, "Register width must be greater than 1")

  val io = IO(new Bundle {
    val enable  = Input(Bool())    // Enable signal
    val reset   = Input(Bool())    // Reset signal
    val d       = Input(UInt(n.W)) // Data input
    val q       = Output(UInt(n.W)) // Data output
  })

  val reg = RegInit(0.U(n.W)) // Initialize register to 0

  when(io.reset) {
    reg := 0.U // Reset register to 0
  } .elsewhen(io.enable) {
    reg := io.d // Load new data if enable is high
  }

  io.q := reg // Output the register value
}

// Companion object for easy instantiation
object Register {
  def apply(n: Int): Register = new Register(n)
}
