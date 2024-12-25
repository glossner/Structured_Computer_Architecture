module {
  firrtl.circuit "SevenSegmentDisplayFlat" {
    firrtl.module @SevenSegmentDisplayFlat(in %clock: !firrtl.clock, in %reset: !firrtl.uint<1>, in %io_binIn_B0: !firrtl.uint<1>, in %io_binIn_B1: !firrtl.uint<1>, in %io_binIn_B2: !firrtl.uint<1>, in %io_binIn_B3: !firrtl.uint<1>, out %io_segOut_a: !firrtl.uint<1>, out %io_segOut_b: !firrtl.uint<1>, out %io_segOut_c: !firrtl.uint<1>, out %io_segOut_d: !firrtl.uint<1>, out %io_segOut_e: !firrtl.uint<1>, out %io_segOut_f: !firrtl.uint<1>, out %io_segOut_g: !firrtl.uint<1>) {
      %c51_ui7 = firrtl.constant 51 : !firrtl.uint<7>
      %c48_ui7 = firrtl.constant 48 : !firrtl.uint<7>
      %c0_ui7 = firrtl.constant 0 : !firrtl.uint<7>
      %c123_ui7 = firrtl.constant 123 : !firrtl.uint<7>
      %c9_ui4 = firrtl.constant 9 : !firrtl.uint<4>
      %c127_ui7 = firrtl.constant 127 : !firrtl.uint<7>
      %c8_ui4 = firrtl.constant 8 : !firrtl.uint<4>
      %c112_ui7 = firrtl.constant 112 : !firrtl.uint<7>
      %c7_ui3 = firrtl.constant 7 : !firrtl.uint<3>
      %c95_ui7 = firrtl.constant 95 : !firrtl.uint<7>
      %c6_ui3 = firrtl.constant 6 : !firrtl.uint<3>
      %c91_ui7 = firrtl.constant 91 : !firrtl.uint<7>
      %c5_ui3 = firrtl.constant 5 : !firrtl.uint<3>
      %c4_ui3 = firrtl.constant 4 : !firrtl.uint<3>
      %c121_ui7 = firrtl.constant 121 : !firrtl.uint<7>
      %c3_ui2 = firrtl.constant 3 : !firrtl.uint<2>
      %c109_ui7 = firrtl.constant 109 : !firrtl.uint<7>
      %c2_ui2 = firrtl.constant 2 : !firrtl.uint<2>
      %c1_ui1 = firrtl.constant 1 : !firrtl.uint<1>
      %c126_ui7 = firrtl.constant 126 : !firrtl.uint<7>
      %io = firrtl.wire : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      %0 = firrtl.subfield %io[binIn_B0] : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      %1 = firrtl.subfield %io[binIn_B1] : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      %2 = firrtl.subfield %io[binIn_B2] : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      %3 = firrtl.subfield %io[binIn_B3] : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      %4 = firrtl.subfield %io[segOut_a] : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      firrtl.matchingconnect %io_segOut_a, %4 : !firrtl.uint<1>
      %5 = firrtl.subfield %io[segOut_b] : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      firrtl.matchingconnect %io_segOut_b, %5 : !firrtl.uint<1>
      %6 = firrtl.subfield %io[segOut_c] : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      firrtl.matchingconnect %io_segOut_c, %6 : !firrtl.uint<1>
      %7 = firrtl.subfield %io[segOut_d] : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      firrtl.matchingconnect %io_segOut_d, %7 : !firrtl.uint<1>
      %8 = firrtl.subfield %io[segOut_e] : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      firrtl.matchingconnect %io_segOut_e, %8 : !firrtl.uint<1>
      %9 = firrtl.subfield %io[segOut_f] : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      firrtl.matchingconnect %io_segOut_f, %9 : !firrtl.uint<1>
      %10 = firrtl.subfield %io[segOut_g] : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      firrtl.matchingconnect %io_segOut_g, %10 : !firrtl.uint<1>
      %binIn = firrtl.wire : !firrtl.uint<4>
      %binIn_lo = firrtl.cat %1, %0 {name = "binIn_lo"} : (!firrtl.uint<1>, !firrtl.uint<1>) -> !firrtl.uint<2>
      %binIn_hi = firrtl.cat %3, %2 {name = "binIn_hi"} : (!firrtl.uint<1>, !firrtl.uint<1>) -> !firrtl.uint<2>
      %_binIn_T = firrtl.cat %binIn_hi, %binIn_lo {name = "_binIn_T"} : (!firrtl.uint<2>, !firrtl.uint<2>) -> !firrtl.uint<4>
      firrtl.matchingconnect %binIn, %_binIn_T : !firrtl.uint<4>
      %segOut = firrtl.wire : !firrtl.uint<7>
      %_io_segOut_a_T = firrtl.bits %segOut 6 to 6 {name = "_io_segOut_a_T"} : (!firrtl.uint<7>) -> !firrtl.uint<1>
      %_io_segOut_b_T = firrtl.bits %segOut 5 to 5 {name = "_io_segOut_b_T"} : (!firrtl.uint<7>) -> !firrtl.uint<1>
      %_io_segOut_c_T = firrtl.bits %segOut 4 to 4 {name = "_io_segOut_c_T"} : (!firrtl.uint<7>) -> !firrtl.uint<1>
      %_io_segOut_d_T = firrtl.bits %segOut 3 to 3 {name = "_io_segOut_d_T"} : (!firrtl.uint<7>) -> !firrtl.uint<1>
      %_io_segOut_e_T = firrtl.bits %segOut 2 to 2 {name = "_io_segOut_e_T"} : (!firrtl.uint<7>) -> !firrtl.uint<1>
      %_io_segOut_f_T = firrtl.bits %segOut 1 to 1 {name = "_io_segOut_f_T"} : (!firrtl.uint<7>) -> !firrtl.uint<1>
      %_io_segOut_g_T = firrtl.bits %segOut 0 to 0 {name = "_io_segOut_g_T"} : (!firrtl.uint<7>) -> !firrtl.uint<1>
      %11 = firrtl.bundlecreate %io_binIn_B0, %io_binIn_B1, %io_binIn_B2, %io_binIn_B3, %_io_segOut_a_T, %_io_segOut_b_T, %_io_segOut_c_T, %_io_segOut_d_T, %_io_segOut_e_T, %_io_segOut_f_T, %_io_segOut_g_T : (!firrtl.uint<1>, !firrtl.uint<1>, !firrtl.uint<1>, !firrtl.uint<1>, !firrtl.uint<1>, !firrtl.uint<1>, !firrtl.uint<1>, !firrtl.uint<1>, !firrtl.uint<1>, !firrtl.uint<1>, !firrtl.uint<1>) -> !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      firrtl.matchingconnect %io, %11 : !firrtl.bundle<binIn_B0: uint<1>, binIn_B1: uint<1>, binIn_B2: uint<1>, binIn_B3: uint<1>, segOut_a: uint<1>, segOut_b: uint<1>, segOut_c: uint<1>, segOut_d: uint<1>, segOut_e: uint<1>, segOut_f: uint<1>, segOut_g: uint<1>>
      %12 = firrtl.orr %binIn : (!firrtl.uint<4>) -> !firrtl.uint<1>
      %13 = firrtl.not %12 : (!firrtl.uint<1>) -> !firrtl.uint<1>
      %14 = firrtl.eq %binIn, %c1_ui1 : (!firrtl.uint<4>, !firrtl.uint<1>) -> !firrtl.uint<1>
      %15 = firrtl.eq %binIn, %c2_ui2 : (!firrtl.uint<4>, !firrtl.uint<2>) -> !firrtl.uint<1>
      %16 = firrtl.eq %binIn, %c3_ui2 : (!firrtl.uint<4>, !firrtl.uint<2>) -> !firrtl.uint<1>
      %17 = firrtl.eq %binIn, %c4_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %18 = firrtl.eq %binIn, %c5_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %19 = firrtl.eq %binIn, %c6_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %20 = firrtl.eq %binIn, %c7_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %21 = firrtl.eq %binIn, %c8_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %22 = firrtl.eq %binIn, %c9_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %23 = firrtl.mux(%22, %c123_ui7, %c0_ui7) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %24 = firrtl.mux(%21, %c127_ui7, %23) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %25 = firrtl.mux(%20, %c112_ui7, %24) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %26 = firrtl.mux(%19, %c95_ui7, %25) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %27 = firrtl.mux(%18, %c91_ui7, %26) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %28 = firrtl.mux(%17, %c51_ui7, %27) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %29 = firrtl.mux(%16, %c121_ui7, %28) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %30 = firrtl.mux(%15, %c109_ui7, %29) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %31 = firrtl.mux(%14, %c48_ui7, %30) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %32 = firrtl.mux(%13, %c126_ui7, %31) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      firrtl.matchingconnect %segOut, %32 : !firrtl.uint<7>
    }
  }
}
