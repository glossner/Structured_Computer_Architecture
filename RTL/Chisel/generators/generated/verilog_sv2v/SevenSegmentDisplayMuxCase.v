module SevenSegmentDisplayMuxCase (
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
	wire [6:0] _io_segOut_T_19;
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
	wire _io_segOut_T = io_binIn_0 == 4'h0;
	wire _io_segOut_T_1 = io_binIn_0 == 4'h1;
	wire _io_segOut_T_2 = io_binIn_0 == 4'h2;
	wire _io_segOut_T_3 = io_binIn_0 == 4'h3;
	wire _io_segOut_T_4 = io_binIn_0 == 4'h4;
	wire _io_segOut_T_5 = io_binIn_0 == 4'h5;
	wire _io_segOut_T_6 = io_binIn_0 == 4'h6;
	wire _io_segOut_T_7 = io_binIn_0 == 4'h7;
	wire _io_segOut_T_8 = io_binIn_0 == 4'h8;
	wire _io_segOut_T_9 = io_binIn_0 == 4'h9;
	wire [6:0] _io_segOut_T_10 = (_io_segOut_T_9 ? 7'h7b : 7'h00);
	wire [6:0] _io_segOut_T_11 = (_io_segOut_T_8 ? 7'h7f : _io_segOut_T_10);
	wire [6:0] _io_segOut_T_12 = (_io_segOut_T_7 ? 7'h70 : _io_segOut_T_11);
	wire [6:0] _io_segOut_T_13 = (_io_segOut_T_6 ? 7'h5f : _io_segOut_T_12);
	wire [6:0] _io_segOut_T_14 = (_io_segOut_T_5 ? 7'h5b : _io_segOut_T_13);
	wire [6:0] _io_segOut_T_15 = (_io_segOut_T_4 ? 7'h33 : _io_segOut_T_14);
	wire [6:0] _io_segOut_T_16 = (_io_segOut_T_3 ? 7'h79 : _io_segOut_T_15);
	wire [6:0] _io_segOut_T_17 = (_io_segOut_T_2 ? 7'h6d : _io_segOut_T_16);
	wire [6:0] _io_segOut_T_18 = (_io_segOut_T_1 ? 7'h30 : _io_segOut_T_17);
	assign _io_segOut_T_19 = (_io_segOut_T ? 7'h7e : _io_segOut_T_18);
	assign io_segOut_0 = _io_segOut_T_19;
	assign io_segOut = io_segOut_0;
endmodule
