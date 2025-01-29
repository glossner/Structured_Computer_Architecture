//`include "DEFINES.vh"
module bigMux (	input	logic	[31:0] 	aluOut, dataIn, value	,
				input	logic	[9:0]	pc						,
				input	logic	[5:0]	opCode					,
				input	logic			inta					,
				output	logic	[31:0]	muxOut					);
	logic	[1:0]	sel	;
				
	assign sel = inta ? 2'b00 : opCode[5:4]	;
				
	always_comb case(sel)
					2'b00: muxOut = pc		;
					2'b01: muxOut = value	;
					2'b10: muxOut = dataIn	;
					2'b11: muxOut = aluOut	;
				endcase	
endmodule