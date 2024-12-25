module SevenSegmentDisplayFlat (
	clock,
	reset,
	io_binIn_B0,
	io_binIn_B1,
	io_binIn_B2,
	io_binIn_B3,
	io_segOut_a,
	io_segOut_b,
	io_segOut_c,
	io_segOut_d,
	io_segOut_e,
	io_segOut_f,
	io_segOut_g
);
	input clock;
	input reset;
	input io_binIn_B0;
	input io_binIn_B1;
	input io_binIn_B2;
	input io_binIn_B3;
	output wire io_segOut_a;
	output wire io_segOut_b;
	output wire io_segOut_c;
	output wire io_segOut_d;
	output wire io_segOut_e;
	output wire io_segOut_f;
	output wire io_segOut_g;
	wire io_binIn_B0_0 = io_binIn_B0;
	wire io_binIn_B1_0 = io_binIn_B1;
	wire io_binIn_B2_0 = io_binIn_B2;
	wire io_binIn_B3_0 = io_binIn_B3;
	wire [111:0] _GEN = 112'h00000000003dffe17edb3f3b587e;
	wire _io_segOut_a_T;
	wire _io_segOut_b_T;
	wire _io_segOut_c_T;
	wire _io_segOut_d_T;
	wire _io_segOut_e_T;
	wire _io_segOut_f_T;
	wire _io_segOut_g_T;
	wire io_segOut_a_0;
	wire io_segOut_b_0;
	wire io_segOut_c_0;
	wire io_segOut_d_0;
	wire io_segOut_e_0;
	wire io_segOut_f_0;
	wire io_segOut_g_0;
	wire [3:0] _binIn_T;
	wire [3:0] binIn;
	wire [1:0] binIn_lo = {io_binIn_B1_0, io_binIn_B0_0};
	wire [1:0] binIn_hi = {io_binIn_B3_0, io_binIn_B2_0};
	assign _binIn_T = {binIn_hi, binIn_lo};
	assign binIn = _binIn_T;
	wire [6:0] segOut;
	assign _io_segOut_a_T = segOut[6];
	assign io_segOut_a_0 = _io_segOut_a_T;
	assign _io_segOut_b_T = segOut[5];
	assign io_segOut_b_0 = _io_segOut_b_T;
	assign _io_segOut_c_T = segOut[4];
	assign io_segOut_c_0 = _io_segOut_c_T;
	assign _io_segOut_d_T = segOut[3];
	assign io_segOut_d_0 = _io_segOut_d_T;
	assign _io_segOut_e_T = segOut[2];
	assign io_segOut_e_0 = _io_segOut_e_T;
	assign _io_segOut_f_T = segOut[1];
	assign io_segOut_f_0 = _io_segOut_f_T;
	assign _io_segOut_g_T = segOut[0];
	assign io_segOut_g_0 = _io_segOut_g_T;
	assign segOut = _GEN[binIn * 7+:7];
	assign io_segOut_a = io_segOut_a_0;
	assign io_segOut_b = io_segOut_b_0;
	assign io_segOut_c = io_segOut_c_0;
	assign io_segOut_d = io_segOut_d_0;
	assign io_segOut_e = io_segOut_e_0;
	assign io_segOut_f = io_segOut_f_0;
	assign io_segOut_g = io_segOut_g_0;
endmodule
