module {
  hw.module @SevenSegmentDisplay(in %clock : i1, in %reset : i1, in %io_binIn : i4, out io_segOut : i7) {
    %io_binIn_0 = sv.wire sym @sym name "io_binIn" {hw.verilogName = "io_binIn_0"} : !hw.inout<i4>
    sv.assign %io_binIn_0, %io_binIn : i4
    %0 = hw.aggregate_constant [0 : i7, 0 : i7, 0 : i7, 0 : i7, 0 : i7, 0 : i7, -5 : i7, -1 : i7, -16 : i7, -33 : i7, -37 : i7, 51 : i7, -7 : i7, -19 : i7, 48 : i7, -2 : i7] : !hw.array<16xi7>
    %1 = sv.wire {hw.verilogName = "_GEN"} : !hw.inout<array<16xi7>>
    sv.assign %1, %0 : !hw.array<16xi7>
    %2 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %3 = comb.extract %2 from 0 : (i4) -> i1
    %_B0_T = sv.wire sym @sym_1 {hw.verilogName = "_B0_T"} : !hw.inout<i1>
    sv.assign %_B0_T, %3 : i1
    %4 = sv.read_inout %_B0_T : !hw.inout<i1>
    %B0 = sv.wire sym @sym_2 {hw.verilogName = "B0"} : !hw.inout<i1>
    sv.assign %B0, %4 : i1
    %5 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %6 = comb.extract %5 from 1 : (i4) -> i1
    %_B1_T = sv.wire sym @sym_3 {hw.verilogName = "_B1_T"} : !hw.inout<i1>
    sv.assign %_B1_T, %6 : i1
    %7 = sv.read_inout %_B1_T : !hw.inout<i1>
    %B1 = sv.wire sym @sym_4 {hw.verilogName = "B1"} : !hw.inout<i1>
    sv.assign %B1, %7 : i1
    %8 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %9 = comb.extract %8 from 2 : (i4) -> i1
    %_B2_T = sv.wire sym @sym_5 {hw.verilogName = "_B2_T"} : !hw.inout<i1>
    sv.assign %_B2_T, %9 : i1
    %10 = sv.read_inout %_B2_T : !hw.inout<i1>
    %B2 = sv.wire sym @sym_6 {hw.verilogName = "B2"} : !hw.inout<i1>
    sv.assign %B2, %10 : i1
    %11 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %12 = comb.extract %11 from 3 : (i4) -> i1
    %_B3_T = sv.wire sym @sym_7 {hw.verilogName = "_B3_T"} : !hw.inout<i1>
    sv.assign %_B3_T, %12 : i1
    %13 = sv.read_inout %_B3_T : !hw.inout<i1>
    %B3 = sv.wire sym @sym_8 {hw.verilogName = "B3"} : !hw.inout<i1>
    sv.assign %B3, %13 : i1
    %io_segOut = sv.wire sym @sym_0 {hw.verilogName = "io_segOut_0"} : !hw.inout<i7>
    %14 = sv.read_inout %io_segOut : !hw.inout<i7>
    %15 = comb.extract %14 from 6 : (i7) -> i1
    %_a_T = sv.wire sym @sym_9 {hw.verilogName = "_a_T"} : !hw.inout<i1>
    sv.assign %_a_T, %15 : i1
    %16 = sv.read_inout %_a_T : !hw.inout<i1>
    %a = sv.wire sym @sym_10 {hw.verilogName = "a"} : !hw.inout<i1>
    sv.assign %a, %16 : i1
    %17 = sv.read_inout %io_segOut : !hw.inout<i7>
    %18 = comb.extract %17 from 5 : (i7) -> i1
    %_b_T = sv.wire sym @sym_11 {hw.verilogName = "_b_T"} : !hw.inout<i1>
    sv.assign %_b_T, %18 : i1
    %19 = sv.read_inout %_b_T : !hw.inout<i1>
    %b = sv.wire sym @sym_12 {hw.verilogName = "b"} : !hw.inout<i1>
    sv.assign %b, %19 : i1
    %20 = sv.read_inout %io_segOut : !hw.inout<i7>
    %21 = comb.extract %20 from 4 : (i7) -> i1
    %_c_T = sv.wire sym @sym_13 {hw.verilogName = "_c_T"} : !hw.inout<i1>
    sv.assign %_c_T, %21 : i1
    %22 = sv.read_inout %_c_T : !hw.inout<i1>
    %c = sv.wire sym @sym_14 {hw.verilogName = "c"} : !hw.inout<i1>
    sv.assign %c, %22 : i1
    %23 = sv.read_inout %io_segOut : !hw.inout<i7>
    %24 = comb.extract %23 from 3 : (i7) -> i1
    %_d_T = sv.wire sym @sym_15 {hw.verilogName = "_d_T"} : !hw.inout<i1>
    sv.assign %_d_T, %24 : i1
    %25 = sv.read_inout %_d_T : !hw.inout<i1>
    %d = sv.wire sym @sym_16 {hw.verilogName = "d"} : !hw.inout<i1>
    sv.assign %d, %25 : i1
    %26 = sv.read_inout %io_segOut : !hw.inout<i7>
    %27 = comb.extract %26 from 2 : (i7) -> i1
    %_e_T = sv.wire sym @sym_17 {hw.verilogName = "_e_T"} : !hw.inout<i1>
    sv.assign %_e_T, %27 : i1
    %28 = sv.read_inout %_e_T : !hw.inout<i1>
    %e = sv.wire sym @sym_18 {hw.verilogName = "e"} : !hw.inout<i1>
    sv.assign %e, %28 : i1
    %29 = sv.read_inout %io_segOut : !hw.inout<i7>
    %30 = comb.extract %29 from 1 : (i7) -> i1
    %_f_T = sv.wire sym @sym_19 {hw.verilogName = "_f_T"} : !hw.inout<i1>
    sv.assign %_f_T, %30 : i1
    %31 = sv.read_inout %_f_T : !hw.inout<i1>
    %f = sv.wire sym @sym_20 {hw.verilogName = "f"} : !hw.inout<i1>
    sv.assign %f, %31 : i1
    %32 = sv.read_inout %io_segOut : !hw.inout<i7>
    %33 = comb.extract %32 from 0 : (i7) -> i1
    %_g_T = sv.wire sym @sym_21 {hw.verilogName = "_g_T"} : !hw.inout<i1>
    sv.assign %_g_T, %33 : i1
    %34 = sv.read_inout %_g_T : !hw.inout<i1>
    %g = sv.wire sym @sym_22 {hw.verilogName = "g"} : !hw.inout<i1>
    sv.assign %g, %34 : i1
    %35 = sv.read_inout %io_binIn_0 : !hw.inout<i4>
    %36 = sv.read_inout %1 : !hw.inout<array<16xi7>>
    %37 = hw.array_get %36[%35] : !hw.array<16xi7>, i4
    sv.assign %io_segOut, %37 : i7
    %38 = sv.read_inout %io_segOut : !hw.inout<i7>
    hw.output %38 : i7
  }
  om.class @SevenSegmentDisplay_Class(%basepath: !om.frozenbasepath) {
    om.class.fields
  }
}
