module MultifunctionAdderSubtractor64 (
	io_a,
	io_b,
	io_opcode,
	io_result,
	io_carryOut,
	io_overflowFlag,
	io_zeroFlag,
	io_negativeFlag
);
	input [63:0] io_a;
	input [63:0] io_b;
	input [3:0] io_opcode;
	output wire [63:0] io_result;
	output wire io_carryOut;
	output wire io_overflowFlag;
	output wire io_zeroFlag;
	output wire io_negativeFlag;
	wire [63:0] io_a_0 = io_a;
	wire [63:0] io_b_0 = io_b;
	wire [3:0] io_opcode_0 = io_opcode;
	wire [6:0] _width_T_4 = 7'h40;
	wire [63:0] _mask_T_4 = 64'hffffffffffffffff;
	wire io_carryIn = 1'h0;
	wire _io_carryOut_T_6;
	wire isOverflow;
	wire isZero;
	wire isNegative;
	wire _isSub_T = io_opcode_0[3];
	wire isSub = _isSub_T;
	wire isSigned = io_opcode_0[2];
	wire [1:0] operandSize = io_opcode_0[1:0];
	wire _GEN = operandSize == 2'h0;
	wire _width_T;
	assign _width_T = _GEN;
	wire _mask_T;
	assign _mask_T = _GEN;
	wire _GEN_0 = operandSize == 2'h1;
	wire _width_T_1;
	assign _width_T_1 = _GEN_0;
	wire _mask_T_1;
	assign _mask_T_1 = _GEN_0;
	wire _GEN_1 = operandSize == 2'h2;
	wire _width_T_2;
	assign _width_T_2 = _GEN_1;
	wire _mask_T_2;
	assign _mask_T_2 = _GEN_1;
	wire _width_T_3 = &operandSize;
	wire [6:0] _width_T_5 = (_width_T_2 ? 7'h20 : 7'h40);
	wire [6:0] _width_T_6 = (_width_T_1 ? 7'h10 : _width_T_5);
	wire [6:0] width = (_width_T ? 7'h08 : _width_T_6);
	wire _mask_T_3 = &operandSize;
	wire [63:0] _mask_T_5 = (_mask_T_2 ? 64'h00000000ffffffff : 64'hffffffffffffffff);
	wire [63:0] _mask_T_6 = (_mask_T_1 ? 64'h000000000000ffff : _mask_T_5);
	wire [63:0] mask = (_mask_T ? 64'h00000000000000ff : _mask_T_6);
	wire [63:0] aEffective = io_a_0 & mask;
	wire [63:0] _fullArithmeticResult_T = aEffective;
	wire [63:0] bEffective = io_b_0 & mask;
	wire [63:0] _bAdjusted_T = ~bEffective;
	wire [64:0] _bAdjusted_T_1 = {1'h0, _bAdjusted_T} + 65'h00000000000000001;
	wire [63:0] _bAdjusted_T_2 = _bAdjusted_T_1[63:0];
	wire [63:0] bAdjusted = (isSub ? _bAdjusted_T_2 : bEffective);
	wire [63:0] _fullArithmeticResult_T_1 = bAdjusted;
	wire [64:0] _fullArithmeticResult_T_2 = {_fullArithmeticResult_T[63], _fullArithmeticResult_T} + {_fullArithmeticResult_T_1[63], _fullArithmeticResult_T_1};
	wire [64:0] _fullArithmeticResult_T_3 = _fullArithmeticResult_T_2;
	wire [64:0] _fullArithmeticResult_T_4 = {1'h0, aEffective} + {1'h0, bAdjusted};
	wire [64:0] fullArithmeticResult = (isSigned ? _fullArithmeticResult_T_3 : _fullArithmeticResult_T_4);
	wire [64:0] truncatedResult = {1'h0, fullArithmeticResult[63:0] & mask};
	wire [63:0] io_result_0 = truncatedResult[63:0];
	wire _GEN_2 = width == 7'h08;
	wire _isCarry_T;
	assign _isCarry_T = _GEN_2;
	wire _aSign_T;
	assign _aSign_T = _GEN_2;
	wire _bSign_T;
	assign _bSign_T = _GEN_2;
	wire _sumSign_T;
	assign _sumSign_T = _GEN_2;
	wire _isCarry_T_1 = fullArithmeticResult[8];
	wire _GEN_3 = width == 7'h10;
	wire _isCarry_T_2;
	assign _isCarry_T_2 = _GEN_3;
	wire _aSign_T_2;
	assign _aSign_T_2 = _GEN_3;
	wire _bSign_T_2;
	assign _bSign_T_2 = _GEN_3;
	wire _sumSign_T_2;
	assign _sumSign_T_2 = _GEN_3;
	wire _isCarry_T_3 = fullArithmeticResult[16];
	wire _GEN_4 = width == 7'h20;
	wire _isCarry_T_4;
	assign _isCarry_T_4 = _GEN_4;
	wire _aSign_T_4;
	assign _aSign_T_4 = _GEN_4;
	wire _bSign_T_4;
	assign _bSign_T_4 = _GEN_4;
	wire _sumSign_T_4;
	assign _sumSign_T_4 = _GEN_4;
	wire _isCarry_T_5 = fullArithmeticResult[32];
	wire _GEN_5 = width == 7'h40;
	wire _isCarry_T_6;
	assign _isCarry_T_6 = _GEN_5;
	wire _aSign_T_6;
	assign _aSign_T_6 = _GEN_5;
	wire _bSign_T_6;
	assign _bSign_T_6 = _GEN_5;
	wire _sumSign_T_6;
	assign _sumSign_T_6 = _GEN_5;
	wire _isCarry_T_7 = fullArithmeticResult[64];
	wire _isCarry_T_8 = _isCarry_T_6 & _isCarry_T_7;
	wire _isCarry_T_9 = (_isCarry_T_4 ? _isCarry_T_5 : _isCarry_T_8);
	wire _isCarry_T_10 = (_isCarry_T_2 ? _isCarry_T_3 : _isCarry_T_9);
	wire isCarry = (_isCarry_T ? _isCarry_T_1 : _isCarry_T_10);
	wire _isBorrow_T = ~isSigned;
	wire _isBorrow_T_1 = isSub & _isBorrow_T;
	wire _isBorrow_T_2 = aEffective < bEffective;
	wire isBorrow = _isBorrow_T_1 & _isBorrow_T_2;
	wire _io_carryOut_T = ~isSigned;
	wire _io_carryOut_T_1 = ~isSub;
	wire _io_carryOut_T_2 = _io_carryOut_T & _io_carryOut_T_1;
	wire _io_carryOut_T_3 = ~isSigned;
	wire _io_carryOut_T_4 = _io_carryOut_T_3 & isSub;
	wire _io_carryOut_T_5 = _io_carryOut_T_4 & isBorrow;
	assign _io_carryOut_T_6 = (_io_carryOut_T_2 ? isCarry : _io_carryOut_T_5);
	wire io_carryOut_0 = _io_carryOut_T_6;
	wire _aSign_T_11;
	wire _bSign_T_11;
	wire _sumSign_T_11;
	wire _aSign_T_1 = aEffective[7];
	wire _aSign_T_3 = aEffective[15];
	wire _aSign_T_5 = aEffective[31];
	wire _aSign_T_7 = aEffective[63];
	wire _aSign_T_8 = _aSign_T_6 & _aSign_T_7;
	wire _aSign_T_9 = (_aSign_T_4 ? _aSign_T_5 : _aSign_T_8);
	wire _aSign_T_10 = (_aSign_T_2 ? _aSign_T_3 : _aSign_T_9);
	assign _aSign_T_11 = (_aSign_T ? _aSign_T_1 : _aSign_T_10);
	wire aSign = _aSign_T_11;
	wire _bSign_T_1 = bAdjusted[7];
	wire _bSign_T_3 = bAdjusted[15];
	wire _bSign_T_5 = bAdjusted[31];
	wire _bSign_T_7 = bAdjusted[63];
	wire _bSign_T_8 = _bSign_T_6 & _bSign_T_7;
	wire _bSign_T_9 = (_bSign_T_4 ? _bSign_T_5 : _bSign_T_8);
	wire _bSign_T_10 = (_bSign_T_2 ? _bSign_T_3 : _bSign_T_9);
	assign _bSign_T_11 = (_bSign_T ? _bSign_T_1 : _bSign_T_10);
	wire bSign = _bSign_T_11;
	wire _sumSign_T_1 = fullArithmeticResult[7];
	wire _sumSign_T_3 = fullArithmeticResult[15];
	wire _sumSign_T_5 = fullArithmeticResult[31];
	wire _sumSign_T_7 = fullArithmeticResult[63];
	wire _sumSign_T_8 = _sumSign_T_6 & _sumSign_T_7;
	wire _sumSign_T_9 = (_sumSign_T_4 ? _sumSign_T_5 : _sumSign_T_8);
	wire _sumSign_T_10 = (_sumSign_T_2 ? _sumSign_T_3 : _sumSign_T_9);
	assign _sumSign_T_11 = (_sumSign_T ? _sumSign_T_1 : _sumSign_T_10);
	wire sumSign = _sumSign_T_11;
	wire _isOverflow_T = aSign == bSign;
	wire _isOverflow_T_1 = aSign != sumSign;
	assign isOverflow = _isOverflow_T & _isOverflow_T_1;
	wire io_overflowFlag_0 = isOverflow;
	wire _isZero_T = |truncatedResult;
	assign isZero = ~_isZero_T;
	wire io_zeroFlag_0 = isZero;
	assign isNegative = isSigned & sumSign;
	wire io_negativeFlag_0 = isNegative;
	assign io_result = io_result_0;
	assign io_carryOut = io_carryOut_0;
	assign io_overflowFlag = io_overflowFlag_0;
	assign io_zeroFlag = io_zeroFlag_0;
	assign io_negativeFlag = io_negativeFlag_0;
endmodule
module ALU64 (
	clock,
	reset,
	io_a,
	io_b,
	io_result,
	io_opcode,
	io_carryOutFlag,
	io_overflowFlag,
	io_zeroFlag,
	io_negativeFlag
);
	input clock;
	input reset;
	input [63:0] io_a;
	input [63:0] io_b;
	output wire [63:0] io_result;
	input [5:0] io_opcode;
	output wire io_carryOutFlag;
	output wire io_overflowFlag;
	output wire io_zeroFlag;
	output wire io_negativeFlag;
	wire [63:0] _adderSubtractor_io_result;
	wire _adderSubtractor_io_carryOut;
	wire _adderSubtractor_io_overflowFlag;
	wire _adderSubtractor_io_zeroFlag;
	wire _adderSubtractor_io_negativeFlag;
	wire [63:0] io_a_0 = io_a;
	wire [63:0] io_b_0 = io_b;
	wire [5:0] io_opcode_0 = io_opcode;
	wire [255:0] _GEN = 256'hffffffffffffffff00000000ffffffff000000000000ffff00000000000000ff;
	wire [27:0] _GEN_0 = 28'h8080808;
	wire _io_carryOutFlag_T;
	wire _io_overflowFlag_T;
	wire _io_zeroFlag_T;
	wire _io_negativeFlag_T;
	wire _isArithmetic_T = io_opcode_0[5];
	wire _isLogical_T = io_opcode_0[5];
	wire isArithmetic = ~_isArithmetic_T;
	wire isLogical = _isLogical_T;
	wire _isSub_T = io_opcode_0[3];
	wire _isSub_T_1 = _isSub_T;
	wire isSub = _isSub_T_1 & isArithmetic;
	wire _isSigned_T = io_opcode_0[2];
	wire isSigned = _isSigned_T & isArithmetic;
	wire [1:0] operandSize = io_opcode_0[1:0];
	wire [6:0] width = _GEN_0[operandSize * 7+:7];
	wire [63:0] mask = _GEN[operandSize * 64+:64];
	wire [63:0] aEffective = io_a_0 & mask;
	wire [63:0] _logicalResult_T_27 = aEffective;
	wire [63:0] bEffective = io_b_0 & mask;
	wire [63:0] _bAdjusted_T = ~bEffective;
	wire [64:0] _bAdjusted_T_1 = {1'h0, _bAdjusted_T} + 65'h00000000000000001;
	wire [63:0] _bAdjusted_T_2 = _bAdjusted_T_1[63:0];
	wire [63:0] bAdjusted = (isSub ? _bAdjusted_T_2 : bEffective);
	wire [3:0] _adderSubtractor_io_opcode_T = io_opcode_0[3:0];
	wire [2:0] _logicalResult_T = io_opcode_0[4:2];
	wire [2:0] _logicalResult_T_3 = io_opcode_0[4:2];
	wire [2:0] _logicalResult_T_6 = io_opcode_0[4:2];
	wire [2:0] _logicalResult_T_9 = io_opcode_0[4:2];
	wire [2:0] _logicalResult_T_17 = io_opcode_0[4:2];
	wire [2:0] _logicalResult_T_25 = io_opcode_0[4:2];
	wire _logicalResult_T_1 = _logicalResult_T == 3'h0;
	wire [63:0] _logicalResult_T_2 = aEffective & bEffective;
	wire _logicalResult_T_4 = _logicalResult_T_3 == 3'h1;
	wire [63:0] _logicalResult_T_5 = aEffective | bEffective;
	wire _logicalResult_T_7 = _logicalResult_T_6 == 3'h2;
	wire [63:0] _logicalResult_T_8 = aEffective ^ bEffective;
	wire _logicalResult_T_10 = _logicalResult_T_9 == 3'h3;
	wire [5:0] _logicalResult_T_11 = bEffective[5:0];
	wire [5:0] _logicalResult_T_19 = bEffective[5:0];
	wire [5:0] _logicalResult_T_28 = bEffective[5:0];
	wire [7:0] _GEN_1 = {1'h0, width} - 8'h01;
	wire [7:0] _logicalResult_T_12;
	assign _logicalResult_T_12 = _GEN_1;
	wire [7:0] _logicalResult_T_20;
	assign _logicalResult_T_20 = _GEN_1;
	wire [7:0] _logicalResult_T_29;
	assign _logicalResult_T_29 = _GEN_1;
	wire [6:0] _logicalResult_T_13 = _logicalResult_T_12[6:0];
	wire [6:0] _logicalResult_T_14 = {1'h0, _logicalResult_T_13[5:0] & _logicalResult_T_11};
	wire [190:0] _logicalResult_T_15 = {127'h00000000000000000000000000000000, aEffective} << _logicalResult_T_14;
	wire [190:0] _logicalResult_T_16 = {127'h00000000000000000000000000000000, _logicalResult_T_15[63:0] & mask};
	wire _logicalResult_T_18 = _logicalResult_T_17 == 3'h4;
	wire [6:0] _logicalResult_T_21 = _logicalResult_T_20[6:0];
	wire [6:0] _logicalResult_T_22 = {1'h0, _logicalResult_T_21[5:0] & _logicalResult_T_19};
	wire [63:0] _logicalResult_T_23 = aEffective >> _logicalResult_T_22;
	wire [63:0] _logicalResult_T_24 = _logicalResult_T_23 & mask;
	wire _logicalResult_T_26 = _logicalResult_T_25 == 3'h5;
	wire [6:0] _logicalResult_T_30 = _logicalResult_T_29[6:0];
	wire [6:0] _logicalResult_T_31 = {1'h0, _logicalResult_T_30[5:0] & _logicalResult_T_28};
	wire [63:0] _logicalResult_T_32 = $signed($signed(_logicalResult_T_27) >>> _logicalResult_T_31);
	wire [63:0] _logicalResult_T_33 = _logicalResult_T_32;
	wire [63:0] _logicalResult_T_34 = _logicalResult_T_33 & mask;
	wire [63:0] _logicalResult_T_35 = (_logicalResult_T_26 ? _logicalResult_T_34 : 64'h0000000000000000);
	wire [63:0] _logicalResult_T_36 = (_logicalResult_T_18 ? _logicalResult_T_24 : _logicalResult_T_35);
	wire [190:0] _logicalResult_T_37 = (_logicalResult_T_10 ? _logicalResult_T_16 : {127'h00000000000000000000000000000000, _logicalResult_T_36});
	wire [190:0] _logicalResult_T_38 = (_logicalResult_T_7 ? {127'h00000000000000000000000000000000, _logicalResult_T_8} : _logicalResult_T_37);
	wire [190:0] _logicalResult_T_39 = (_logicalResult_T_4 ? {127'h00000000000000000000000000000000, _logicalResult_T_5} : _logicalResult_T_38);
	wire [190:0] logicalResult = (_logicalResult_T_1 ? {127'h00000000000000000000000000000000, _logicalResult_T_2} : _logicalResult_T_39);
	wire [190:0] _io_result_T = (isArithmetic ? {127'h00000000000000000000000000000000, _adderSubtractor_io_result} : logicalResult);
	wire [63:0] io_result_0 = _io_result_T[63:0];
	assign _io_carryOutFlag_T = isArithmetic & _adderSubtractor_io_carryOut;
	wire io_carryOutFlag_0 = _io_carryOutFlag_T;
	assign _io_overflowFlag_T = isArithmetic & _adderSubtractor_io_overflowFlag;
	wire io_overflowFlag_0 = _io_overflowFlag_T;
	assign _io_zeroFlag_T = isArithmetic & _adderSubtractor_io_zeroFlag;
	wire io_zeroFlag_0 = _io_zeroFlag_T;
	assign _io_negativeFlag_T = isArithmetic & _adderSubtractor_io_negativeFlag;
	wire io_negativeFlag_0 = _io_negativeFlag_T;
	MultifunctionAdderSubtractor64 adderSubtractor(
		.io_a(aEffective),
		.io_b(bEffective),
		.io_opcode(_adderSubtractor_io_opcode_T),
		.io_result(_adderSubtractor_io_result),
		.io_carryOut(_adderSubtractor_io_carryOut),
		.io_overflowFlag(_adderSubtractor_io_overflowFlag),
		.io_zeroFlag(_adderSubtractor_io_zeroFlag),
		.io_negativeFlag(_adderSubtractor_io_negativeFlag)
	);
	assign io_result = io_result_0;
	assign io_carryOutFlag = io_carryOutFlag_0;
	assign io_overflowFlag = io_overflowFlag_0;
	assign io_zeroFlag = io_zeroFlag_0;
	assign io_negativeFlag = io_negativeFlag_0;
endmodule
