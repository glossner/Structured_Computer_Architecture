module  DCDtoyRISC(
        input   logic   [5:0]   opCode                      ,
        output  logic           inta, dataRead, dataWrite   ,
        input   logic           intIn. reset, clk           );
// Interrupt automaton
    logic   ei  ;
    always_ff @(posedge clk) 
        if (reset)                              ei <= 0;
            else begin 	if (opCode == `eint) 	ei <= 1;
                        if (opCode == `dint) 	ei <= 0;
                        if (intIn & ei)      	ei <= 0;
                 end
    assign inta = intIn & ei;
// Data memory commands decoder
    always_comb begin
        dataRead    = (opCode == `read)     ? 1'b1  : 1'b0  ;
        dataWrite   = (opCode == `store)    ? 1'b1  : 1'b0  ;
    end
endmodule
