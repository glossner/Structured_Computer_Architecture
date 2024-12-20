module {
  firrtl.circuit "SevenSegmentDisplay" {
    firrtl.module @SevenSegmentDisplay(in %clock: !firrtl.clock, in %reset: !firrtl.uint<1>, in %io_binIn: !firrtl.uint<4>, out %io_segOut: !firrtl.uint<7>) attributes {convention = #firrtl<convention scalarized>} {
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
      %io_segOut_0 = firrtl.wire {name = "io_segOut"} : !firrtl.uint<7>
      firrtl.matchingconnect %io_segOut, %io_segOut_0 : !firrtl.uint<7>
      %0 = firrtl.orr %io_binIn : (!firrtl.uint<4>) -> !firrtl.uint<1>
      %1 = firrtl.not %0 : (!firrtl.uint<1>) -> !firrtl.uint<1>
      %2 = firrtl.eq %io_binIn, %c1_ui1 : (!firrtl.uint<4>, !firrtl.uint<1>) -> !firrtl.uint<1>
      %3 = firrtl.eq %io_binIn, %c2_ui2 : (!firrtl.uint<4>, !firrtl.uint<2>) -> !firrtl.uint<1>
      %4 = firrtl.eq %io_binIn, %c3_ui2 : (!firrtl.uint<4>, !firrtl.uint<2>) -> !firrtl.uint<1>
      %5 = firrtl.eq %io_binIn, %c4_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %6 = firrtl.eq %io_binIn, %c5_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %7 = firrtl.eq %io_binIn, %c6_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %8 = firrtl.eq %io_binIn, %c7_ui3 : (!firrtl.uint<4>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %9 = firrtl.eq %io_binIn, %c8_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %10 = firrtl.eq %io_binIn, %c9_ui4 : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<1>
      %11 = firrtl.mux(%10, %c123_ui7, %c0_ui7) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %12 = firrtl.mux(%9, %c127_ui7, %11) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %13 = firrtl.mux(%8, %c112_ui7, %12) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %14 = firrtl.mux(%7, %c95_ui7, %13) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %15 = firrtl.mux(%6, %c91_ui7, %14) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %16 = firrtl.mux(%5, %c51_ui7, %15) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %17 = firrtl.mux(%4, %c121_ui7, %16) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %18 = firrtl.mux(%3, %c109_ui7, %17) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %19 = firrtl.mux(%2, %c48_ui7, %18) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      %20 = firrtl.mux(%1, %c126_ui7, %19) : (!firrtl.uint<1>, !firrtl.uint<7>, !firrtl.uint<7>) -> !firrtl.uint<7>
      firrtl.matchingconnect %io_segOut_0, %20 : !firrtl.uint<7>
    }
  }
}
