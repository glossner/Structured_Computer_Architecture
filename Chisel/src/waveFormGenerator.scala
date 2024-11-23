import chisel3._
import chisel3.util._

class WaveFormGenerator extends Module {
  val io = IO(new Bundle {
    val randomWave = Output(Bool())
    val clockWave = Output(Bool())
  })

  // Random wave pattern based on a sequence of values
  val randomWaveSeq = VecInit(false.B, true.B, false.B, true.B, false.B)
  val randomIndex = RegInit(0.U(3.W))

  io.randomWave := randomWaveSeq(randomIndex)
  randomIndex := Mux(randomIndex === 4.U, 4.U, randomIndex + 1.U)

  // Clock waveform toggles every 2 cycles
  val clockToggle = RegInit(false.B)
  clockToggle := ~clockToggle
  io.clockWave := clockToggle
}

object WaveFormGenerator extends App {
  chisel3.Driver.execute(args, () => new WaveFormGenerator)
}
