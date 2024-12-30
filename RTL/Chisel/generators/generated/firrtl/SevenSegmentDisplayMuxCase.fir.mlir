module {
  hw.module @SevenSegmentDisplayMuxCase(in %clock : i1, in %reset : i1, in %io_binIn : i4, out io_segOut : i7) {
    %io_binIn_0 = sv.wire sym @sym name "io_binIn" {hw.verilogName = "io_binIn_0"} : !hw.inout<i4>
    sv.assign %io_binIn_0, %io_binIn : i4
    %c7_i4 = hw.constant 7 : i4
    %c6_i4 = hw.constant 6 : i4
    %c5_i4 = hw.constant 5 : i4
    %c4_i4 = hw.constant 4 : i4
    %c3_i4 = hw.constant 3 : i4
    %c2_i4 = hw.constant 2 : i4
    %c1_i4 = hw.constant 1 : i4
    %c0_i4 = hw.constant 0 : i4
    %c-8_i4 = hw.constant -8 : i4
    %c-7_i4 = hw.constant -7 : i4
    %c-5_i7 = hw.constant -5 : i7
    %c0_i7 = hw.constant 0 : i7
    %c-1_i7 = hw.constant -1 : i7
    %c-16_i7 = hw.constant -16 : i7
    %c-33_i7 = hw.constant -33 : i7
    %c-37_i7 = hw.constant -37 : i7
    %c51_i7 = hw.constant 51 : i7
    %c-7_i7 = hw.constant -7 : i7
    %c-19_i7 = hw.constant -19 : i7
    %c48_i7 = hw.constant 48 : i7
    %c-2_i7 = hw.constant -2 : i7
    %_io_segOut_T_19 = sv.wire sym @sym_42 {hw.verilogName = "_io_segOut_T_19"} : !hw.inout<i7>
    %0 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %1 = comb.extract %0 from 0 : (i4) -> i1
    %_B0_T = sv.wire sym @sym_1 {hw.verilogName = "_B0_T"} : !hw.inout<i1>
    sv.assign %_B0_T, %1 : i1
    %2 = sv.read_inout %_B0_T : !hw.inout<i1>
    %B0 = sv.wire sym @sym_2 {hw.verilogName = "B0"} : !hw.inout<i1>
    sv.assign %B0, %2 : i1
    %3 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %4 = comb.extract %3 from 1 : (i4) -> i1
    %_B1_T = sv.wire sym @sym_3 {hw.verilogName = "_B1_T"} : !hw.inout<i1>
    sv.assign %_B1_T, %4 : i1
    %5 = sv.read_inout %_B1_T : !hw.inout<i1>
    %B1 = sv.wire sym @sym_4 {hw.verilogName = "B1"} : !hw.inout<i1>
    sv.assign %B1, %5 : i1
    %6 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %7 = comb.extract %6 from 2 : (i4) -> i1
    %_B2_T = sv.wire sym @sym_5 {hw.verilogName = "_B2_T"} : !hw.inout<i1>
    sv.assign %_B2_T, %7 : i1
    %8 = sv.read_inout %_B2_T : !hw.inout<i1>
    %B2 = sv.wire sym @sym_6 {hw.verilogName = "B2"} : !hw.inout<i1>
    sv.assign %B2, %8 : i1
    %9 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %10 = comb.extract %9 from 3 : (i4) -> i1
    %_B3_T = sv.wire sym @sym_7 {hw.verilogName = "_B3_T"} : !hw.inout<i1>
    sv.assign %_B3_T, %10 : i1
    %11 = sv.read_inout %_B3_T : !hw.inout<i1>
    %B3 = sv.wire sym @sym_8 {hw.verilogName = "B3"} : !hw.inout<i1>
    sv.assign %B3, %11 : i1
    %io_segOut = sv.wire sym @sym_0 {hw.verilogName = "io_segOut_0"} : !hw.inout<i7>
    %12 = sv.read_inout %io_segOut : !hw.inout<i7>
    %13 = comb.extract %12 from 6 : (i7) -> i1
    %_a_T = sv.wire sym @sym_9 {hw.verilogName = "_a_T"} : !hw.inout<i1>
    sv.assign %_a_T, %13 : i1
    %14 = sv.read_inout %_a_T : !hw.inout<i1>
    %a = sv.wire sym @sym_10 {hw.verilogName = "a"} : !hw.inout<i1>
    sv.assign %a, %14 : i1
    %15 = sv.read_inout %io_segOut : !hw.inout<i7>
    %16 = comb.extract %15 from 5 : (i7) -> i1
    %_b_T = sv.wire sym @sym_11 {hw.verilogName = "_b_T"} : !hw.inout<i1>
    sv.assign %_b_T, %16 : i1
    %17 = sv.read_inout %_b_T : !hw.inout<i1>
    %b = sv.wire sym @sym_12 {hw.verilogName = "b"} : !hw.inout<i1>
    sv.assign %b, %17 : i1
    %18 = sv.read_inout %io_segOut : !hw.inout<i7>
    %19 = comb.extract %18 from 4 : (i7) -> i1
    %_c_T = sv.wire sym @sym_13 {hw.verilogName = "_c_T"} : !hw.inout<i1>
    sv.assign %_c_T, %19 : i1
    %20 = sv.read_inout %_c_T : !hw.inout<i1>
    %c = sv.wire sym @sym_14 {hw.verilogName = "c"} : !hw.inout<i1>
    sv.assign %c, %20 : i1
    %21 = sv.read_inout %io_segOut : !hw.inout<i7>
    %22 = comb.extract %21 from 3 : (i7) -> i1
    %_d_T = sv.wire sym @sym_15 {hw.verilogName = "_d_T"} : !hw.inout<i1>
    sv.assign %_d_T, %22 : i1
    %23 = sv.read_inout %_d_T : !hw.inout<i1>
    %d = sv.wire sym @sym_16 {hw.verilogName = "d"} : !hw.inout<i1>
    sv.assign %d, %23 : i1
    %24 = sv.read_inout %io_segOut : !hw.inout<i7>
    %25 = comb.extract %24 from 2 : (i7) -> i1
    %_e_T = sv.wire sym @sym_17 {hw.verilogName = "_e_T"} : !hw.inout<i1>
    sv.assign %_e_T, %25 : i1
    %26 = sv.read_inout %_e_T : !hw.inout<i1>
    %e = sv.wire sym @sym_18 {hw.verilogName = "e"} : !hw.inout<i1>
    sv.assign %e, %26 : i1
    %27 = sv.read_inout %io_segOut : !hw.inout<i7>
    %28 = comb.extract %27 from 1 : (i7) -> i1
    %_f_T = sv.wire sym @sym_19 {hw.verilogName = "_f_T"} : !hw.inout<i1>
    sv.assign %_f_T, %28 : i1
    %29 = sv.read_inout %_f_T : !hw.inout<i1>
    %f = sv.wire sym @sym_20 {hw.verilogName = "f"} : !hw.inout<i1>
    sv.assign %f, %29 : i1
    %30 = sv.read_inout %io_segOut : !hw.inout<i7>
    %31 = comb.extract %30 from 0 : (i7) -> i1
    %_g_T = sv.wire sym @sym_21 {hw.verilogName = "_g_T"} : !hw.inout<i1>
    sv.assign %_g_T, %31 : i1
    %32 = sv.read_inout %_g_T : !hw.inout<i1>
    %g = sv.wire sym @sym_22 {hw.verilogName = "g"} : !hw.inout<i1>
    sv.assign %g, %32 : i1
    %33 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %34 = comb.icmp bin eq %33, %c0_i4 : i4
    %_io_segOut_T = sv.wire sym @sym_23 {hw.verilogName = "_io_segOut_T"} : !hw.inout<i1>
    sv.assign %_io_segOut_T, %34 : i1
    %35 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %36 = comb.icmp bin eq %35, %c1_i4 : i4
    %_io_segOut_T_1 = sv.wire sym @sym_24 {hw.verilogName = "_io_segOut_T_1"} : !hw.inout<i1>
    sv.assign %_io_segOut_T_1, %36 : i1
    %37 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %38 = comb.icmp bin eq %37, %c2_i4 : i4
    %_io_segOut_T_2 = sv.wire sym @sym_25 {hw.verilogName = "_io_segOut_T_2"} : !hw.inout<i1>
    sv.assign %_io_segOut_T_2, %38 : i1
    %39 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %40 = comb.icmp bin eq %39, %c3_i4 : i4
    %_io_segOut_T_3 = sv.wire sym @sym_26 {hw.verilogName = "_io_segOut_T_3"} : !hw.inout<i1>
    sv.assign %_io_segOut_T_3, %40 : i1
    %41 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %42 = comb.icmp bin eq %41, %c4_i4 : i4
    %_io_segOut_T_4 = sv.wire sym @sym_27 {hw.verilogName = "_io_segOut_T_4"} : !hw.inout<i1>
    sv.assign %_io_segOut_T_4, %42 : i1
    %43 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %44 = comb.icmp bin eq %43, %c5_i4 : i4
    %_io_segOut_T_5 = sv.wire sym @sym_28 {hw.verilogName = "_io_segOut_T_5"} : !hw.inout<i1>
    sv.assign %_io_segOut_T_5, %44 : i1
    %45 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %46 = comb.icmp bin eq %45, %c6_i4 : i4
    %_io_segOut_T_6 = sv.wire sym @sym_29 {hw.verilogName = "_io_segOut_T_6"} : !hw.inout<i1>
    sv.assign %_io_segOut_T_6, %46 : i1
    %47 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %48 = comb.icmp bin eq %47, %c7_i4 : i4
    %_io_segOut_T_7 = sv.wire sym @sym_30 {hw.verilogName = "_io_segOut_T_7"} : !hw.inout<i1>
    sv.assign %_io_segOut_T_7, %48 : i1
    %49 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %50 = comb.icmp bin eq %49, %c-8_i4 : i4
    %_io_segOut_T_8 = sv.wire sym @sym_31 {hw.verilogName = "_io_segOut_T_8"} : !hw.inout<i1>
    sv.assign %_io_segOut_T_8, %50 : i1
    %51 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %52 = comb.icmp bin eq %51, %c-7_i4 : i4
    %_io_segOut_T_9 = sv.wire sym @sym_32 {hw.verilogName = "_io_segOut_T_9"} : !hw.inout<i1>
    sv.assign %_io_segOut_T_9, %52 : i1
    %53 = sv.read_inout %_io_segOut_T_9 : !hw.inout<i1>
    %54 = comb.mux bin %53, %c-5_i7, %c0_i7 : i7
    %_io_segOut_T_10 = sv.wire sym @sym_33 {hw.verilogName = "_io_segOut_T_10"} : !hw.inout<i7>
    sv.assign %_io_segOut_T_10, %54 : i7
    %55 = sv.read_inout %_io_segOut_T_8 : !hw.inout<i1>
    %56 = sv.read_inout %_io_segOut_T_10 : !hw.inout<i7>
    %57 = comb.mux bin %55, %c-1_i7, %56 : i7
    %_io_segOut_T_11 = sv.wire sym @sym_34 {hw.verilogName = "_io_segOut_T_11"} : !hw.inout<i7>
    sv.assign %_io_segOut_T_11, %57 : i7
    %58 = sv.read_inout %_io_segOut_T_7 : !hw.inout<i1>
    %59 = sv.read_inout %_io_segOut_T_11 : !hw.inout<i7>
    %60 = comb.mux bin %58, %c-16_i7, %59 : i7
    %_io_segOut_T_12 = sv.wire sym @sym_35 {hw.verilogName = "_io_segOut_T_12"} : !hw.inout<i7>
    sv.assign %_io_segOut_T_12, %60 : i7
    %61 = sv.read_inout %_io_segOut_T_6 : !hw.inout<i1>
    %62 = sv.read_inout %_io_segOut_T_12 : !hw.inout<i7>
    %63 = comb.mux bin %61, %c-33_i7, %62 : i7
    %_io_segOut_T_13 = sv.wire sym @sym_36 {hw.verilogName = "_io_segOut_T_13"} : !hw.inout<i7>
    sv.assign %_io_segOut_T_13, %63 : i7
    %64 = sv.read_inout %_io_segOut_T_5 : !hw.inout<i1>
    %65 = sv.read_inout %_io_segOut_T_13 : !hw.inout<i7>
    %66 = comb.mux bin %64, %c-37_i7, %65 : i7
    %_io_segOut_T_14 = sv.wire sym @sym_37 {hw.verilogName = "_io_segOut_T_14"} : !hw.inout<i7>
    sv.assign %_io_segOut_T_14, %66 : i7
    %67 = sv.read_inout %_io_segOut_T_4 : !hw.inout<i1>
    %68 = sv.read_inout %_io_segOut_T_14 : !hw.inout<i7>
    %69 = comb.mux bin %67, %c51_i7, %68 : i7
    %_io_segOut_T_15 = sv.wire sym @sym_38 {hw.verilogName = "_io_segOut_T_15"} : !hw.inout<i7>
    sv.assign %_io_segOut_T_15, %69 : i7
    %70 = sv.read_inout %_io_segOut_T_3 : !hw.inout<i1>
    %71 = sv.read_inout %_io_segOut_T_15 : !hw.inout<i7>
    %72 = comb.mux bin %70, %c-7_i7, %71 : i7
    %_io_segOut_T_16 = sv.wire sym @sym_39 {hw.verilogName = "_io_segOut_T_16"} : !hw.inout<i7>
    sv.assign %_io_segOut_T_16, %72 : i7
    %73 = sv.read_inout %_io_segOut_T_2 : !hw.inout<i1>
    %74 = sv.read_inout %_io_segOut_T_16 : !hw.inout<i7>
    %75 = comb.mux bin %73, %c-19_i7, %74 : i7
    %_io_segOut_T_17 = sv.wire sym @sym_40 {hw.verilogName = "_io_segOut_T_17"} : !hw.inout<i7>
    sv.assign %_io_segOut_T_17, %75 : i7
    %76 = sv.read_inout %_io_segOut_T_1 : !hw.inout<i1>
    %77 = sv.read_inout %_io_segOut_T_17 : !hw.inout<i7>
    %78 = comb.mux bin %76, %c48_i7, %77 : i7
    %_io_segOut_T_18 = sv.wire sym @sym_41 {hw.verilogName = "_io_segOut_T_18"} : !hw.inout<i7>
    sv.assign %_io_segOut_T_18, %78 : i7
    %79 = sv.read_inout %_io_segOut_T : !hw.inout<i1>
    %80 = sv.read_inout %_io_segOut_T_18 : !hw.inout<i7>
    %81 = comb.mux bin %79, %c-2_i7, %80 : i7
    sv.assign %_io_segOut_T_19, %81 : i7
    %82 = sv.read_inout %_io_segOut_T_19 : !hw.inout<i7>
    sv.assign %io_segOut, %82 : i7
    %83 = sv.read_inout %io_segOut : !hw.inout<i7>
    hw.output %83 : i7
  }
  om.class @SevenSegmentDisplayMuxCase_Class(%basepath: !om.frozenbasepath) {
    om.class.fields
  }
}
