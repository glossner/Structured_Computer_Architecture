`include "0_DEFINES.vh"
module testPipelinedRISC;
    logic   inta    ;
    logic   intIn   ;
    logic   reset   ;
    logic   clk     ;

    integer i   ;

    `include "0_RISCcodeGenerator.sv"

    initial begin               clk = 0     ;
                    forever #1  clk = ~clk  ;
            end

    initial
        begin           intIn       = 1 ;
                        reset       = 1 ;
                        // DISPLAY THE CONTENT OF PROGRAM MEMORY
                        for (i=0; i<16; i=i+1)
                            $display("progMemory[%0d] \t = %b", i,
                                    dut.memSys.progMemory[i])   ;
                #4      reset       = 0 ;
                #100    $finish     ;
        end

    toyRISCsystem dut(  inta    ,
                        intIn   ,
                        reset   ,
                        clk     );

    // MONITOR FOR PROGRAM LAOD & CONTROLLER
    initial begin
     $monitor("t=%0d pc=%d  RF=[%0d, %0d, %0d, %0d, %0d, %0d, %0d, %0d]
            leftOp2=%0d rightOp2=%0d result3=%0d   intState=%b inta=%b",
                $time,
                dut.processor.pc,
                dut.processor.regFile.rf[0],
                dut.processor.regFile.rf[1],
                dut.processor.regFile.rf[2],
                dut.processor.regFile.rf[3],
                dut.processor.regFile.rf[4],
                dut.processor.regFile.rf[5],
                dut.processor.regFile.rf[30],
                dut.processor.regFile.rf[31],
                dut.processor.leftOp2,
                dut.processor.rightOp2,
                dut.processor.result3,
                dut.processor.dcd.intState,
                dut.processor.dcd.inta);
    end
endmodule