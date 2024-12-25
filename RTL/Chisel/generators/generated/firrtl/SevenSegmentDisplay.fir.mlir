module {
  firrtl.circuit "SevenSegmentDisplay" {
    firrtl.module @SevenSegmentDisplay(in %clock: !firrtl.clock, in %reset: !firrtl.uint<1>, in %io_binIn: !firrtl.uint<4>, out %io_segOut: !firrtl.uint<7>) {
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
      %c51_ui7 = firrtl.constant 51 : !firrtl.uint<7>
      %c4_ui3 = firrtl.constant 4 : !firrtl.uint<3>
      %c121_ui7 = firrtl.constant 121 : !firrtl.uint<7>
      %c3_ui2 = firrtl.constant 3 : !firrtl.uint<2>
      %c109_ui7 = firrtl.constant 109 : !firrtl.uint<7>
      %c2_ui2 = firrtl.constant 2 : !firrtl.uint<2>
      %c48_ui7 = firrtl.constant 48 : !firrtl.uint<7>
      %c1_ui1 = firrtl.constant 1 : !firrtl.uint<1>
      %c126_ui7 = firrtl.constant 126 : !firrtl.uint<7>
      %c0_ui7 = firrtl.constant 0 : !firrtl.uint<7>
      %io = firrtl.wire : !firrtl.bundle<binIn: uint<4>, segOut: uint<7>>
      %0 = firrtl.subfield %io[binIn] : !firrtl.bundle<binIn: uint<4>, segOut: uint<7>>
      %1 = firrtl.subfield %io[segOut] : !firrtl.bundle<binIn: uint<4>, segOut: uint<7>>
      firrtl.matchingconnect %io_segOut, %1 : !firrtl.uint<7>
      %2 = firrtl.orr %0 : (!firrtl.uint<4>) -> !firrtl.uint<1>
      %3 = firrtl.not %2 : (!firrtl.uint<1>) -> !firrtl.uint<1>
      %4 = firrtl.eq %0, %c1_ui1 : (!firrtl.uint<4>, !firrtl.uint<1>) -> !firrtl.uint<1>
      %5 = firrtl.eq %0, %c2_ui2 : (!firrtl.uint<4>, !firrtl.uint<2>) -> !firrtl.uint<1>
      %6 = firrtl.eq %0, %c3_ui2 : (!firrtl.uint<4>, !firrtl.uint<2>) -> !firrtl.uint<1>
      %7 = firrtl.eq %0, %c4_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %8 = firrtl.eq %0, %c5_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %9 = firrtl.eq %0, %c6_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %10 = firrtl.eq %0, %c7_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %11 = firrtl.eq %0, %c8_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %12 = firrtl.eq %0, %c9_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %13 = firrtl.mux(%12, %c123_ui7, %c0_ui7) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %14 = firrtl.mux(%11, %c127_ui7, %13) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %15 = firrtl.mux(%10, %c112_ui7, %14) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %16 = firrtl.mux(%9, %c95_ui7, %15) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %17 = firrtl.mux(%8, %c91_ui7, %16) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %18 = firrtl.mux(%7, %c51_ui7, %17) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %19 = firrtl.mux(%6, %c121_ui7, %18) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %20 = firrtl.mux(%5, %c109_ui7, %19) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %21 = firrtl.mux(%4, %c48_ui7, %20) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %22 = firrtl.mux(%3, %c126_ui7, %21) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %23 = firrtl.bundlecreate %io_binIn, %22 : (!firrtl.uint<4>, !firrtl.uint<7>) -> !firrtl.bundle<binIn: uint<4>, segOut: uint<7>>
      firrtl.matchingconnect %io, %23 : !firrtl.bundle<binIn: uint<4>, segOut: uint<7>>
    }
  }
}
