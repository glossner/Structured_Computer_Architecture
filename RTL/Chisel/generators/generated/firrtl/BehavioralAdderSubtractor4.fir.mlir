module {
  hw.module private @BehavioralAdderSubtractor(in %io_a : i4, in %io_b : i4, in %io_subtract : i1, out io_result : i4) {
    %io_a_0 = sv.wire sym @sym name "io_a" {hw.verilogName = "io_a_0"} : !hw.inout<i4>
    sv.assign %io_a_0, %io_a : i4
    %io_b_1 = sv.wire sym @sym_0 name "io_b" {hw.verilogName = "io_b_0"} : !hw.inout<i4>
    sv.assign %io_b_1, %io_b : i4
    %io_subtract_2 = sv.wire sym @sym_1 name "io_subtract" {hw.verilogName = "io_subtract_0"} : !hw.inout<i1>
    sv.assign %io_subtract_2, %io_subtract : i1
    %false = hw.constant false
    %0 = sv.read_inout %io_subtract_2 : !hw.inout<i1>
    %_fullResult_T = sv.wire sym @sym_3 {hw.verilogName = "_fullResult_T"} : !hw.inout<i1>
    sv.assign %_fullResult_T, %0 : i1
    %_io_result_T = sv.wire sym @sym_9 {hw.verilogName = "_io_result_T"} : !hw.inout<i4>
    %1 = sv.read_inout %io_a_0 : !hw.inout<i4>
    %2 = comb.concat %false, %1 : i1, i4
    %3 = sv.wire {hw.verilogName = "_GEN"} : !hw.inout<i5>
    sv.assign %3, %2 : i5
    %4 = sv.read_inout %io_b_1 : !hw.inout<i4>
    %5 = comb.concat %false, %4 : i1, i4
    %6 = sv.wire {hw.verilogName = "_GEN_0"} : !hw.inout<i5>
    sv.assign %6, %5 : i5
    %7 = sv.read_inout %3 : !hw.inout<i5>
    %8 = sv.read_inout %6 : !hw.inout<i5>
    %9 = comb.sub bin %7, %8 : i5
    %_fullResult_T_1 = sv.wire sym @sym_4 {hw.verilogName = "_fullResult_T_1"} : !hw.inout<i5>
    sv.assign %_fullResult_T_1, %9 : i5
    %10 = sv.read_inout %_fullResult_T_1 : !hw.inout<i5>
    %11 = comb.extract %10 from 0 : (i5) -> i4
    %_fullResult_T_2 = sv.wire sym @sym_5 {hw.verilogName = "_fullResult_T_2"} : !hw.inout<i4>
    sv.assign %_fullResult_T_2, %11 : i4
    %12 = sv.read_inout %3 : !hw.inout<i5>
    %13 = sv.read_inout %6 : !hw.inout<i5>
    %14 = comb.add bin %12, %13 : i5
    %_fullResult_T_3 = sv.wire sym @sym_6 {hw.verilogName = "_fullResult_T_3"} : !hw.inout<i5>
    sv.assign %_fullResult_T_3, %14 : i5
    %15 = sv.read_inout %_fullResult_T_3 : !hw.inout<i5>
    %16 = comb.extract %15 from 0 : (i5) -> i4
    %_fullResult_T_4 = sv.wire sym @sym_7 {hw.verilogName = "_fullResult_T_4"} : !hw.inout<i4>
    sv.assign %_fullResult_T_4, %16 : i4
    %17 = sv.read_inout %_fullResult_T : !hw.inout<i1>
    %18 = sv.read_inout %_fullResult_T_2 : !hw.inout<i4>
    %19 = sv.read_inout %_fullResult_T_4 : !hw.inout<i4>
    %20 = comb.mux bin %17, %18, %19 : i4
    %fullResult = sv.wire sym @sym_8 {hw.verilogName = "fullResult"} : !hw.inout<i4>
    sv.assign %fullResult, %20 : i4
    %21 = sv.read_inout %fullResult : !hw.inout<i4>
    sv.assign %_io_result_T, %21 : i4
    %22 = sv.read_inout %_io_result_T : !hw.inout<i4>
    %io_result = sv.wire sym @sym_2 {hw.verilogName = "io_result_0"} : !hw.inout<i4>
    sv.assign %io_result, %22 : i4
    %23 = sv.read_inout %io_result : !hw.inout<i4>
    hw.output %23 : i4
  }
  hw.module @BehavioralAdderSubtractor4(in %clock : i1, in %reset : i1, in %io_a : i4, in %io_b : i4, in %io_subtract : i1, out io_sum : i4) {
    %io_sum = sv.wire sym @sym_2 {hw.verilogName = "io_sum_0"} : !hw.inout<i4>
    %io_a_0 = sv.wire sym @sym name "io_a" {hw.verilogName = "io_a_0", sv.namehint = "io_a"} : !hw.inout<i4>
    sv.assign %io_a_0, %io_a : i4
    %io_b_1 = sv.wire sym @sym_0 name "io_b" {hw.verilogName = "io_b_0", sv.namehint = "io_b"} : !hw.inout<i4>
    sv.assign %io_b_1, %io_b : i4
    %io_subtract_2 = sv.wire sym @sym_1 name "io_subtract" {hw.verilogName = "io_subtract_0", sv.namehint = "io_subtract"} : !hw.inout<i1>
    sv.assign %io_subtract_2, %io_subtract : i1
    %0 = sv.read_inout %io_a_0 : !hw.inout<i4>
    %1 = sv.read_inout %io_b_1 : !hw.inout<i4>
    %2 = sv.read_inout %io_subtract_2 : !hw.inout<i1>
    %io_sum_module.io_result = hw.instance "io_sum_module" @BehavioralAdderSubtractor(io_a: %0: i4, io_b: %1: i4, io_subtract: %2: i1) -> (io_result: i4) {hw.verilogName = "io_sum_module", sv.namehint = "io_sum_module.io_result"}
    sv.assign %io_sum, %io_sum_module.io_result : i4
    %3 = sv.read_inout %io_sum : !hw.inout<i4>
    hw.output %3 : i4
  }
  om.class @BehavioralAdderSubtractor4_Class(%basepath: !om.frozenbasepath) {
    om.class.fields
  }
}
