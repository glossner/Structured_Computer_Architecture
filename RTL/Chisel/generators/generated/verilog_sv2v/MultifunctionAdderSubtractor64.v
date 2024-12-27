module MultifunctionAdderSubtractor64 (
	clock,
	reset,
	io_a,
	io_b,
	io_result,
	io_opcode,
	io_carryOut
);
	input clock;
	input reset;
	input [63:0] io_a;
	input [63:0] io_b;
	output wire [63:0] io_result;
	input [3:0] io_opcode;
	output wire io_carryOut;
	wire [63:0] io_a_0 = io_a;
	wire [63:0] io_b_0 = io_b;
	wire [3:0] io_opcode_0 = io_opcode;
	wire [27:0] _GEN = 28'h8080808;
	wire _io_carryOut_T_3;
	wire isSub = io_opcode_0[3];
	wire isAdd = ~isSub;
	wire isSigned = io_opcode_0[2];
	wire [1:0] operandSize = io_opcode_0[1:0];
	wire [6:0] width = _GEN[operandSize * 7+:7];
	wire [127:0] _mask_T = 128'h00000000000000000000000000000001 << width;
	wire [128:0] _mask_T_1 = {1'h0, _mask_T} - 129'h000000000000000000000000000000001;
	wire [127:0] mask = _mask_T_1[127:0];
	wire [127:0] aEffective = {64'h0000000000000000, mask[63:0] & io_a_0};
	wire [127:0] _fullResult_T = aEffective;
	wire [127:0] bEffective = {64'h0000000000000000, mask[63:0] & io_b_0};
	wire [127:0] _bAdjusted_T = ~bEffective;
	wire [128:0] _bAdjusted_T_1 = {1'h0, _bAdjusted_T} + 129'h000000000000000000000000000000001;
	wire [127:0] _bAdjusted_T_2 = _bAdjusted_T_1[127:0];
	wire [127:0] bAdjusted = (isSub ? _bAdjusted_T_2 : bEffective);
	wire [127:0] _fullResult_T_1 = bAdjusted;
	wire [128:0] _fullResult_T_2 = {_fullResult_T[127], _fullResult_T} + {_fullResult_T_1[127], _fullResult_T_1};
	wire [128:0] _fullResult_T_3 = _fullResult_T_2;
	wire [128:0] _fullResult_T_4 = {1'h0, aEffective} + {1'h0, bAdjusted};
	wire [128:0] fullResult = (isSigned ? _fullResult_T_3 : _fullResult_T_4);
	wire [128:0] truncatedResult = {1'h0, fullResult[127:0] & mask};
	wire [128:0] _extendedResult_T = truncatedResult;
	wire [128:0] _extendedResult_T_3 = truncatedResult;
	wire [128:0] _extendedResult_T_1 = _extendedResult_T;
	wire [128:0] _extendedResult_T_2 = _extendedResult_T_1;
	wire [128:0] extendedResult = (isSigned ? _extendedResult_T_2 : _extendedResult_T_3);
	wire [63:0] io_result_0 = truncatedResult[63:0];
	wire _io_carryOut_T = fullResult > {1'h0, mask};
	wire _io_carryOut_T_1 = aEffective < bEffective;
	wire _io_carryOut_T_2 = (isAdd ? _io_carryOut_T : _io_carryOut_T_1);
	assign _io_carryOut_T_3 = ~isSigned & _io_carryOut_T_2;
	wire io_carryOut_0 = _io_carryOut_T_3;
	assign io_result = io_result_0;
	assign io_carryOut = io_carryOut_0;
endmodule
