module RALUtoyRISC( output  logic   [31:0]  dataOut,leftOp  ,
                    input   logic   [9:0]   pc              ,
                    input   logic   [5:0]   opCode          ,
                    input   logic   [31:0]  dataIn,v        ,
                    input   logic   [4:0]   l, r, d         ,
                    input   logic           inta, clk       );
    logic   [31:0]  leftIn, rightIn, muxOut, aluOut ;

    registerFile    regFile(.leftOp     (leftIn ),
                            .rightOp    (rightIn),
                            .in         (muxOut ),
                            .leftAddr   (l      ),
                            .rightAddr  (r      ),
                            .destAddr   (d      ),
                            .inta       (inta   ),
                            .opCode     (opCode ),
                            .clk        (clk    ));
    assign dataOut  = rightIn   ;
    assign leftOp   = leftIn    ;
    ALU alu(.out    (aluOut ),
            .leftIn (leftIn ),
            .rightIn(rightIn),
            .value  (v      ),
            .opCode (opCode ));
    bigMux bigMux(  .aluOut (aluOut),
                    .dataIn (dataIn),
                    .value  (v      ),
                    .pc     (pc     ),
                    .opCode (opCode ),
                    .inta   (inta   ),
                    .muxOut (muxOut ));
endmodule