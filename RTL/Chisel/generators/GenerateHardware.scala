// Run from RTL/Chisel sbt 'runMain generators.GenerateHardware'

// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package generators

import circt.stage.ChiselStage 
import sys.process._  
import java.io.File   
import java.nio.file.{Files, StandardCopyOption} 
import java.io.IOException
import java.io.PrintStream
import java.io.OutputStream


// Checks if firtool, yosys, and netlistsvg are in the environment PATH
// If yes, it uses the installed version
// If no, it falls back to Chisel supplied firtool

object GenerateHardware extends App {
  // Default paths when running from toplevel RTL/Chisel
  val generatedFirRTLPath = "generators/generated/firrtl"

  val generatedSystemVerilogAnnotatedPath = "generators/generated/systemverilog_annotated" 
  val generatedSystemVerilogCleanPath = "generators/generated/systemverilog_clean" 

  val generatedVerilogCleanPath = "generators/generated/verilog_clean"

  val generatedNetlistPath = "generators/generated/netlist"
  val generatedDiagramsPath = "generators/generated/diagrams"


  // Modules to Generate
  val modulesToGenerate = Seq(
     (() => new scabook.Decoder2to4, "Decoder2to4"),
    // (() => new scabook.DeMux16, "DeMux16"),
    // (() => new scabook.Mux4, "Mux4"),
    // (() => new scabook.SevenSegmentDisplay, "SevenSegmentDisplay"),
    // (() => new scabook.WaveFormGenerator, "WaveFormGenerator"),
    //  (() => new scabook.adders.BehavioralAdder4, "BehavioralAdder4"),  
    //(() => new scabook.addersubtractors.BehavioralAdderSubtractor64, "BehavioralAdderSubtractor64"),
  )


  //#####################################################
  //# Should not need to change anything below this line
  //#####################################################

  modulesToGenerate.foreach { case (module, moduleName) => 
    try {
      println("  ")
      println("###########################################################################")
      println(s"Processing module ${moduleName}")
      println("###########################################################################")
      println("  ")

      generateFIRRTL(module, moduleName)

      //firtools generates SystemVerilog when --verilog is used
      //  there is no way to change this behavior

      generateSystemVerilogAnnotated(module, moduleName)
      generateSystemVerilogClean(module, moduleName)
      convertSystemVerilogToVerilog(module, moduleName)
      generateNetlist(module, moduleName)
      generateSVG(module, moduleName)
    
    } catch {
      case e: Exception =>
        println(s"Error generating hardware ${e.getMessage}")
        throw e
    }
  }

  def generateFIRRTL(chiselModule: () => chisel3.Module, moduleName: String): Unit = {
    println(s"${moduleName}: generateFIRTL")
    val firrtlBaseArgs = Array("--target", "firrtl",
                                "--target-dir", generatedFirRTLPath)
    if(isCmdInstalled("firtool")) {
      
      val firtoolPath = getCmdPath("firtool")  // Remove the type annotation
      println(s"firtoolPath=$firtoolPath") 
      
      val firrtlArgs: Array[String] =        
          firrtlBaseArgs ++ Array("--firtool-binary-path", firtoolPath)
         
      (new ChiselStage).execute(
        firrtlArgs,
        Seq(chisel3.stage.ChiselGeneratorAnnotation(chiselModule)) 
      )
    } else {
      println("firtool not installed. See: https://github.com/llvm/circt/releases")
      println("Falling back to Chisel supplied firtool")
      (new ChiselStage).execute(
        firrtlBaseArgs,
        Seq(chisel3.stage.ChiselGeneratorAnnotation(chiselModule)) 
      )
    }
  }

  def generateSystemVerilogAnnotated(chiselModule: () => chisel3.Module, moduleName: String): Unit = {
    println(s"${moduleName}: generateSystemVerilogAnnotated")
    if ( isCmdInstalled("firtool")) {      
      val firrtlFile = new File( generatedFirRTLPath + "/" + moduleName + ".fir.mlir")
      val systemVerilogFile = new File( generatedSystemVerilogAnnotatedPath + "/" + moduleName + ".v")

      val firtoolCleanVerilogCommand = Seq(
        "firtool",
        "-o", systemVerilogFile.getAbsolutePath,
        "--verilog", 
         firrtlFile.getAbsolutePath, 
      )

      val result = firtoolCleanVerilogCommand.!  // Execute the command

      if (result != 0) {
        println(s"Error: firtool execution failed with code $result")
      } else {
        println("Annotated SystemVerilog generated successfully!")
      }
    } else {
      println("firtool not installed. See: https://github.com/llvm/circt/releases")
      println("Falling back to Chisel supplied firtool")
     
      (new ChiselStage).execute(
        Array("--target", "verilog",
              "--target-dir", generatedSystemVerilogAnnotatedPath),
        Seq(chisel3.stage.ChiselGeneratorAnnotation(chiselModule)) 
      )
    } 
  }

  def generateSystemVerilogClean(chiselModule: () => chisel3.Module, moduleName: String): Unit = {
    println(s"${moduleName}: generateSystemVerilogClean")
    if ( isCmdInstalled("firtool")) {      
      val firrtlFile = new File( generatedFirRTLPath + "/" + moduleName + ".fir.mlir")
      val systemVerilogFile = new File( generatedSystemVerilogCleanPath + "/" + moduleName + ".v")

      val firtoolCleanVerilogCommand = Seq(
        "firtool",
        "-o", systemVerilogFile.getAbsolutePath,
        "--verilog", 
        "--strip-debug-info", 
        firrtlFile.getAbsolutePath, 
      )

      val result = firtoolCleanVerilogCommand.!  // Execute the command

      if (result != 0) {
        println(s"Error: firtool execution failed with code $result")
      } else {
        println("Clean SystemVerilog generated successfully!")
      }
    } else {
      println("firtool not installed. See: https://github.com/llvm/circt/releases")
    }    
  }

  def convertSystemVerilogToVerilog(chiselModule: () => chisel3.Module, moduleName: String): Unit = {
    println(s"${moduleName}: convertSystemVerilogToVerilog")
    if ( isCmdInstalled("sv2v")) {      
      val systemVerilogCleanFile = new File( generatedSystemVerilogCleanPath + "/" + moduleName + ".v")
      val systemVerilogCleanFilePath = systemVerilogCleanFile.getAbsolutePath()

      val verilogCleanFile = new File( generatedVerilogCleanPath + "/" + moduleName + ".v")
      val verilogCleanFilePath = verilogCleanFile.getAbsolutePath()

      val sv2vCommand = Seq("sv2v", systemVerilogCleanFilePath)
      val result = (sv2vCommand #> new File(verilogCleanFilePath)).!

      if (result != 0) {
        println(s"Error: sv2v execution failed with code $result")
      } else {
        println("Clean Verilog generated successfully!")
      }
    } else {
      println("sv2v not installed. See: https://github.com/zachjs/sv2v")
    }    
  }

  def generateNetlist(chiselModule: () => chisel3.Module, moduleName: String): Unit = {
    println(s"${moduleName}: generateNetlist")
    if ( isCmdInstalled("yosys")) {      
      val systemVerilogFile = new File( generatedVerilogCleanPath + "/" + moduleName + ".v")
      val blifFile = new File( generatedNetlistPath + "/" + moduleName + ".blif")
      val jsonFile = new File( generatedNetlistPath + "/" + moduleName + ".json")

      // Generate BLIF file
      val yosysBLIFcommand = Seq(
          "yosys",
          "-p",
          s"read_verilog -sv $systemVerilogFile; synth -top $moduleName; write_blif $blifFile"
      )
      val blifResult = yosysBLIFcommand.!  
      if (blifResult != 0) {
        println(s"Error: firtool execution failed with code $blifResult")
      } else {
        println("BLIF file generated successfully!")
      }
 
      // Generate JSON because netlistsvg works better with JSON
      val yosysJSONcommand = Seq(
          "yosys",
          "-p",
          s"read_verilog -sv $systemVerilogFile; synth -top $moduleName; flatten; write_json $jsonFile"
        )
      val jsonResult = yosysJSONcommand.!  // Execute the command
      if (jsonResult != 0) {
        println(s"Error: firtool execution failed with code $jsonResult")
      } else {
        println("JSON generated successfully!")
      }

    } else {
      println("yosys not installed. See: https://github.com/YosysHQ/yosys and install it from https://github.com/YosysHQ/oss-cad-suite-build/releases")
    }    
  }

  def generateSVG(chiselModule: () => chisel3.Module, moduleName: String): Unit = {
    println(s"${moduleName}: generateSVG")
    if ( isCmdInstalled("netlistsvg")) {      
      val jsonFile = new File( generatedNetlistPath + "/" + moduleName + ".json")
      val jsonFileFullPath = jsonFile.getAbsolutePath()
      val svgFile = new File(generatedDiagramsPath + "/" + moduleName + ".svg")
      val svgFileFullPath = svgFile.getAbsolutePath()

      val netlistSVGcommand = s"netlistsvg $jsonFileFullPath -o $svgFileFullPath"

      println(s"netlistSVGcommand= $netlistSVGcommand")

      val netlistSVGresult = netlistSVGcommand.!  
      if (netlistSVGresult != 0) {
        println(s"Error: netlistsvg execution failed with code $netlistSVGresult")
      } else {
        println("SVG file generated successfully!")
      }
 
    } else {
      println("netlistsvg not installed. See: https://github.com/nturley/netlistsvg")
    }    
  }

  
  
  // Helper function to see if a command is installed (firtool, yosys, etc.)
  def isCmdInstalled(command: String): Boolean = {
      try {      
      
      // Disable errors during test because netlistsvg doesn't support --version
      val originalErr = System.err
      System.setErr(new PrintStream(new OutputStream() {
        override def write(b: Int): Unit = {} // Do nothing
      }))
      
      // Run the command with a test argument to verify installation
      Seq(command, "--version").! == 0

      //Turn error logging back on
      System.setErr(originalErr)

      println(s"$command installed")
      return true
      } catch {
      case _: Exception =>
          return false
      }
  }

  def getCmdPath(command: String): String = {
    try {
      val path = Seq("which", command).!!.trim
      if (path.nonEmpty) path
      else throw new IOException(s"Command '$command' not found in PATH")
    } catch {
      case _: Exception => "NOT FOUND" 
    }
  }

  def printHelp(): Unit = {
    (new ChiselStage).execute(
      Array("--help"),
      Seq(chisel3.stage.ChiselGeneratorAnnotation(() => new scabook.SevenSegmentDisplay))
    )
  }

}
