`include "DEFINES.vh"
module toyRISC( input   logic [31:0]  instr              ,
                output  logic [9:0]   nextPC             ,
                input   logic         intIn              ,
                output  logic         inta               ,
                input   logic [31:0]  dataIn             ,
                output  logic [31:0]  dataOut            ,
                output  logic [9:0]   addr               ,
                output  logic         dataRead, dataWrite,
                input   logic         reset, clk         );
    logic   [5:0]   opCode   ;
    logic   [4:0]   d, l, r  ; // dest, left, right rf locations
    logic   [31:0]  v, leftOp; // immediate value, left operand
    logic   [9:0]   pc       ;

    assign opCode   = instr[31:26]                  ;
    assign d        = instr[25:21]                  ;
    assign l        = instr[20:16]                  ;
    assign r        = instr[15:11]                  ;
    assign v        = {{16{instr[15]}}, instr[15:0]};
    DCDtoyRISC  DCD(opCode      ,
                    intIn       ,
                    inta        ,
                    dataRead    ,
                    dataWrite   ,
                    reset, clk  );
    PCtoyRISC   PC( nextPC      ,
                    pc          ,
                    leftOp      ,
                    v[9:0]      ,
                    opCode      ,
                    inta        ,
                    reset, clk 	);
    RALUtoyRISC RALU(   dataOut ,
                        leftOp  ,
                        pc      ,
                        opCode  ,
                        dataIn  ,
                        v       ,
                        l       ,
                        r       ,
                        d       ,
                        inta    ,
                        clk     );

    assign addr = leftOp[15:0]  ;
endmodule // Synthesis results: #LUT=455, #FF=11, #DSP=6