module BehavioralAdderSubtractor64 (
	clock,
	reset,
	io_a,
	io_b,
	io_subtract,
	io_result
);
	input clock;
	input reset;
	input [63:0] io_a;
	input [63:0] io_b;
	input io_subtract;
	output wire [63:0] io_result;
	assign io_result = io_a + (io_subtract ? ~io_b + 64'h0000000000000001 : io_b);
endmodule
