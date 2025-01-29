/*************************************************************************
File name:      alu.sv
Circuit name:   arithmetic and logic unit
Description:    the circuit selects, using the selection code 'func', one
                of the 8 functions
*************************************************************************/
`include "0_define.vh"
module ALU( input   logic [3:0]   func  ,
            input   logic [31:0]  left	,
			input   logic [31:0]  right ,
			input   logic 		  crIn	,
            output  logic         crOut ,
            output  logic [31:0]  out  	);
	logic [32:0] sum	;
	logic [32:0] dif	;
	
	assign sum = left + right + crIn	;
	assign dif = left - right - crIn	;
	always_comb 
		case(func)
			`bigv  : {crOut, out} <= {1'b0, left[15:0], right[15:0]};
			`add   : {crOut, out} <=  sum			      			;
			`sub   : {crOut, out} <=  dif			      			;
			`addcr : {crOut, out} <= {32'b0, sum[32]}         		;
			`subcr : {crOut, out} <= {32'b0, dif[32]}         		;
			`lsh   : {crOut, out} <= {left[0], left[31:1]}        	;
			`ash   : {crOut, out} <= {left[31], left[31:1]} 		;
			`move  : {crOut, out} <= {1'b0, left}               	;
			`mult  : {crOut, out} <= {1'b0, left * right}         	;
			`bwand : {crOut, out} <= {1'b0, left & right}         	;
			`bwor  : {crOut, out} <= {1'b0, left | right}         	;
			`bwxor : {crOut, out} <= {1'b0, left ^ right}         	;
			default {crOut, out} <=  33'b0 - 1'b1         			;
		endcase
endmodule

 