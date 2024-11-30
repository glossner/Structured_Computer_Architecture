// See README.md for license details.

package scabook

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class WaveFormGeneratorSpec extends AnyFreeSpec with Matchers {

  "WaveFormGenerator should generate the correct waveforms" in {
    simulate(new WaveFormGenerator) { dut =>
      val randomWaveExpected = Seq(false, true, false, true, false)
      var randomIndex = 0
      var clockState = false

      for (cycle <- 0 until 10) { // Simulate 10 clock cycles
        // Check randomWave output
        dut.io.randomWave.expect(randomWaveExpected(randomIndex).B)
        randomIndex = (randomIndex + 1) % randomWaveExpected.length

        // Check clockWave toggling every cycle
        dut.io.clockWave.expect(clockState.B)
        clockState = !clockState

        // Step the simulation forward
        dut.clock.step()
      }
    }
  }
}
