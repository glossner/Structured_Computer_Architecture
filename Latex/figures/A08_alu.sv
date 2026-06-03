module ALU(input    logic         crIn          ,
            input   logic [2:0]   func          ,
            input   logic [31:0]  left, right   ,
            output  logic         crOut         ,
            output  logic [31:0]  out           );
  always_comb 
	case(func)
        3'b000: {crOut, out} = left + right + crIn;     //add
        3'b001: {crOut, out} = left - right - crIn;     //sub
        3'b010: {crOut, out} = {1'b0, left & right};    //and
        3'b011: {crOut, out} = {1'b0, left | right};    //or
        3'b100: {crOut, out} = {1'b0, left ^ right};    //xor
        3'b101: {crOut, out} = {1'b0, ~left};           //not
        3'b110: {crOut, out} = {1'b0, left};            //left
        3'b111: {crOut, out} = {1'b0, left >> 1};       //shr
    endcase
endmodule