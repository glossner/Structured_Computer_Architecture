module PCtoyRISC(   output  logic   [9:0]   nextPC, pc      ,
                    input   logic   [31:0]  leftOp          ,
                    input   logic   [9:0]   v               ,
                    input   logic   [5:0]   opCode          ,
                    input   logic           inta, reset, clk);

    always_ff @(posedge clk) 
	    if (reset)         pc <= -1            ;
            else if (inta) pc <= leftOp[9:0]   ;
                    else   pc <= nextPC        ;
    always_comb 
	    case(opCode)
            `rjmp   : nextPC = pc + v                         ;
            `zbr    : nextPC = (leftOp == 0) ? pc + v : pc + 1;
            `nzbr   : nextPC = (leftOp != 0) ? pc + v : pc + 1;
            `ret    : nextPC = leftOp                         ;
            `halt   : nextPC = pc                             ;
            default : nextPC = pc + 1                         ;
        endcase
endmodule