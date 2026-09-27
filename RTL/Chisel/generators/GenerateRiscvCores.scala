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

  println("Generating SystemVerilog for RiscvFetchExecute (Single-Cycle RV32I)...")
  ChiselStage.emitSystemVerilogFile(
    new RiscvFetchExecute(xlen = 32, enableZmmul = false),
    args = Array("--target-dir", "synth/rv32i_unpipelined"),
    firtoolOpts = firtoolFlags
  )

  println("Generating SystemVerilog for RiscvPipelined (4-Stage RV32I)...")
  ChiselStage.emitSystemVerilogFile(
    new RiscvPipelined(xlen = 32, enableZmmul = false),
    args = Array("--target-dir", "synth/rv32i_pipelined"),
    firtoolOpts = firtoolFlags
  )

  println("Generating SystemVerilog for RiscvALU (RV32I)...")
  ChiselStage.emitSystemVerilogFile(
    new scabook.riscv.RiscvALU(width = 32, enableZmmul = false),
    args = Array("--target-dir", "synth/rv32i_alu"),
    firtoolOpts = firtoolFlags
  )

  println("Generating SystemVerilog for RiscvFetchExecute (Single-Cycle RV32I_Zmmul)...")
  ChiselStage.emitSystemVerilogFile(
    new RiscvFetchExecute(xlen = 32, enableZmmul = true),
    args = Array("--target-dir", "synth/zmmul_unpipelined"),
    firtoolOpts = firtoolFlags
  )

  println("Generating SystemVerilog for RiscvPipelined (4-Stage RV32I_Zmmul)...")
  ChiselStage.emitSystemVerilogFile(
    new RiscvPipelined(xlen = 32, enableZmmul = true),
    args = Array("--target-dir", "synth/zmmul_pipelined"),
    firtoolOpts = firtoolFlags
  )

  println("RTL generation complete. Verilog files written to synth/")
}
