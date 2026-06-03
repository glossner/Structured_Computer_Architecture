module EXECUTE( input   logic [31:0]    leftOp2, rightOp2, value2,
                input   logic [5:0]     opCode2     ,
                input   logic [4:0]     destAddr2   ,
                input   logic [31:0]    dataIn      ,
                input   logic           we          ,
                input   logic [1:0]     opSel2      ,
                output  logic [9:0]     dataAddr    ,
                output  logic [31:0]    dataOut     ,
                output  logic           we3         ,
                output  logic [4:0]     destAddr3   ,
                output  logic [31:0]    result3     ,
                output  logic [5:0]     opCode3     ,
                input   logic           clk         );
    logic [31:0]    rightIn ;
    logic [31:0]    out     ;
    logic [31:0]    dataIn3 ;
    assign dataAddr = leftOp2[9:0]  ;
    assign dataOut  = rightOp2      ;
    always_comb case(opSel2)
                    2'b00: rightIn = rightOp2   ;
                    2'b01: rightIn = value2     ;
                    2'b10: rightIn = dataIn3    ;
                    2'b11: rightIn = 31'b0      ;
                endcase
    ALU alu(.out    (out    ),
            .leftIn (leftOp2),
            .rightIn(rightIn),
            .opCode (opCode2));
    // PipeReg3
    always_ff @(posedge clk)    begin   we3         <= we       ;
                                        destAddr3   <= destAddr2;
                                        result3     <= out      ;
                                        opCode3     <= opCode2  ;
                                        dataIn3     <= dataIn   ;
                                end
endmodule