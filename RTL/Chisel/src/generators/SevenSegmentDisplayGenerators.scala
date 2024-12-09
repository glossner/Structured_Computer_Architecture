// package generators

// import circt.stage.ChiselStage
// //import firrtl.options.TargetDirAnnotation
// //import sys.process._

// import scabook.SevenSegmentDisplay

// object SevenSegmentDisplayGenerator extends App {
//    ChiselStage.emitSystemVerilog(
//     new SevenSegmentDisplay, 
//     Array("-disable-all-randomization", "-strip-debug-info"))
// }

package generators

import circt.stage.ChiselStage // CIRCT-only import
import scabook.SevenSegmentDisplay

object SevenSegmentDisplayGenerator extends App {
  // Generate SystemVerilog directly with CIRCT
  ChiselStage.emitSystemVerilog(
    new scabook.SevenSegmentDisplay(), 
    Array(
      "-disable-all-randomization", // CIRCT-specific options
      "-strip-debug-info"           // Option to strip debug metadata
    )
  )
}




// object SevenSegmentDisplayGenerators {
//   def main(args: Array[String]): Unit = {
//     // Base target directory
//     val targetDir = "target"

//     // Subdirectories for specific outputs
//     val firrtlDir = s"$targetDir/firrtl"
//     val verilogDir = s"$targetDir/verilog"
//     val diagramDir = s"$targetDir/diagrams"

//     // Generate FIRRTL
//     (new ChiselStage).execute(
//       Array("-X", "firrtl"),
//       Seq(
//         ChiselGeneratorAnnotation(() => new scabook.SevenSegmentDisplay),
//         TargetDirAnnotation(firrtlDir)
//       )
//     )

//     // Generate SystemVerilog
//     (new ChiselStage).execute(
//       Array("-X", "verilog"),
//       Seq(
//         ChiselGeneratorAnnotation(() => new scabook.SevenSegmentDisplay),
//         TargetDirAnnotation(verilogDir)
//       )
//     )

//     // Use `firtool` to generate circuit diagrams from FIRRTL
//     val firFile = s"$firrtlDir/SevenSegmentDisplay.fir"
//     val diagramFile = s"$diagramDir/SevenSegmentDisplay.svg"

//     // Ensure the diagrams directory exists
//     new java.io.File(diagramDir).mkdirs()

//     // Firtool command to generate the SVG diagram
//     val firtoolCmd = s"firtool $firFile --output-file=$diagramFile --format=svg"

//     try {
//       println(s"Running: $firtoolCmd")
//       val result = firtoolCmd.!
//       if (result != 0) {
//         throw new RuntimeException(s"firtool failed with exit code $result")
//       } else {
//         println(s"Diagram successfully generated: $diagramFile")
//       }
//     } catch {
//       case e: Exception =>
//         println(s"Error running firtool: ${e.getMessage}")
//     }
//   }
// }