`include "0_DEFINES.vh"
module toyRISCforwarded(input   logic [31:0]  instruction   ,
                        output  logic [9:0]   pc            ,
                        input   logic         intIn         ,
                        output  logic         inta          ,
                        input   logic [31:0]  dataIn        ,
                        output  logic [31:0]  dataOut       ,
                        output  logic [9:0]   dataAddr      ,
                        output  logic         dataRead      ,
                        output  logic         dataWrite     ,
                        input   logic         reset         ,
                        input   logic         clk           );
    logic [31:0]    instruction1;
    logic [31:0]    instruction2;
    logic [31:0]    instruction3;
    logic [31:0]    instruction4;
    logic [9:0]     pc1         ;
    logic [31:0]    leftOp      ;
    logic [1:0]     nextPCsel   ;
    logic           we4         ;
    logic [31:0]    result4     ;
    logic [3:0]     opSel       ;
    logic [3:0]     opSel2      ;
    logic           zero        ;
    logic [31:0]    leftOp2     ;
    logic [31:0]    rightOp2    ;
    logic [31:0]    leftOp3     ;
    logic [31:0]    rightOp3    ;
    logic [31:0]    result3     ;
    logic           we          ;
    logic           memSel      ;

    DECODE dcd( .instruction1   (instruction1   ),
                .instruction2   (instruction2   ),
                .instruction3   (instruction3   ),
                .zero           (zero           ),
                .intIn          (intIn          ),
                .inta           (inta           ),
                .nextPCsel      (nextPCsel      ),
                .opSel          (opSel          ),
                .memSel         (memSel         ),
                .we             (we             ),
                .dataRead       (dataRead       ),
                .dataWrite      (dataWrite      ),
                .reset          (reset          ),
                .clk            (clk            ));

    INSTRFETCH progCount(   instruction ,
                            pc          ,
                            instruction1,
                            pc1         ,
                            leftOp      ,
                            nextPCsel   ,
                            reset       ,
                            clk         );

    OPSFETCH regFile(   instruction1,
                        instruction4,
                        pc1         ,
                        inta        ,
                        we4         ,
                        result4     ,
                        opSel       ,
                        zero        ,
                        leftOp      ,
                        leftOp2     ,
                        rightOp2    ,
                        opSel2      ,
                        instruction2,
                        clk         );

    EXECUTE execute(leftOp2     ,
                    rightOp2    ,
                    opSel2      ,
                    instruction2,
                    result4     ,
                    leftOp3     ,
                    result3     ,
                    rightOp3    ,
                    instruction3,
                    clk         );

    DATAMEM datamem(result3     ,
                    instruction3,
                    we          ,
                    memSel      ,
                    dataIn      ,
                    result4     ,
                    instruction4,
                    we4         ,
                    clk         );

    assign dataAddr = leftOp3   ;
    assign dataOut  = rightOp3  ;
endmodule // Synthesis results: #LUT=706, #FF=275, #DSP=3