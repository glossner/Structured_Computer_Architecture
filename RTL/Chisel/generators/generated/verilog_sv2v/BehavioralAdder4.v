module BehavioralAdder (
	io_a,
	io_b,
	io_sum
);
	input [3:0] io_a;
	input [3:0] io_b;
	output wire [3:0] io_sum;
	assign io_sum = io_a + io_b;
endmodule
module BehavioralAdder4 (
	clock,
	reset,
	io_a,
	io_b,
	io_sum
);
	input clock;
	input reset;
	input [3:0] io_a;
	input [3:0] io_b;
	output wire [3:0] io_sum;
	BehavioralAdder io_sum_adder(
		.io_a(io_a),
		.io_b(io_b),
		.io_sum(io_sum)
	);
endmodule
