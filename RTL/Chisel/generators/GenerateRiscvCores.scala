// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package generators

import circt.stage.ChiselStage
import scabook.riscv.{RiscvFetchExecute, RiscvPipelined}

object GenerateRiscvCores extends App {
  val firtoolFlags = Array(
    "-disable-all-randomization",
    "-strip-debug-info",
    "--lowering-options=disallowLocalVariables,disallowPackedArrays,disallowPackedStructAssignments,disallowDeclAssignments"
  )

  println("Generating SystemVerilog for RiscvFetchExecute (Single-Cycle)...")
  ChiselStage.emitSystemVerilogFile(
    new RiscvFetchExecute(xlen = 32),
    args = Array("--target-dir", "synth/unpipelined"),
    firtoolOpts = firtoolFlags
  )

  println("Generating SystemVerilog for RiscvPipelined (4-Stage)...")
  ChiselStage.emitSystemVerilogFile(
    new RiscvPipelined(xlen = 32),
    args = Array("--target-dir", "synth/pipelined"),
    firtoolOpts = firtoolFlags
  )

  println("RTL generation complete. Verilog files written to synth/")
}
