module {
  firrtl.circuit "Decoder2to4" {
    firrtl.module @Decoder2to4(in %clock: !firrtl.clock, in %reset: !firrtl.uint<1>, in %io_input: !firrtl.uint<2>, out %io_output: !firrtl.uint<4>) attributes {convention = #firrtl<convention scalarized>} {
      %c1_ui1 = firrtl.constant 1 : !firrtl.uint<1>
      %io_output_0 = firrtl.wire {name = "io_output"} : !firrtl.uint<4>
      firrtl.matchingconnect %io_output, %io_output_0 : !firrtl.uint<4>
      %_io_output_T = firrtl.dshl %c1_ui1, %io_input {name = "_io_output_T"} : (!firrtl.uint<1>, !firrtl.uint<2>) -> !firrtl.uint<4>
      firrtl.matchingconnect %io_output_0, %_io_output_T : !firrtl.uint<4>
    }
  }
}
