module INSTRFETCH(  input   logic [31:0]    instruction ,       
                    output  logic [9:0]     pc          , 
                    output  logic [31:0]    instruction1,   
                    output  logic [9:0]     pc1         ,
                    input   logic [31:0]    leftOp      ,
                    input   logic [1:0]     nextPCsel   ,
                    input   logic           reset       ,       
                    input   logic           clk         );                    
                    
    always @(posedge clk)   
        if (reset)  begin           pc <= -1            ;
                                    instruction1 <= 0   ;   
                    end 
        else    begin   // PipeReg0
                    case(nextPCsel)
                        2'b00:  pc <= pc                        ;
                        2'b01:  pc <= pc+ 1                     ;
                        2'b10:  pc <= instruction1[9:0] + pc1   ;
                        2'b11:  pc <= leftOp                    ;
                    endcase
                    // PipeReg1
                    instruction1 <= instruction ;   
                    pc1          <= pc          ;
                end                 
endmodule

