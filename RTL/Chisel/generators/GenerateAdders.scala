// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package generators

import circt.stage.ChiselStage
import scabook.adders._

object GenerateAdders extends App {
  val firtoolFlags = Array(
    "-disable-all-randomization",
    "-strip-debug-info",
    "--lowering-options=disallowLocalVariables,disallowPackedArrays,disallowPackedStructAssignments,disallowDeclAssignments"
  )

  println("Generating SystemVerilog for 32-bit RippleCarryAdder...")
  ChiselStage.emitSystemVerilogFile(
    new RippleCarryAdder(width = 32),
    args = Array("--target-dir", "synth/adders/rca"),
    firtoolOpts = firtoolFlags
  )

  println("Generating SystemVerilog for 32-bit CarryLookAheadAdder...")
  ChiselStage.emitSystemVerilogFile(
    new CarryLookAheadAdder(width = 32),
    args = Array("--target-dir", "synth/adders/cla"),
    firtoolOpts = firtoolFlags
  )

  println("Generating SystemVerilog for 32-bit CarrySelectAdder...")
  ChiselStage.emitSystemVerilogFile(
    new CarrySelectAdder(width = 32, blockSize = 4),
    args = Array("--target-dir", "synth/adders/csa"),
    firtoolOpts = firtoolFlags
  )

  println("Generating SystemVerilog for 32-bit BrentKungAdder...")
  ChiselStage.emitSystemVerilogFile(
    new BrentKungAdder(width = 32),
    args = Array("--target-dir", "synth/adders/bka"),
    firtoolOpts = firtoolFlags
  )

  println("Generating SystemVerilog for 32-bit KoggeStoneAdder...")
  ChiselStage.emitSystemVerilogFile(
    new KoggeStoneAdder(width = 32),
    args = Array("--target-dir", "synth/adders/ksa"),
    firtoolOpts = firtoolFlags
  )

  println("Adder SystemVerilog generation complete.")
}
