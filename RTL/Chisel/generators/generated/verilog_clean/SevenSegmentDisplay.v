module SevenSegmentDisplay (
	clock,
	reset,
	io_binIn,
	io_segOut
);
	input clock;
	input reset;
	input [3:0] io_binIn;
	output wire [6:0] io_segOut;
	wire [111:0] _GEN = 112'h00000000003dffe17edb3f3b587e;
	assign io_segOut = _GEN[io_binIn * 7+:7];
endmodule
