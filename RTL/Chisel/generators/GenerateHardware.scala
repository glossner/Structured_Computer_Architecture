// Run from RTL/Chisel sbt 'runMain generators.GenerateHardware'

// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package generators

import circt.stage.ChiselStage 
import sys.process._  
import java.io.File   
import java.nio.file.{Files, Paths, StandardCopyOption, NoSuchFileException} 

import java.io.IOException
import java.io.PrintStream
import java.io.OutputStream


// Checks if firtool, sv2v, yosys, and netlistsvg are in the environment PATH
// If yes, it uses the installed version
// If no, it falls back to Chisel supplied tools

object GenerateHardware extends App {
  
  val shouldDeleteIntermediateFiles = true

  // Modules to Generate
  val modulesToGenerate = Seq(
    //  (() => new scabook.Decoder2to4, "Decoder2to4"),
    //  (() => new scabook.DeMux16, "DeMux16"),
    //  (() => new scabook.Mux4, "Mux4"),
    // (() => new scabook.SevenSegmentDisplay, "SevenSegmentDisplay"),
    //  (() => new scabook.SevenSegmentDisplayMux, "SevenSegmentDisplayMux"),
    //  (() => new scabook.SevenSegmentDisplayFlat, "SevenSegmentDisplayFlat"),
    //  (() => new scabook.WaveFormGenerator, "WaveFormGenerator"),
    //  (() => new scabook.adders.BehavioralAdder4, "BehavioralAdder4"),  
    //  (() => new scabook.addersubtractors.BehavioralAdderSubtractor4, "BehavioralAdderSubtractor4"), 
    //  (() => new scabook.addersubtractors.BehavioralAdderSubtractorHW4, "BehavioralAdderSubtractorHW4"), 
    //  (() => new scabook.addersubtractors.BehavioralAdderSubtractor64, "BehavioralAdderSubtractor64"),
    (() => new scabook.addersubtractors.MultifunctionAdderSubtractor64, "MultifunctionAdderSubtractor64"), 
  )

  // Default paths when running from toplevel RTL/Chisel
  val generatedFirRTLPath = "generators/generated/firrtl"     //*.fir and *.fir.mlir

  val generatedSystemVerilogAnnotatedPath = "generators/generated/systemverilog_annotated" 
 
  val generatedSv2vPath = "generators/generated/verilog_sv2v"
  val generatedVerilogElaboratedPath = "generators/generated/verilog_yosys_elaborated"
  val generatedVerilogElaboratedFlatPath = "generators/generated/verilog_yosys_elaborated_flat"

  val generatedNetlistPath = "generators/generated/netlist"
  val generatedDiagramsPath = "generators/generated/diagrams"

 
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

      //firtools generates SystemVerilog when --verilog is used
      generateFIRRTLandSystemVerilog(module, moduleName)
      convertSystemVerilogToVerilog(module, moduleName)
      synthesizeNetlist(module, moduleName)
      generateSVG(module, moduleName)
      convertSVGtoPNG(module, moduleName)
      if(shouldDeleteIntermediateFiles) deleteIntermediateFiles(module, moduleName)
      //printHelp()
    
    } catch {
      case e: Exception =>
        println(s"Error generating hardware ${e.getMessage}")
        throw e
    }
  }

  def generateFIRRTLandSystemVerilog(chiselModule: () => chisel3.Module, moduleName: String): Unit = {
    println(s"${moduleName}: generateFIRTL")
    // Stage1: Dump fir file with all annotations. Will also generate stipped fir.mlir file
    val firrtlBaseArgs = Array("--target", "firrtl",
                               "--dump-fir",  // Contains all suggestNames()
                               "--preserve-aggregate", "all",
                               "--target-dir", generatedFirRTLPath)
    
    if(isCmdInstalled("firtool")) {      
      val firtoolPath = getCmdPath("firtool") 
      println(s"firtoolPath=$firtoolPath") 
      
      // --preserve-values=none keeps suggestName values
      val firrtlArgs: Array[String] =        
          firrtlBaseArgs ++ Array("--firtool-binary-path", firtoolPath)
         
      (new ChiselStage).execute(
        firrtlArgs,
        Seq(chisel3.stage.ChiselGeneratorAnnotation(chiselModule)) 
      )

      // Stage2: Use the dumped fir file to produce final mlir and annotated SystemVerilog files
      val firFile = new File( generatedFirRTLPath + "/" + moduleName + ".fir")
      val mlirFile = new File( generatedFirRTLPath + "/" + moduleName + ".fir.mlir")
      val svFile = new File( generatedSystemVerilogAnnotatedPath + "/" + moduleName + ".sv")
      val annotationFile =  new File( generatedFirRTLPath + "/" + moduleName + "_annotated.txt")
      val mlirCommand = Seq(
        "firtool",
        //"-O=debug", // Compiler set to debug
        //"-g",   // Enable debug information
        "-o", svFile.getAbsolutePath,
        //s"--output-annotation-file=${annotationFile.getAbsolutePath()}",  
        //"--preserve-aggregate=all",
        //"--split-aggregate",
        "--preserve-values=all",
        s"--output-final-mlir=${mlirFile.getAbsolutePath()}", //output fir.mlir
        "--format=fir",  //input file format
         firFile.getAbsolutePath, 
      )

      println(s"$moduleName - firtool command = $mlirCommand")

      val result = mlirCommand.!  // Execute the command

      if (result != 0) {
        println(s"Error: firtool execution failed with code $result")
      } else {
        println("Annotated SystemVerilog generated successfully!")
      }

      //println(s"$moduleName: Successfully generated fir file")
    } else {
      println("firtool not installed. See: https://github.com/llvm/circt/releases")
      println("Falling back to Chisel supplied firtool")
      (new ChiselStage).execute(
        firrtlBaseArgs,
        Seq(chisel3.stage.ChiselGeneratorAnnotation(chiselModule)) 
      )
    }
  }

  def convertSystemVerilogToVerilog(chiselModule: () => chisel3.Module, moduleName: String): Unit = {
    println(s"${moduleName}: convertSystemVerilogToVerilog")
    if ( isCmdInstalled("sv2v")) {      
      val systemVerilogAnnotatedFile = new File( generatedSystemVerilogAnnotatedPath + "/" + moduleName + ".sv")
      val systemVerilogAnnotatedFilePath = systemVerilogAnnotatedFile.getAbsolutePath()

      val verilogSv2vFile = new File( generatedSv2vPath + "/" + moduleName + ".v")
      val verilogSv2vFilePath = verilogSv2vFile.getAbsolutePath()

      val sv2vCommand = Seq("sv2v", systemVerilogAnnotatedFilePath)
      val result = (sv2vCommand #> new File(verilogSv2vFilePath)).!

      if (result != 0) {
        println(s"Error: sv2v execution failed with code $result")
      } else {
        println("Clean Verilog generated successfully!")
      }
    } else {
      println("sv2v not installed. See: https://github.com/zachjs/sv2v")
    }    
  }

  def synthesizeNetlist(chiselModule: () => chisel3.Module, moduleName: String): Unit = {
    println(s"${moduleName}: generateNetlist")
    if ( isCmdInstalled("yosys")) {      
      val systemVerilogInFile = new File( generatedSv2vPath + "/" + moduleName + ".v")
      val verilogOutFile = new File( generatedVerilogElaboratedPath + "/" + moduleName + ".v")
      val verilogOutFileFlat = new File( generatedVerilogElaboratedFlatPath + "/" + moduleName + "_flat" + ".v")

      val edifFile = new File( generatedNetlistPath + "/" + moduleName + ".edif")
      val jsonFile = new File( generatedNetlistPath + "/" + moduleName + ".json")
      val jsonFileFlattened = new File( generatedNetlistPath + "/" + moduleName + "_flat" + ".json")
  
      // yosys flags
       val yosysSynthFlags = s"synth -top $moduleName"
        

      // Generate synthesized (elaborated) verilog file
      //Aggressive flags: "read_verilog your_design.v; synth -flatten -abc2; opt -purge; abc -script +resyn2;  opt_clean; write_verilog optimized_design.v;"
      val yosysVerilogcommand = Seq(
          "yosys",
           "-p",
          s"read_verilog -sv $systemVerilogInFile;  $yosysSynthFlags; write_verilog $verilogOutFile"
      )
      
      val yosysVerilogResult = yosysVerilogcommand.!  
      if (yosysVerilogResult != 0) {
        println(s"Error: yosys execution failed with code $yosysVerilogResult")
      } else {
        println("yosys synthesized (elaborated) verilog file generated successfully!")
      }

      // Synthesize flattened verilog
      //Aggressive flags: "read_verilog your_design.v; synth -flatten -abc2; opt -purge; abc -script +resyn2;  opt_clean; write_verilog optimized_design.v;"
      val yosysSynthFlatFlags =  s"synth -flatten -top $moduleName; rename $moduleName ${moduleName}_flat"      
      val yosysVerilogCommandFlat = Seq(
          "yosys",
           "-p",
          s"read_verilog -sv $systemVerilogInFile; $yosysSynthFlatFlags; write_verilog $verilogOutFileFlat"
      )
      
      val yosysVerilogFlatResult = yosysVerilogCommandFlat.!  
      if (yosysVerilogFlatResult != 0) {
        println(s"Error: yosys execution failed with code $yosysVerilogFlatResult")
      } else {
        println("yosys synthesized (elaborated) verilog file generated successfully!")
      }

      // Write EDIF and JSON
      val yosysWriteCommand = Seq(
          "yosys",
           "-p",
          s"read_verilog -sv $verilogOutFile; write_edif $edifFile; write_json $jsonFile; read_verilog -sv $verilogOutFileFlat;  write_json $jsonFileFlattened"
      )
      // show -format pdf $moduleName  $pdfFile works but the symbols are not IEEE
      
      val yosysWriteResult = yosysWriteCommand.!  
      if (yosysWriteResult != 0) {
        println(s"Error: yosys writeCommand execution failed with code $yosysWriteResult")
      } else {
        println("EDIF and JSON files generated successfully!")
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
      val jsonFileFlattened = new File( generatedNetlistPath + "/" + moduleName + "_flat" + ".json")
      val jsonFileFlattenedFullPath = jsonFileFlattened.getAbsolutePath()
      
      val svgFile = new File(generatedDiagramsPath + "/" + moduleName + ".svg")
      val svgFileFullPath = svgFile.getAbsolutePath()
      val svgFileFlattened = new File(generatedDiagramsPath + "/" + moduleName + "_flat" + ".svg")
      val svgFileFlattenedFullPath = svgFileFlattened.getAbsolutePath()

      // hierarchical svg
      val netlistSVGcommand = s"netlistsvg $jsonFileFullPath -o $svgFileFullPath"
      val netlistSVGresult = netlistSVGcommand.!  
      if (netlistSVGresult != 0) {
        println(s"Error: netlistsvg execution failed with code $netlistSVGresult")
      } else {
        println("SVG file generated successfully!")
      }

      // flattened svg
      val netlistSVGflatCommand = s"netlistsvg $jsonFileFlattenedFullPath -o $svgFileFlattenedFullPath"
      val netlistSVGFlatResult = netlistSVGflatCommand.!  
      if (netlistSVGFlatResult != 0) {
        println(s"Error: netlistsvg flattened execution failed with code $netlistSVGFlatResult")
      } else {
        println("SVG Flattened file generated successfully!")
      }
 
    } else {
      println("netlistsvg not installed. See: https://github.com/nturley/netlistsvg")
    }    
  }

  
  def convertSVGtoPNG(chiselModule: () => chisel3.Module, moduleName: String): Unit = {
    println(s"${moduleName}: convertSVGtoPNG")
    if ( isCmdInstalled("rsvg-convert")) {      
      val svgFile = new File(generatedDiagramsPath + "/" + moduleName + ".svg")
      val pngFile = new File(generatedDiagramsPath + "/" + moduleName + ".png")

      val svgFlatFile = new File(generatedDiagramsPath + "/" + moduleName + "_flat" + ".svg")
      val pngFlatFile = new File(generatedDiagramsPath + "/" + moduleName + "_flat" + ".png")
      

      // hierarchical
      val convertSVGcommand = s"rsvg-convert -f png -d 300 -p 300 -o $pngFile $svgFile"
      val convertSVGresult = convertSVGcommand.!  
      if (convertSVGresult != 0) {
        println(s"Error: convertSVGtoPNG execution failed with code $convertSVGresult")
      } else {
        println("PNG file generated successfully!")
      }
     
      // flattened
      val convertSVGflatCommand = s"rsvg-convert -f png -d 300 -p 300 -o $pngFlatFile $svgFlatFile"
      val convertSVGflatResult = convertSVGflatCommand.!  
      if (convertSVGflatResult != 0) {
        println(s"Error: convertSVGtoPNG Flattened execution failed with code $convertSVGflatResult")
      } else {
        println("PNG Flattened file generated successfully!")
      }


    } else {
      println("rsvg-convert not installed. try: sudo apt install librsvg2-bin")
    }    
  }


  def deleteIntermediateFiles(chiselModule: () => chisel3.Module, moduleName: String): Unit = {
    println(s"${moduleName}: deleteIntermediateFiles")

    val mlirFile = generatedFirRTLPath + "/" + moduleName + ".fir.mlir"
    val edifFile = generatedNetlistPath + "/" + moduleName + ".edif"
    val jsonNetlistFile =  generatedNetlistPath + "/" + moduleName + ".json" 
    val jsonNetlistFlatFile =  generatedNetlistPath + "/" + moduleName + "_flat" + ".json" 
    val svgFile = generatedDiagramsPath + "/" + moduleName + ".svg" 
    val svgFlatFile = generatedDiagramsPath + "/" + moduleName + "_flat" + ".svg" 
    

    val files = Array(edifFile, jsonNetlistFile, jsonNetlistFlatFile, svgFile, svgFlatFile)

    files.foreach { file =>
      val path = Paths.get(file)
      try {
        Files.delete(path)
        println(s"File $file deleted successfully.")
      } catch {
        case e: NoSuchFileException =>
          println(s"File $file does not exist.")
        case e: SecurityException =>
          println(s"Permission denied to delete $file.")
        case e: Exception =>
          println(s"An error occurred while deleting $file: ${e.getMessage}")
      }
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
    // circt 1.99.1 for chisel execute()
    // [info] Usage: circt [options] [<arg>...]
    // [info]   --help                   prints this usage text
    // [info] Shell Options
    // [info]   <arg>...                 optional unbounded args
    // [info]   -td, --target-dir <directory>
    // [info]                            Work directory (default: '.')
    // [info]   -faf, --annotation-file <file>
    // [info]                            An input annotation file
    // [info]   -foaf, --output-annotation-file <file>
    // [info]                            An output annotation file
    // [info]   --show-registrations     print discovered registered libraries and transforms
    // [info] Logging Options
    // [info]   -ll, --log-level {error|warn|info|debug|trace}
    // [info]                            Set global logging verbosity (default: None
    // [info]   -cll, --class-log-level <FullClassName:{error|warn|info|debug|trace}>...
    // [info]                            Set per-class logging verbosity
    // [info]   --log-file <file>        Log to a file instead of STDOUT
    // [info]   -lcn, --log-class-names  Show class names and log level in logging output
    // [info] Chisel options
    // [info]   --module <package>.<module>
    // [info]                            The name of a Chisel module to elaborate (module must be in the classpath)
    // [info]   --full-stacktrace        Show full stack trace when an exception is thrown
    // [info]   --throw-on-first-error   Throw an exception on the first error instead of continuing
    // [info]   --warnings-as-errors     Treat warnings as errors
    // [info]   --warn-conf <value>      Warning configuration
    // [info]   --warn-conf-file <value>
    // [info]                            Warning configuration
    // [info]   --source-root <file>     Root directory for source files, used for enhanced error reporting
    // [info]   --dump-fir               Write the intermediate .fir file
    // [info] CIRCT (MLIR FIRRTL Compiler) options
    // [info]   --target {chirrtl|firrtl|hw|verilog|systemverilog}
    // [info]                            The CIRCT
    // [info]   --preserve-aggregate <value>
    // [info]                            Do not lower aggregate types to ground types
    // [info]   --split-verilog          Indicates that "firtool" should emit one-file-per-module and write separate outputs to separate files
    // [info]   --firtool-binary-path path
    // [info]                            Specifies the path to the "firtool" binary Chisel should use.
    // [info] AspectLibrary
    // [info]   --with-aspect <package>.<aspect>
    // [info]                            The name/class of an aspect to compile with (must be a class/object without arguments!)

    (new ChiselStage).execute(
      Array("--help"),
      Seq(chisel3.stage.ChiselGeneratorAnnotation(() => new scabook.SevenSegmentDisplay))
    )
  }

}
