// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/

package generators

import circt.stage.ChiselStage
import scabook.multipliers._

object GenerateMultipliers extends App {
  val targetDir = "synth/multipliers"
  val firtoolArgs = Array(
    "--disable-all-randomization",
    "--strip-debug-info",
    "--lowering-options=disallowLocalVariables,disallowPackedArrays"
  )

  val multipliers = Seq(
    ("array", () => new ArrayMultiplier(32)),
    ("booth", () => new BoothMultiplier(32)),
    ("wallace", () => new WallaceTreeMultiplier(32)),
    ("rbm", () => new RedundantBinaryMultiplier(32)),
    ("behavioral", () => new BehavioralMultiplier(32))
  )

  for ((name, gen) <- multipliers) {
    println(s"Generating SystemVerilog for $name multiplier...")
    val dir = s"$targetDir/$name"
    ChiselStage.emitSystemVerilogFile(
      gen(),
      args = Array("--target-dir", dir),
      firtoolOpts = firtoolArgs
    )
    println(s"Successfully generated $name in $dir")
  }
}
