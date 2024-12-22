module {
  firrtl.circuit "DeMux16" {
    firrtl.module private @Demultiplexer(in %io_input: !firrtl.uint<64>, in %io_select: !firrtl.uint<4>, out %io_outputs_0: !firrtl.uint<64>, out %io_outputs_1: !firrtl.uint<64>, out %io_outputs_2: !firrtl.uint<64>, out %io_outputs_3: !firrtl.uint<64>, out %io_outputs_4: !firrtl.uint<64>, out %io_outputs_5: !firrtl.uint<64>, out %io_outputs_6: !firrtl.uint<64>, out %io_outputs_7: !firrtl.uint<64>, out %io_outputs_8: !firrtl.uint<64>, out %io_outputs_9: !firrtl.uint<64>, out %io_outputs_10: !firrtl.uint<64>, out %io_outputs_11: !firrtl.uint<64>, out %io_outputs_12: !firrtl.uint<64>, out %io_outputs_13: !firrtl.uint<64>, out %io_outputs_14: !firrtl.uint<64>, out %io_outputs_15: !firrtl.uint<64>) {
      %c0_ui64 = firrtl.constant 0 : !firrtl.uint<64> {name = "_io_outputs_15_WIRE"}
      %c14_ui4 = firrtl.constant 14 : !firrtl.uint<4>
      %c13_ui4 = firrtl.constant 13 : !firrtl.uint<4>
      %c12_ui4 = firrtl.constant 12 : !firrtl.uint<4>
      %c11_ui4 = firrtl.constant 11 : !firrtl.uint<4>
      %c10_ui4 = firrtl.constant 10 : !firrtl.uint<4>
      %c9_ui4 = firrtl.constant 9 : !firrtl.uint<4>
      %c8_ui4 = firrtl.constant 8 : !firrtl.uint<4>
      %c7_ui4 = firrtl.constant 7 : !firrtl.uint<4>
      %c6_ui4 = firrtl.constant 6 : !firrtl.uint<4>
      %c5_ui4 = firrtl.constant 5 : !firrtl.uint<4>
      %c4_ui4 = firrtl.constant 4 : !firrtl.uint<4>
      %c3_ui4 = firrtl.constant 3 : !firrtl.uint<4>
      %c2_ui4 = firrtl.constant 2 : !firrtl.uint<4>
      %c1_ui4 = firrtl.constant 1 : !firrtl.uint<4>
      %io_outputs_0_0 = firrtl.wire {name = "io_outputs_0"} : !firrtl.uint<64>
      %io_outputs_1_1 = firrtl.wire {name = "io_outputs_1"} : !firrtl.uint<64>
      %io_outputs_2_2 = firrtl.wire {name = "io_outputs_2"} : !firrtl.uint<64>
      %io_outputs_3_3 = firrtl.wire {name = "io_outputs_3"} : !firrtl.uint<64>
      %io_outputs_4_4 = firrtl.wire {name = "io_outputs_4"} : !firrtl.uint<64>
      %io_outputs_5_5 = firrtl.wire {name = "io_outputs_5"} : !firrtl.uint<64>
      %io_outputs_6_6 = firrtl.wire {name = "io_outputs_6"} : !firrtl.uint<64>
      %io_outputs_7_7 = firrtl.wire {name = "io_outputs_7"} : !firrtl.uint<64>
      %io_outputs_8_8 = firrtl.wire {name = "io_outputs_8"} : !firrtl.uint<64>
      %io_outputs_9_9 = firrtl.wire {name = "io_outputs_9"} : !firrtl.uint<64>
      %io_outputs_10_10 = firrtl.wire {name = "io_outputs_10"} : !firrtl.uint<64>
      %io_outputs_11_11 = firrtl.wire {name = "io_outputs_11"} : !firrtl.uint<64>
      %io_outputs_12_12 = firrtl.wire {name = "io_outputs_12"} : !firrtl.uint<64>
      %io_outputs_13_13 = firrtl.wire {name = "io_outputs_13"} : !firrtl.uint<64>
      %io_outputs_14_14 = firrtl.wire {name = "io_outputs_14"} : !firrtl.uint<64>
      %io_outputs_15_15 = firrtl.wire {name = "io_outputs_15"} : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_0, %io_outputs_0_0 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_1, %io_outputs_1_1 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_2, %io_outputs_2_2 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_3, %io_outputs_3_3 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_4, %io_outputs_4_4 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_5, %io_outputs_5_5 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_6, %io_outputs_6_6 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_7, %io_outputs_7_7 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_8, %io_outputs_8_8 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_9, %io_outputs_9_9 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_10, %io_outputs_10_10 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_11, %io_outputs_11_11 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_12, %io_outputs_12_12 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_13, %io_outputs_13_13 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_14, %io_outputs_14_14 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_15, %io_outputs_15_15 : !firrtl.uint<64>
      %0 = firrtl.orr %io_select : (!firrtl.uint<4>) -> !firrtl.uint<1>
      %1 = firrtl.not %0 : (!firrtl.uint<1>) -> !firrtl.uint<1>
      %2 = firrtl.mux(%1, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_0_0, %2 : !firrtl.uint<64>
      %3 = firrtl.eq %io_select, %c1_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %4 = firrtl.mux(%3, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_1_1, %4 : !firrtl.uint<64>
      %5 = firrtl.eq %io_select, %c2_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %6 = firrtl.mux(%5, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_2_2, %6 : !firrtl.uint<64>
      %7 = firrtl.eq %io_select, %c3_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %8 = firrtl.mux(%7, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_3_3, %8 : !firrtl.uint<64>
      %9 = firrtl.eq %io_select, %c4_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %10 = firrtl.mux(%9, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_4_4, %10 : !firrtl.uint<64>
      %11 = firrtl.eq %io_select, %c5_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %12 = firrtl.mux(%11, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_5_5, %12 : !firrtl.uint<64>
      %13 = firrtl.eq %io_select, %c6_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %14 = firrtl.mux(%13, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_6_6, %14 : !firrtl.uint<64>
      %15 = firrtl.eq %io_select, %c7_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %16 = firrtl.mux(%15, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_7_7, %16 : !firrtl.uint<64>
      %17 = firrtl.eq %io_select, %c8_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %18 = firrtl.mux(%17, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_8_8, %18 : !firrtl.uint<64>
      %19 = firrtl.eq %io_select, %c9_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %20 = firrtl.mux(%19, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_9_9, %20 : !firrtl.uint<64>
      %21 = firrtl.eq %io_select, %c10_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %22 = firrtl.mux(%21, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_10_10, %22 : !firrtl.uint<64>
      %23 = firrtl.eq %io_select, %c11_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %24 = firrtl.mux(%23, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_11_11, %24 : !firrtl.uint<64>
      %25 = firrtl.eq %io_select, %c12_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %26 = firrtl.mux(%25, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_12_12, %26 : !firrtl.uint<64>
      %27 = firrtl.eq %io_select, %c13_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %28 = firrtl.mux(%27, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_13_13, %28 : !firrtl.uint<64>
      %29 = firrtl.eq %io_select, %c14_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %30 = firrtl.mux(%29, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_14_14, %30 : !firrtl.uint<64>
      %31 = firrtl.andr %io_select : (!firrtl.uint<4>) -> !firrtl.uint<1>
      %32 = firrtl.mux(%31, %io_input, %c0_ui64) : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_15_15, %32 : !firrtl.uint<64>
    }
    firrtl.module @DeMux16(in %clock: !firrtl.clock, in %reset: !firrtl.uint<1>, in %io_input: !firrtl.uint<64>, in %io_select: !firrtl.uint<4>, out %io_outputs_0: !firrtl.uint<64>, out %io_outputs_1: !firrtl.uint<64>, out %io_outputs_2: !firrtl.uint<64>, out %io_outputs_3: !firrtl.uint<64>, out %io_outputs_4: !firrtl.uint<64>, out %io_outputs_5: !firrtl.uint<64>, out %io_outputs_6: !firrtl.uint<64>, out %io_outputs_7: !firrtl.uint<64>, out %io_outputs_8: !firrtl.uint<64>, out %io_outputs_9: !firrtl.uint<64>, out %io_outputs_10: !firrtl.uint<64>, out %io_outputs_11: !firrtl.uint<64>, out %io_outputs_12: !firrtl.uint<64>, out %io_outputs_13: !firrtl.uint<64>, out %io_outputs_14: !firrtl.uint<64>, out %io_outputs_15: !firrtl.uint<64>) attributes {convention = #firrtl<convention scalarized>} {
      %io_outputs_0_0 = firrtl.wire {name = "io_outputs_0"} : !firrtl.uint<64>
      %io_outputs_1_1 = firrtl.wire {name = "io_outputs_1"} : !firrtl.uint<64>
      %io_outputs_2_2 = firrtl.wire {name = "io_outputs_2"} : !firrtl.uint<64>
      %io_outputs_3_3 = firrtl.wire {name = "io_outputs_3"} : !firrtl.uint<64>
      %io_outputs_4_4 = firrtl.wire {name = "io_outputs_4"} : !firrtl.uint<64>
      %io_outputs_5_5 = firrtl.wire {name = "io_outputs_5"} : !firrtl.uint<64>
      %io_outputs_6_6 = firrtl.wire {name = "io_outputs_6"} : !firrtl.uint<64>
      %io_outputs_7_7 = firrtl.wire {name = "io_outputs_7"} : !firrtl.uint<64>
      %io_outputs_8_8 = firrtl.wire {name = "io_outputs_8"} : !firrtl.uint<64>
      %io_outputs_9_9 = firrtl.wire {name = "io_outputs_9"} : !firrtl.uint<64>
      %io_outputs_10_10 = firrtl.wire {name = "io_outputs_10"} : !firrtl.uint<64>
      %io_outputs_11_11 = firrtl.wire {name = "io_outputs_11"} : !firrtl.uint<64>
      %io_outputs_12_12 = firrtl.wire {name = "io_outputs_12"} : !firrtl.uint<64>
      %io_outputs_13_13 = firrtl.wire {name = "io_outputs_13"} : !firrtl.uint<64>
      %io_outputs_14_14 = firrtl.wire {name = "io_outputs_14"} : !firrtl.uint<64>
      %io_outputs_15_15 = firrtl.wire {name = "io_outputs_15"} : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_0, %io_outputs_0_0 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_1, %io_outputs_1_1 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_2, %io_outputs_2_2 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_3, %io_outputs_3_3 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_4, %io_outputs_4_4 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_5, %io_outputs_5_5 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_6, %io_outputs_6_6 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_7, %io_outputs_7_7 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_8, %io_outputs_8_8 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_9, %io_outputs_9_9 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_10, %io_outputs_10_10 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_11, %io_outputs_11_11 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_12, %io_outputs_12_12 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_13, %io_outputs_13_13 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_14, %io_outputs_14_14 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_15, %io_outputs_15_15 : !firrtl.uint<64>
      %demux_io_input, %demux_io_select, %demux_io_outputs_0, %demux_io_outputs_1, %demux_io_outputs_2, %demux_io_outputs_3, %demux_io_outputs_4, %demux_io_outputs_5, %demux_io_outputs_6, %demux_io_outputs_7, %demux_io_outputs_8, %demux_io_outputs_9, %demux_io_outputs_10, %demux_io_outputs_11, %demux_io_outputs_12, %demux_io_outputs_13, %demux_io_outputs_14, %demux_io_outputs_15 = firrtl.instance demux @Demultiplexer(in io_input: !firrtl.uint<64>, in io_select: !firrtl.uint<4>, out io_outputs_0: !firrtl.uint<64>, out io_outputs_1: !firrtl.uint<64>, out io_outputs_2: !firrtl.uint<64>, out io_outputs_3: !firrtl.uint<64>, out io_outputs_4: !firrtl.uint<64>, out io_outputs_5: !firrtl.uint<64>, out io_outputs_6: !firrtl.uint<64>, out io_outputs_7: !firrtl.uint<64>, out io_outputs_8: !firrtl.uint<64>, out io_outputs_9: !firrtl.uint<64>, out io_outputs_10: !firrtl.uint<64>, out io_outputs_11: !firrtl.uint<64>, out io_outputs_12: !firrtl.uint<64>, out io_outputs_13: !firrtl.uint<64>, out io_outputs_14: !firrtl.uint<64>, out io_outputs_15: !firrtl.uint<64>)
      %demux.io_outputs_0 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_1 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_2 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_3 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_4 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_5 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_6 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_7 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_8 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_9 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_10 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_11 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_12 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_13 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_14 = firrtl.wire : !firrtl.uint<64>
      %demux.io_outputs_15 = firrtl.wire : !firrtl.uint<64>
      firrtl.matchingconnect %demux_io_input, %io_input : !firrtl.uint<64>
      firrtl.matchingconnect %demux_io_select, %io_select : !firrtl.uint<4>
      firrtl.matchingconnect %demux.io_outputs_0, %demux_io_outputs_0 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_1, %demux_io_outputs_1 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_2, %demux_io_outputs_2 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_3, %demux_io_outputs_3 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_4, %demux_io_outputs_4 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_5, %demux_io_outputs_5 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_6, %demux_io_outputs_6 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_7, %demux_io_outputs_7 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_8, %demux_io_outputs_8 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_9, %demux_io_outputs_9 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_10, %demux_io_outputs_10 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_11, %demux_io_outputs_11 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_12, %demux_io_outputs_12 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_13, %demux_io_outputs_13 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_14, %demux_io_outputs_14 : !firrtl.uint<64>
      firrtl.matchingconnect %demux.io_outputs_15, %demux_io_outputs_15 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_0_0, %demux.io_outputs_0 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_1_1, %demux.io_outputs_1 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_2_2, %demux.io_outputs_2 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_3_3, %demux.io_outputs_3 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_4_4, %demux.io_outputs_4 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_5_5, %demux.io_outputs_5 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_6_6, %demux.io_outputs_6 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_7_7, %demux.io_outputs_7 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_8_8, %demux.io_outputs_8 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_9_9, %demux.io_outputs_9 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_10_10, %demux.io_outputs_10 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_11_11, %demux.io_outputs_11 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_12_12, %demux.io_outputs_12 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_13_13, %demux.io_outputs_13 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_14_14, %demux.io_outputs_14 : !firrtl.uint<64>
      firrtl.matchingconnect %io_outputs_15_15, %demux.io_outputs_15 : !firrtl.uint<64>
    }
  }
}
