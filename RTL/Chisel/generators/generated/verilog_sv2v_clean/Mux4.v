module Multiplexer (
	io_inputs_0,
	io_inputs_1,
	io_inputs_2,
	io_inputs_3,
	io_select,
	io_output
);
	input [7:0] io_inputs_0;
	input [7:0] io_inputs_1;
	input [7:0] io_inputs_2;
	input [7:0] io_inputs_3;
	input [1:0] io_select;
	output wire [7:0] io_output;
	wire [31:0] _GEN = {io_inputs_3, io_inputs_2, io_inputs_1, io_inputs_0};
	assign io_output = _GEN[io_select * 8+:8];
endmodule
module Mux4 (
	clock,
	reset,
	io_inputs_0,
	io_inputs_1,
	io_inputs_2,
	io_inputs_3,
	io_select,
	io_output
);
	input clock;
	input reset;
	input [7:0] io_inputs_0;
	input [7:0] io_inputs_1;
	input [7:0] io_inputs_2;
	input [7:0] io_inputs_3;
	input [1:0] io_select;
	output wire [7:0] io_output;
	Multiplexer io_output_mux(
		.io_inputs_0(io_inputs_0),
		.io_inputs_1(io_inputs_1),
		.io_inputs_2(io_inputs_2),
		.io_inputs_3(io_inputs_3),
		.io_select(io_select),
		.io_output(io_output)
	);
endmodule
