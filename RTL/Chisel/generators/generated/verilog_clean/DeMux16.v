module Demultiplexer (
	io_input,
	io_select,
	io_outputs_0,
	io_outputs_1,
	io_outputs_2,
	io_outputs_3,
	io_outputs_4,
	io_outputs_5,
	io_outputs_6,
	io_outputs_7,
	io_outputs_8,
	io_outputs_9,
	io_outputs_10,
	io_outputs_11,
	io_outputs_12,
	io_outputs_13,
	io_outputs_14,
	io_outputs_15
);
	input [63:0] io_input;
	input [3:0] io_select;
	output wire [63:0] io_outputs_0;
	output wire [63:0] io_outputs_1;
	output wire [63:0] io_outputs_2;
	output wire [63:0] io_outputs_3;
	output wire [63:0] io_outputs_4;
	output wire [63:0] io_outputs_5;
	output wire [63:0] io_outputs_6;
	output wire [63:0] io_outputs_7;
	output wire [63:0] io_outputs_8;
	output wire [63:0] io_outputs_9;
	output wire [63:0] io_outputs_10;
	output wire [63:0] io_outputs_11;
	output wire [63:0] io_outputs_12;
	output wire [63:0] io_outputs_13;
	output wire [63:0] io_outputs_14;
	output wire [63:0] io_outputs_15;
	assign io_outputs_0 = (io_select == 4'h0 ? io_input : 64'h0000000000000000);
	assign io_outputs_1 = (io_select == 4'h1 ? io_input : 64'h0000000000000000);
	assign io_outputs_2 = (io_select == 4'h2 ? io_input : 64'h0000000000000000);
	assign io_outputs_3 = (io_select == 4'h3 ? io_input : 64'h0000000000000000);
	assign io_outputs_4 = (io_select == 4'h4 ? io_input : 64'h0000000000000000);
	assign io_outputs_5 = (io_select == 4'h5 ? io_input : 64'h0000000000000000);
	assign io_outputs_6 = (io_select == 4'h6 ? io_input : 64'h0000000000000000);
	assign io_outputs_7 = (io_select == 4'h7 ? io_input : 64'h0000000000000000);
	assign io_outputs_8 = (io_select == 4'h8 ? io_input : 64'h0000000000000000);
	assign io_outputs_9 = (io_select == 4'h9 ? io_input : 64'h0000000000000000);
	assign io_outputs_10 = (io_select == 4'ha ? io_input : 64'h0000000000000000);
	assign io_outputs_11 = (io_select == 4'hb ? io_input : 64'h0000000000000000);
	assign io_outputs_12 = (io_select == 4'hc ? io_input : 64'h0000000000000000);
	assign io_outputs_13 = (io_select == 4'hd ? io_input : 64'h0000000000000000);
	assign io_outputs_14 = (io_select == 4'he ? io_input : 64'h0000000000000000);
	assign io_outputs_15 = (&io_select ? io_input : 64'h0000000000000000);
endmodule
module DeMux16 (
	clock,
	reset,
	io_input,
	io_select,
	io_outputs_0,
	io_outputs_1,
	io_outputs_2,
	io_outputs_3,
	io_outputs_4,
	io_outputs_5,
	io_outputs_6,
	io_outputs_7,
	io_outputs_8,
	io_outputs_9,
	io_outputs_10,
	io_outputs_11,
	io_outputs_12,
	io_outputs_13,
	io_outputs_14,
	io_outputs_15
);
	input clock;
	input reset;
	input [63:0] io_input;
	input [3:0] io_select;
	output wire [63:0] io_outputs_0;
	output wire [63:0] io_outputs_1;
	output wire [63:0] io_outputs_2;
	output wire [63:0] io_outputs_3;
	output wire [63:0] io_outputs_4;
	output wire [63:0] io_outputs_5;
	output wire [63:0] io_outputs_6;
	output wire [63:0] io_outputs_7;
	output wire [63:0] io_outputs_8;
	output wire [63:0] io_outputs_9;
	output wire [63:0] io_outputs_10;
	output wire [63:0] io_outputs_11;
	output wire [63:0] io_outputs_12;
	output wire [63:0] io_outputs_13;
	output wire [63:0] io_outputs_14;
	output wire [63:0] io_outputs_15;
	Demultiplexer demux(
		.io_input(io_input),
		.io_select(io_select),
		.io_outputs_0(io_outputs_0),
		.io_outputs_1(io_outputs_1),
		.io_outputs_2(io_outputs_2),
		.io_outputs_3(io_outputs_3),
		.io_outputs_4(io_outputs_4),
		.io_outputs_5(io_outputs_5),
		.io_outputs_6(io_outputs_6),
		.io_outputs_7(io_outputs_7),
		.io_outputs_8(io_outputs_8),
		.io_outputs_9(io_outputs_9),
		.io_outputs_10(io_outputs_10),
		.io_outputs_11(io_outputs_11),
		.io_outputs_12(io_outputs_12),
		.io_outputs_13(io_outputs_13),
		.io_outputs_14(io_outputs_14),
		.io_outputs_15(io_outputs_15)
	);
endmodule
