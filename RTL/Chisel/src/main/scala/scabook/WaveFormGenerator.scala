// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.
package scabook

import chisel3._
import circt.stage.ChiselStage

class WaveFormGenerator extends Module {
  val io = IO(new Bundle {
    val randomWave = Output(Bool())
    val clockWave = Output(Bool())
  })

  // Random wave pattern based on a sequence of values
  val randomWaveSeq = VecInit(false.B, true.B, false.B, true.B, false.B)
  val randomIndex = RegInit(0.U(3.W))
  io.randomWave := randomWaveSeq(randomIndex)
  randomIndex := Mux(randomIndex === 4.U, 0.U, randomIndex + 1.U) 

  // Clock waveform toggles every 2 cycles
  val clockToggle = RegInit(false.B)
  clockToggle := ~clockToggle
  io.clockWave := clockToggle
}

object WaveFormGenerator extends App {
  ChiselStage.emitSystemVerilogFile(
    new WaveFormGenerator,
    Array("--target-dir", "generated_verilog_annotated")
    //firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info"),
  )
}