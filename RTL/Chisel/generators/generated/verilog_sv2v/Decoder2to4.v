module Decoder2to4 (
	clock,
	reset,
	io_input,
	io_output
);
	input clock;
	input reset;
	input [1:0] io_input;
	output wire [3:0] io_output;
	assign io_output = 4'h1 << io_input;
endmodule
