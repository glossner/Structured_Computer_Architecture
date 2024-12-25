module {
  firrtl.circuit "SevenSegmentDisplayMux" {
    firrtl.module @SevenSegmentDisplayMux(in %clock: !firrtl.clock, in %reset: !firrtl.uint<1>, in %io_binIn: !firrtl.uint<4>, out %io_segOut: !firrtl.uint<7>) {
      %c126_ui7 = firrtl.constant 126 : !firrtl.uint<7>
      %c48_ui7 = firrtl.constant 48 : !firrtl.uint<7>
      %c109_ui7 = firrtl.constant 109 : !firrtl.uint<7>
      %c121_ui7 = firrtl.constant 121 : !firrtl.uint<7>
      %c51_ui7 = firrtl.constant 51 : !firrtl.uint<7>
      %c91_ui7 = firrtl.constant 91 : !firrtl.uint<7>
      %c95_ui7 = firrtl.constant 95 : !firrtl.uint<7>
      %c112_ui7 = firrtl.constant 112 : !firrtl.uint<7>
      %c127_ui7 = firrtl.constant 127 : !firrtl.uint<7>
      %c0_ui7 = firrtl.constant 0 : !firrtl.uint<7>
      %c123_ui7 = firrtl.constant 123 : !firrtl.uint<7>
      %c9_ui4 = firrtl.constant 9 : !firrtl.uint<4>
      %c8_ui4 = firrtl.constant 8 : !firrtl.uint<4>
      %c7_ui3 = firrtl.constant 7 : !firrtl.uint<3>
      %c6_ui3 = firrtl.constant 6 : !firrtl.uint<3>
      %c5_ui3 = firrtl.constant 5 : !firrtl.uint<3>
      %c4_ui3 = firrtl.constant 4 : !firrtl.uint<3>
      %c3_ui2 = firrtl.constant 3 : !firrtl.uint<2>
      %c2_ui2 = firrtl.constant 2 : !firrtl.uint<2>
      %c1_ui1 = firrtl.constant 1 : !firrtl.uint<1>
      %io = firrtl.wire : !firrtl.bundle<binIn: uint<4>, segOut: uint<7>>
      %0 = firrtl.subfield %io[binIn] : !firrtl.bundle<binIn: uint<4>, segOut: uint<7>>
      %1 = firrtl.subfield %io[segOut] : !firrtl.bundle<binIn: uint<4>, segOut: uint<7>>
      firrtl.matchingconnect %io_segOut, %1 : !firrtl.uint<7>
      %2 = firrtl.orr %0 : (!firrtl.uint<4>) -> !firrtl.uint<1>
      %_io_segOut_T = firrtl.not %2 {name = "_io_segOut_T"} : (!firrtl.uint<1>) -> !firrtl.uint<1>
      %_io_segOut_T_1 = firrtl.eq %0, %c1_ui1 {name = "_io_segOut_T_1"} : (!firrtl.uint<4>, !firrtl.uint<1>) -> !firrtl.uint<1>
      %_io_segOut_T_2 = firrtl.eq %0, %c2_ui2 {name = "_io_segOut_T_2"} : (!firrtl.uint<4>, !firrtl.uint<2>) -> !firrtl.uint<1>
      %_io_segOut_T_3 = firrtl.eq %0, %c3_ui2 {name = "_io_segOut_T_3"} : (!firrtl.uint<4>, !firrtl.uint<2>) -> !firrtl.uint<1>
      %_io_segOut_T_4 = firrtl.eq %0, %c4_ui3 {name = "_io_segOut_T_4"} : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %_io_segOut_T_5 = firrtl.eq %0, %c5_ui3 {name = "_io_segOut_T_5"} : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %_io_segOut_T_6 = firrtl.eq %0, %c6_ui3 {name = "_io_segOut_T_6"} : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %_io_segOut_T_7 = firrtl.eq %0, %c7_ui3 {name = "_io_segOut_T_7"} : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %_io_segOut_T_8 = firrtl.eq %0, %c8_ui4 {name = "_io_segOut_T_8"} : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %_io_segOut_T_9 = firrtl.eq %0, %c9_ui4 {name = "_io_segOut_T_9"} : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %_io_segOut_T_10 = firrtl.mux(%_io_segOut_T_9, %c123_ui7, %c0_ui7) {name = "_io_segOut_T_10"} : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %_io_segOut_T_11 = firrtl.mux(%_io_segOut_T_8, %c127_ui7, %_io_segOut_T_10) {name = "_io_segOut_T_11"} : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %_io_segOut_T_12 = firrtl.mux(%_io_segOut_T_7, %c112_ui7, %_io_segOut_T_11) {name = "_io_segOut_T_12"} : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %_io_segOut_T_13 = firrtl.mux(%_io_segOut_T_6, %c95_ui7, %_io_segOut_T_12) {name = "_io_segOut_T_13"} : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %_io_segOut_T_14 = firrtl.mux(%_io_segOut_T_5, %c91_ui7, %_io_segOut_T_13) {name = "_io_segOut_T_14"} : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %_io_segOut_T_15 = firrtl.mux(%_io_segOut_T_4, %c51_ui7, %_io_segOut_T_14) {name = "_io_segOut_T_15"} : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %_io_segOut_T_16 = firrtl.mux(%_io_segOut_T_3, %c121_ui7, %_io_segOut_T_15) {name = "_io_segOut_T_16"} : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %_io_segOut_T_17 = firrtl.mux(%_io_segOut_T_2, %c109_ui7, %_io_segOut_T_16) {name = "_io_segOut_T_17"} : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %_io_segOut_T_18 = firrtl.mux(%_io_segOut_T_1, %c48_ui7, %_io_segOut_T_17) {name = "_io_segOut_T_18"} : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %_io_segOut_T_19 = firrtl.mux(%_io_segOut_T, %c126_ui7, %_io_segOut_T_18) {name = "_io_segOut_T_19"} : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %3 = firrtl.bundlecreate %io_binIn, %_io_segOut_T_19 : (!firrtl.uint<4>, !firrtl.uint<7>) -> !firrtl.bundle<binIn: uint<4>, segOut: uint<7>>
      firrtl.matchingconnect %io, %3 : !firrtl.bundle<binIn: uint<4>, segOut: uint<7>>
    }
  }
}
