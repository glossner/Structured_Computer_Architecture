`include "DEFINES.vh"
module testRISC;
    logic           inta    ;
    logic           intIn   ;
    logic           reset   ;
    logic           clk     ;
    integer i   ;
    `include "RISCcodeGenerator.sv"
    initial begin               clk = 0     ;
                    forever #1  clk = ~clk  ;
            end
    initial begin
            intIn       = 1 ;
            reset       = 1 ;
            // DISPLAY THE CONTENT OF PROGRAM MEMORY
            for (i=0; i<16; i=i+1)
                $display("progMemory[%0d] \t = %b", i,
                         progMemory[i]) ;
        #4  reset       = 0 ;
        #40 $finish     ;
    end
    logic   [31:0]  instr       ;
    logic   [9:0]   nextPC      ;
    logic   [31:0]  dataIn      ;
    logic   [31:0]  dataOut     ;
    logic   [9:0]   addr        ;
    logic           dataRead    ;
    logic           dataWrite   ;
    logic   [31:0]  dataMemory[0:1023]  ;
    logic   [31:0]  progMemory[0:1023]  ;
    logic   [31:0]  dataMemOut          ;
    always_ff @(posedge clk) begin
        if (dataRead) dataIn <= dataMemory[addr]    ;
        if (dataWrite) dataMemory[addr] <= dataOut  ;
        instr <= progMemory[nextPC]                 ;
    end
    toyRISC dut(instr       ,
                nextPC      ,
                intIn       ,
                inta        ,
                dataIn      ,
                dataOut     ,
                addr        ,
                dataRead    ,
                dataWrite   ,
                reset       ,
                clk         );
    // MONITOR FOR PROGRAM LAOD & CONTROLLER
    initial begin
     $monitor
        ("t=%0d pc=%d  RF=[%0d, %0d, %0d, %0d, ... %0d, %0d] ei=%b inta=%b",
         $time,
         dut.PC.pc,
         dut.RALU.regFile.rf[0],
         dut.RALU.regFile.rf[1],
         dut.RALU.regFile.rf[2],
         dut.RALU.regFile.rf[3],
         dut.RALU.regFile.rf[30],
         dut.RALU.regFile.rf[31],
         dut.DCD.ei,
         dut.inta);
    end
endmodule