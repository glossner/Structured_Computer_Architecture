`include "0_DEFINES.vh"
module toyRISCpipelined( input   logic [31:0]  instruction  ,
                         output  logic [9:0]   pc           ,
                         input   logic         intIn        ,
                         output  logic         inta         ,
                         input   logic [31:0]  dataIn       ,
                         output  logic [31:0]  dataOut      ,
                         output  logic [9:0]   dataAddr     ,
                         output  logic         dataRead     ,
                         output  logic         dataWrite    ,
                         input   logic         reset        ,
                         input   logic         clk              );
    logic [31:0]    instruction1;
    logic [9:0]     pc1         ;
    logic [31:0]    leftOp      ;
    logic [1:0]     nextPCsel   ;
    logic [31:0]    results     ;
    logic [5:0]     opCode1     ;
    logic [31:0]    leftOp2     ;
    logic [31:0]    rightOp2    ;
    logic [31:0]    value2      ;
    logic [5:0]     opCode2     ;
    logic [4:0]     destAddr2   ;
    logic           we3         ;
    logic [4:0]     destAddr3   ;
    logic [31:0]    result3     ;
    logic [5:0]     decode      ;
    logic           we          ;
    logic [1:0]     opSel       ;
    logic [1:0]     opSel2      ;
    logic           zero        ;
    logic [5:0]     opCode3     ;

    DECODE dcd( .opCode1    (instruction1[31:26]),
                .zero       (zero               ),
                .opCode2    (opCode2            ),
                .intIn      (intIn              ),
                .inta       (inta               ),
                .nextPCsel  (nextPCsel          ),
                .opSel      (opSel              ),
                .we         (we                 ),
                .dataRead   (dataRead           ),
                .dataWrite  (dataWrite          ),
                .reset      (reset              ),
                .clk        (clk                ));

    INSTRFETCH progCount(   instruction ,
                            pc          ,
                            instruction1,
                            pc1         ,
                            leftOp      ,
                            nextPCsel   ,
                            reset       ,
                            clk         );

    OPSFETCH regFile(   instruction1,
                        pc1         ,
                        inta        ,
                        we3         ,
                        destAddr3   ,
                        result3     ,
                        opSel       ,
                        opSel2      ,
                        zero        ,
                        leftOp      ,
                        leftOp2     ,
                        rightOp2    ,
                        value2      ,
                        opCode2     ,
                        destAddr2   ,
                        clk         );

    EXECUTE execute(leftOp2     ,
                    rightOp2    ,
                    value2      ,
                    opCode2     ,
                    destAddr2   ,
                    dataIn      ,
                    we          ,
                    opSel2      ,
                    dataAddr    ,
                    dataOut     ,
                    we3         ,
                    destAddr3   ,
                    result3     ,
                    opCode3     ,
                    clk         );
endmodule // Synthesis results: #LUT=346, #FF=219, #DSP=3