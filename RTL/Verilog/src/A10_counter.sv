`include "counterDefines.vh"
module counter( output  logic [3:0] out ,
                input   logic [2:0] op  ,
                input   logic [3:0] in  ,
                input   logic       clk );

    always_ff @(posedge clk)    case(op)
                                   `reset   : out <= 0      ;
                                   `load    : out <= in     ;
                                   `up      : out <= out + 1;
                                   `down    : out <= out - 1;
                                   default  : out <= out    ;
                                endcase
endmodule