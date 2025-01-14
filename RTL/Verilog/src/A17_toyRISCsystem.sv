`include "0_DEFINES.vh"
module toyRISCsystem(   output  logic   inta                ,
                        input   logic   intIn, reset, clk   );
    logic   [9:0]   PC, dataAddr                ;
    logic   [31:0]  instruction, dataIn, dataOut;
    logic           dataRead, dataWrite         ;

    toyRISC processor(  instruction ,
                        PC          ,
                        intIn       ,
                        inta        ,
                        dataIn      ,
                        dataOut     ,
                        dataAddr    ,
                        dataRead    ,
                        dataWrite   ,
                        reset       ,
                        clk         );
    memorySystem memSys(instruction ,
                        PC          ,
                        dataIn      ,
                        dataOut     ,
                        dataAddr    ,
                        dataRead    ,
                        dataWrite   ,
                        clk         );
endmodule