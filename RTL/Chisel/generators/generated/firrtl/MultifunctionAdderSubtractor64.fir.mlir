module {
  hw.module @MultifunctionAdderSubtractor64(in %clock : i1, in %reset : i1, in %io_a : i64, in %io_b : i64, out io_result : i64, in %io_opcode : i4, out io_carryOut : i1) {
    %io_a_0 = sv.wire sym @sym name "io_a" {hw.verilogName = "io_a_0"} : !hw.inout<i64>
    sv.assign %io_a_0, %io_a : i64
    %io_b_1 = sv.wire sym @sym_0 name "io_b" {hw.verilogName = "io_b_0"} : !hw.inout<i64>
    sv.assign %io_b_1, %io_b : i64
    %io_opcode_2 = sv.wire sym @sym_2 name "io_opcode" {hw.verilogName = "io_opcode_0"} : !hw.inout<i4>
    sv.assign %io_opcode_2, %io_opcode : i4
    %0 = hw.aggregate_constant [-64 : i7, 32 : i7, 16 : i7, 8 : i7] : !hw.array<4xi7>
    %1 = sv.wire {hw.verilogName = "_GEN"} : !hw.inout<array<4xi7>>
    sv.assign %1, %0 : !hw.array<4xi7>
    %c1_i129 = hw.constant 1 : i129
    %c1_i128 = hw.constant 1 : i128
    %c-1_i128 = hw.constant -1 : i128
    %c0_i64 = hw.constant 0 : i64
    %c0_i121 = hw.constant 0 : i121
    %false = hw.constant false
    %true = hw.constant true
    %_io_carryOut_T_3 = sv.wire sym @sym_33 {hw.verilogName = "_io_carryOut_T_3"} : !hw.inout<i1>
    %2 = sv.read_inout %io_opcode_2 : !hw.inout<i4>
    %3 = comb.extract %2 from 3 : (i4) -> i1
    %isSub = sv.wire sym @sym_4 {hw.verilogName = "isSub"} : !hw.inout<i1>
    sv.assign %isSub, %3 : i1
    %4 = sv.read_inout %isSub : !hw.inout<i1>
    %5 = comb.xor bin %4, %true : i1
    %isAdd = sv.wire sym @sym_5 {hw.verilogName = "isAdd"} : !hw.inout<i1>
    sv.assign %isAdd, %5 : i1
    %6 = sv.read_inout %io_opcode_2 : !hw.inout<i4>
    %7 = comb.extract %6 from 2 : (i4) -> i1
    %isSigned = sv.wire sym @sym_6 {hw.verilogName = "isSigned"} : !hw.inout<i1>
    sv.assign %isSigned, %7 : i1
    %8 = sv.read_inout %io_opcode_2 : !hw.inout<i4>
    %9 = comb.extract %8 from 0 : (i4) -> i2
    %operandSize = sv.wire sym @sym_7 {hw.verilogName = "operandSize"} : !hw.inout<i2>
    sv.assign %operandSize, %9 : i2
    %10 = sv.read_inout %operandSize : !hw.inout<i2>
    %11 = sv.read_inout %1 : !hw.inout<array<4xi7>>
    %12 = hw.array_get %11[%10] : !hw.array<4xi7>, i2
    %width = sv.wire sym @sym_8 {hw.verilogName = "width"} : !hw.inout<i7>
    sv.assign %width, %12 : i7
    %13 = sv.read_inout %width : !hw.inout<i7>
    %14 = comb.concat %c0_i121, %13 : i121, i7
    %15 = comb.shl bin %c1_i128, %14 : i128
    %_mask_T = sv.wire sym @sym_9 {hw.verilogName = "_mask_T"} : !hw.inout<i128>
    sv.assign %_mask_T, %15 : i128
    %16 = sv.read_inout %_mask_T : !hw.inout<i128>
    %17 = comb.concat %false, %16 : i1, i128
    %c1_i129_3 = hw.constant 1 : i129
    %18 = comb.sub %17, %c1_i129_3 : i129
    %_mask_T_1 = sv.wire sym @sym_10 {hw.verilogName = "_mask_T_1"} : !hw.inout<i129>
    sv.assign %_mask_T_1, %18 : i129
    %19 = sv.read_inout %_mask_T_1 : !hw.inout<i129>
    %20 = comb.extract %19 from 0 : (i129) -> i128
    %mask = sv.wire sym @sym_11 {hw.verilogName = "mask"} : !hw.inout<i128>
    sv.assign %mask, %20 : i128
    %21 = sv.read_inout %mask : !hw.inout<i128>
    %22 = comb.extract %21 from 0 : (i128) -> i64
    %23 = sv.read_inout %io_a_0 : !hw.inout<i64>
    %24 = comb.and bin %22, %23 : i64
    %25 = comb.concat %c0_i64, %24 : i64, i64
    %aEffective = sv.wire sym @sym_12 {hw.verilogName = "aEffective"} : !hw.inout<i128>
    sv.assign %aEffective, %25 : i128
    %26 = sv.read_inout %aEffective : !hw.inout<i128>
    %_fullResult_T = sv.wire sym @sym_18 {hw.verilogName = "_fullResult_T"} : !hw.inout<i128>
    sv.assign %_fullResult_T, %26 : i128
    %27 = sv.read_inout %io_b_1 : !hw.inout<i64>
    %28 = comb.and bin %22, %27 : i64
    %29 = comb.concat %c0_i64, %28 : i64, i64
    %bEffective = sv.wire sym @sym_13 {hw.verilogName = "bEffective"} : !hw.inout<i128>
    sv.assign %bEffective, %29 : i128
    %30 = sv.read_inout %bEffective : !hw.inout<i128>
    %31 = comb.xor bin %30, %c-1_i128 : i128
    %_bAdjusted_T = sv.wire sym @sym_14 {hw.verilogName = "_bAdjusted_T"} : !hw.inout<i128>
    sv.assign %_bAdjusted_T, %31 : i128
    %32 = sv.read_inout %_bAdjusted_T : !hw.inout<i128>
    %33 = comb.concat %false, %32 : i1, i128
    %34 = comb.add bin %33, %c1_i129 : i129
    %_bAdjusted_T_1 = sv.wire sym @sym_15 {hw.verilogName = "_bAdjusted_T_1"} : !hw.inout<i129>
    sv.assign %_bAdjusted_T_1, %34 : i129
    %35 = sv.read_inout %_bAdjusted_T_1 : !hw.inout<i129>
    %36 = comb.extract %35 from 0 : (i129) -> i128
    %_bAdjusted_T_2 = sv.wire sym @sym_16 {hw.verilogName = "_bAdjusted_T_2"} : !hw.inout<i128>
    sv.assign %_bAdjusted_T_2, %36 : i128
    %37 = sv.read_inout %isSub : !hw.inout<i1>
    %38 = sv.read_inout %_bAdjusted_T_2 : !hw.inout<i128>
    %39 = sv.read_inout %bEffective : !hw.inout<i128>
    %40 = comb.mux bin %37, %38, %39 : i128
    %bAdjusted = sv.wire sym @sym_17 {hw.verilogName = "bAdjusted"} : !hw.inout<i128>
    sv.assign %bAdjusted, %40 : i128
    %41 = sv.read_inout %bAdjusted : !hw.inout<i128>
    %_fullResult_T_1 = sv.wire sym @sym_19 {hw.verilogName = "_fullResult_T_1"} : !hw.inout<i128>
    sv.assign %_fullResult_T_1, %41 : i128
    %42 = sv.read_inout %_fullResult_T : !hw.inout<i128>
    %43 = comb.extract %42 from 127 : (i128) -> i1
    %44 = sv.read_inout %_fullResult_T : !hw.inout<i128>
    %45 = comb.concat %43, %44 : i1, i128
    %46 = sv.read_inout %_fullResult_T_1 : !hw.inout<i128>
    %47 = comb.extract %46 from 127 : (i128) -> i1
    %48 = sv.read_inout %_fullResult_T_1 : !hw.inout<i128>
    %49 = comb.concat %47, %48 : i1, i128
    %50 = comb.add bin %45, %49 : i129
    %_fullResult_T_2 = sv.wire sym @sym_20 {hw.verilogName = "_fullResult_T_2"} : !hw.inout<i129>
    sv.assign %_fullResult_T_2, %50 : i129
    %51 = sv.read_inout %_fullResult_T_2 : !hw.inout<i129>
    %_fullResult_T_3 = sv.wire sym @sym_21 {hw.verilogName = "_fullResult_T_3"} : !hw.inout<i129>
    sv.assign %_fullResult_T_3, %51 : i129
    %52 = sv.read_inout %aEffective : !hw.inout<i128>
    %53 = comb.concat %false, %52 : i1, i128
    %54 = sv.read_inout %bAdjusted : !hw.inout<i128>
    %55 = comb.concat %false, %54 : i1, i128
    %56 = comb.add bin %53, %55 : i129
    %_fullResult_T_4 = sv.wire sym @sym_22 {hw.verilogName = "_fullResult_T_4"} : !hw.inout<i129>
    sv.assign %_fullResult_T_4, %56 : i129
    %57 = sv.read_inout %_fullResult_T_3 : !hw.inout<i129>
    %58 = sv.read_inout %_fullResult_T_4 : !hw.inout<i129>
    %59 = sv.read_inout %isSigned : !hw.inout<i1>
    %60 = comb.mux bin %59, %57, %58 : i129
    %fullResult = sv.wire sym @sym_23 {hw.verilogName = "fullResult"} : !hw.inout<i129>
    sv.assign %fullResult, %60 : i129
    %61 = sv.read_inout %fullResult : !hw.inout<i129>
    %62 = comb.extract %61 from 0 : (i129) -> i128
    %63 = sv.read_inout %mask : !hw.inout<i128>
    %64 = comb.and bin %62, %63 : i128
    %65 = comb.concat %false, %64 : i1, i128
    %truncatedResult = sv.wire sym @sym_24 {hw.verilogName = "truncatedResult"} : !hw.inout<i129>
    sv.assign %truncatedResult, %65 : i129
    %66 = sv.read_inout %truncatedResult : !hw.inout<i129>
    %_extendedResult_T = sv.wire sym @sym_25 {hw.verilogName = "_extendedResult_T"} : !hw.inout<i129>
    sv.assign %_extendedResult_T, %66 : i129
    %67 = sv.read_inout %truncatedResult : !hw.inout<i129>
    %_extendedResult_T_3 = sv.wire sym @sym_28 {hw.verilogName = "_extendedResult_T_3"} : !hw.inout<i129>
    sv.assign %_extendedResult_T_3, %67 : i129
    %68 = sv.read_inout %_extendedResult_T : !hw.inout<i129>
    %_extendedResult_T_1 = sv.wire sym @sym_26 {hw.verilogName = "_extendedResult_T_1"} : !hw.inout<i129>
    sv.assign %_extendedResult_T_1, %68 : i129
    %69 = sv.read_inout %_extendedResult_T_1 : !hw.inout<i129>
    %_extendedResult_T_2 = sv.wire sym @sym_27 {hw.verilogName = "_extendedResult_T_2"} : !hw.inout<i129>
    sv.assign %_extendedResult_T_2, %69 : i129
    %70 = sv.read_inout %_extendedResult_T_3 : !hw.inout<i129>
    %71 = sv.read_inout %_extendedResult_T_2 : !hw.inout<i129>
    %72 = sv.read_inout %isSigned : !hw.inout<i1>
    %73 = comb.mux bin %72, %71, %70 : i129
    %extendedResult = sv.wire sym @sym_29 {hw.verilogName = "extendedResult"} : !hw.inout<i129>
    sv.assign %extendedResult, %73 : i129
    %74 = sv.read_inout %truncatedResult : !hw.inout<i129>
    %75 = comb.extract %74 from 0 : (i129) -> i64
    %io_result = sv.wire sym @sym_1 {hw.verilogName = "io_result_0"} : !hw.inout<i64>
    sv.assign %io_result, %75 : i64
    %76 = sv.read_inout %mask : !hw.inout<i128>
    %77 = comb.concat %false, %76 : i1, i128
    %78 = sv.read_inout %fullResult : !hw.inout<i129>
    %79 = comb.icmp bin ugt %78, %77 : i129
    %_io_carryOut_T = sv.wire sym @sym_30 {hw.verilogName = "_io_carryOut_T"} : !hw.inout<i1>
    sv.assign %_io_carryOut_T, %79 : i1
    %80 = sv.read_inout %aEffective : !hw.inout<i128>
    %81 = sv.read_inout %bEffective : !hw.inout<i128>
    %82 = comb.icmp bin ult %80, %81 : i128
    %_io_carryOut_T_1 = sv.wire sym @sym_31 {hw.verilogName = "_io_carryOut_T_1"} : !hw.inout<i1>
    sv.assign %_io_carryOut_T_1, %82 : i1
    %83 = sv.read_inout %isAdd : !hw.inout<i1>
    %84 = sv.read_inout %_io_carryOut_T : !hw.inout<i1>
    %85 = sv.read_inout %_io_carryOut_T_1 : !hw.inout<i1>
    %86 = comb.mux bin %83, %84, %85 : i1
    %_io_carryOut_T_2 = sv.wire sym @sym_32 {hw.verilogName = "_io_carryOut_T_2"} : !hw.inout<i1>
    sv.assign %_io_carryOut_T_2, %86 : i1
    %87 = sv.read_inout %isSigned : !hw.inout<i1>
    %88 = comb.xor %87, %true : i1
    %89 = sv.read_inout %_io_carryOut_T_2 : !hw.inout<i1>
    %90 = comb.and %88, %89 : i1
    sv.assign %_io_carryOut_T_3, %90 : i1
    %91 = sv.read_inout %_io_carryOut_T_3 : !hw.inout<i1>
    %io_carryOut = sv.wire sym @sym_3 {hw.verilogName = "io_carryOut_0"} : !hw.inout<i1>
    sv.assign %io_carryOut, %91 : i1
    %92 = sv.read_inout %io_result : !hw.inout<i64>
    %93 = sv.read_inout %io_carryOut : !hw.inout<i1>
    hw.output %92, %93 : i64, i1
  }
  om.class @MultifunctionAdderSubtractor64_Class(%basepath: !om.frozenbasepath) {
    om.class.fields
  }
}
