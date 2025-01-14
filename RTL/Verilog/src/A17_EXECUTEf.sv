module EXECUTE( input   logic [31:0]    leftOp2     ,
                input   logic [31:0]    rightOp2    ,
                input   logic [3:0]     opSel2      ,
                input   logic [31:0]    instruction2,
                input   logic [31:0]    result4     ,
                output  logic [31:0]    leftOp3     ,
                output  logic [31:0]    result3     ,
                output  logic [31:0]    rightOp3    ,
                output  logic [31:0]    instruction3,
                input                   clk         );
    logic [31:0]    rightIn ;
    logic [31:0]    leftIn  ;
    logic [31:0]    out     ;
    logic [31:0]    value   ;

    assign value = {{16{instruction2[15]}}, instruction2[15:0]} ;

    always_comb case(opSel2[1:0])
                    2'b00: rightIn = rightOp2   ;
                    2'b01: rightIn = result3    ;
                    2'b10: rightIn = result4    ;
                    2'b11: rightIn = value      ;
                endcase
    always_comb case(opSel2[3:2])
                    2'b00: leftIn = leftOp2;
                    2'b01: leftIn = result3;
                    2'b10: leftIn = result4;
                    2'b11: leftIn = 31'b0   ;
                endcase

    ALU alu(.out    (out                ),
            .leftIn (leftIn             ),
            .rightIn(rightIn            ),
            .opCode (instruction2[31:26]));

    // PipeReg3
    always_ff @(posedge clk)
        begin   leftOp3         <= leftIn       ;
                result3         <= out          ;
                rightOp3        <= rightIn      ;
                instruction3    <= instruction2 ;
        end
endmodule