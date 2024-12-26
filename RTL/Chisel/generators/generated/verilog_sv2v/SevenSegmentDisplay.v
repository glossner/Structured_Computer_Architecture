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
	wire [3:0] io_binIn_0 = io_binIn;
	wire [111:0] _GEN = 112'h00000000003dffe17edb3f3b587e;
	wire _B0_T = io_binIn_0[0];
	wire B0 = _B0_T;
	wire _B1_T = io_binIn_0[1];
	wire B1 = _B1_T;
	wire _B2_T = io_binIn_0[2];
	wire B2 = _B2_T;
	wire _B3_T = io_binIn_0[3];
	wire B3 = _B3_T;
	wire [6:0] io_segOut_0;
	wire _a_T = io_segOut_0[6];
	wire a = _a_T;
	wire _b_T = io_segOut_0[5];
	wire b = _b_T;
	wire _c_T = io_segOut_0[4];
	wire c = _c_T;
	wire _d_T = io_segOut_0[3];
	wire d = _d_T;
	wire _e_T = io_segOut_0[2];
	wire e = _e_T;
	wire _f_T = io_segOut_0[1];
	wire f = _f_T;
	wire _g_T = io_segOut_0[0];
	wire g = _g_T;
	assign io_segOut_0 = _GEN[io_binIn_0 * 7+:7];
	assign io_segOut = io_segOut_0;
endmodule
