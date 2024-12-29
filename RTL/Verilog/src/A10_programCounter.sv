module programCounter #(parameter n=32)
        (   output  logic [31:0]    progAddr,
            input   logic [2:0]     sel     ,
            input   logic [31:0]    relValue,
            input   logic [31:0]    absValue,
            input   logic           reset   ,
            input   logic           clock   );
    logic [31:0]    pc      ;
    logic [31:0]    nextPC  ;

    always_ff @(posedge clock) if (reset)   pc <= 0     ;
                                else        pc <= nextPC;
    always_comb
     case(sel)
         3'b000: nextPC = pc                                        ;
         3'b001: nextPC = pc + relValue                             ;
         3'b010: nextPC = (absValue == 0) ? pc + relValue : pc + 1  ;
         3'b011: nextPC = (absValue == 0) ? pc + 1 : pc + relValue  ;
         3'b100: nextPC = absValue                                  ;
        default: nextPC = pc + 1                                    ;
     endcase

     assign progAddr = pc       ; // for pipelined version
     //assign progAddr = nextPC ; // for non-pipelined version
endmodule