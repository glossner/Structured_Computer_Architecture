module BehavioralAdderSubtractor (
	io_a,
	io_b,
	io_subtract,
	io_result
);
	input [3:0] io_a;
	input [3:0] io_b;
	input io_subtract;
	output wire [3:0] io_result;
	wire [3:0] io_a_0 = io_a;
	wire [3:0] io_b_0 = io_b;
	wire io_subtract_0 = io_subtract;
	wire _fullResult_T = io_subtract_0;
	wire [3:0] _io_result_T;
	wire [4:0] _GEN = {1'h0, io_a_0};
	wire [4:0] _GEN_0 = {1'h0, io_b_0};
	wire [4:0] _fullResult_T_1 = _GEN - _GEN_0;
	wire [3:0] _fullResult_T_2 = _fullResult_T_1[3:0];
	wire [4:0] _fullResult_T_3 = _GEN + _GEN_0;
	wire [3:0] _fullResult_T_4 = _fullResult_T_3[3:0];
	wire [3:0] fullResult = (_fullResult_T ? _fullResult_T_2 : _fullResult_T_4);
	assign _io_result_T = fullResult;
	wire [3:0] io_result_0 = _io_result_T;
	assign io_result = io_result_0;
endmodule
module BehavioralAdderSubtractor4 (
	clock,
	reset,
	io_a,
	io_b,
	io_subtract,
	io_sum
);
	input clock;
	input reset;
	input [3:0] io_a;
	input [3:0] io_b;
	input io_subtract;
	output wire [3:0] io_sum;
	wire [3:0] io_sum_0;
	wire [3:0] io_a_0 = io_a;
	wire [3:0] io_b_0 = io_b;
	wire io_subtract_0 = io_subtract;
	BehavioralAdderSubtractor io_sum_module(
		.io_a(io_a_0),
		.io_b(io_b_0),
		.io_subtract(io_subtract_0),
		.io_result(io_sum_0)
	);
	assign io_sum = io_sum_0;
endmodule
