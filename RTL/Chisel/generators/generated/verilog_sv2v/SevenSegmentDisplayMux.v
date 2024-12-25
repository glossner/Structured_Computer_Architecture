module SevenSegmentDisplayMux (
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
	wire [6:0] io_segOut_0;
	wire B0 = io_binIn_0[0];
	wire B1 = io_binIn_0[1];
	wire B2 = io_binIn_0[2];
	wire B3 = io_binIn_0[3];
	wire a = io_segOut_0[6];
	wire b = io_segOut_0[5];
	wire c = io_segOut_0[4];
	wire d = io_segOut_0[3];
	wire e = io_segOut_0[2];
	wire f = io_segOut_0[1];
	wire g = io_segOut_0[0];
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
