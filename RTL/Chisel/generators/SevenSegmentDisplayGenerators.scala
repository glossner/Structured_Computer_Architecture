// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package generators

import config.FirtoolConfig

import circt.stage.ChiselStage // Correct import for Chisel 6.6.0
import scabook.SevenSegmentDisplay

import sys.process._  // For process execution
import java.io.File   // Import for File class
import java.nio.file.{Files, StandardCopyOption} // For file operations

object SevenSegmentDisplayGenerator extends App {  
  //********************************************
  //* Generate CIRCT/FIRRTL using ChiselStage
  //*********************************************
  (new ChiselStage).execute(
    FirtoolConfig.firrtlArgs,
    Seq(chisel3.stage.ChiselGeneratorAnnotation(() => new SevenSegmentDisplay)) 
  )

  //******************************************
  //* Generate CLEAN SystemVerilog 
  //*******************************************
  ChiselStage.emitSystemVerilogFile(
    new SevenSegmentDisplay, 
    firtoolOpts = Array("-strip-debug-info") 
  )

  // Move the generated file
  val sourceFile = new File("SevenSegmentDisplay.sv") // File in the root directory
  val targetFile = new File(FirtoolConfig.generatedSystemVerilogCleanPath + "/SevenSegmentDisplay.sv") 

  Files.move(
    sourceFile.toPath,
    targetFile.toPath,
    StandardCopyOption.REPLACE_EXISTING // Overwrite if the file exists
  )


  //***************************************
  //* Generate Annotated Verilog 
  //***************************************
  (new ChiselStage).execute(
    FirtoolConfig.verilogAnnotatedArgs,
    Seq(chisel3.stage.ChiselGeneratorAnnotation(() => new SevenSegmentDisplay)) 
  )


  //*********************************************
  //* Generate CLEAN Verilog using system firtool
  // ********************************************
  if (FirtoolConfig.firtoolPath.nonEmpty) { 

    val firrtlFile = new File(FirtoolConfig.generatedFirRTLPath + "/SevenSegmentDisplay.fir.mlir")
    val verilogFile = new File(FirtoolConfig.generatedVerilogCleanPath + "/SevenSegmentDisplay.v")

    val firtoolCleanVerilogCommand = Seq(
      "firtool",
      "-o", verilogFile.getAbsolutePath,
      "--verilog", 
      "--strip-debug-info", 
      firrtlFile.getAbsolutePath, 
    )

    val result = firtoolCleanVerilogCommand.!  // Execute the command

    if (result != 0) {
      println(s"Error: firtool execution failed with code $result")
    } else {
      println("Verilog generated successfully!")
    }

  }

  //**************************
  //* Generate Circuit Diagram
  //**************************


 //**********************************
 //* Parameter HELP
 //***********************************
 (new ChiselStage).execute(
    Array(
      "--help" 
    ),
    Seq(chisel3.stage.ChiselGeneratorAnnotation(() => new SevenSegmentDisplay))
  )
}
