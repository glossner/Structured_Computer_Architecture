package config

object FirtoolConfig {
    
    //Comment out if not installed 
    val firtoolPath = "/home/jglossner/Programs/circt/firtool-1.99.1/bin/firtool"    

    // Default paths RTL/Chisel
    val generatedFirRTLPath = "generated_firrtl"
    val generatedVerilogAnnotatedPath = "generated_verilog_annotated"
    val generatedVerilogCleanPath = "generated_verilog_clean"
    val generatedSystemVerilogAnnotatedPath = "generated_systemverilog_annotated" 
    val generatedSystemVerilogCleanPath = "generated_systemverilog_clean" 
    val generatedNetlistPath = "generated_netlist"




    //######################################################################
    //# Edit below here only if you know what you are doing
    //# This ensures that if firtool isn't installed, it reverts to Chisel's
    //#######################################################################

    //########
    //# FIRRTL
    //########  
    val genFIRRTLArgs = 
        if (FirtoolConfig.firtoolPath.nonEmpty) { // Check if the path is not empty
            Array("--firtool-binary-path", firtoolPath)
        } else {
            Array.empty[String]
        }

    val firrtlArgs = genFIRRTLArgs ++ Array(
        "--target", "firrtl",
        "--target-dir", generatedFirRTLPath
        )

    //***************************************
    //* Verilog Annotated
    //***************************************
    val genVerilogAnnotatedArgs = 
        if (FirtoolConfig.firtoolPath.nonEmpty) { // Check if the path is not empty
            Array("--firtool-binary-path", firtoolPath)
        } else {
            Array.empty[String]
        }
    val verilogAnnotatedArgs =  genVerilogAnnotatedArgs ++ Array(
        "--target", "verilog",
        "--target-dir", generatedVerilogAnnotatedPath
        )
}