module {
  firrtl.circuit "Mux4" {
    firrtl.module private @Multiplexer(in %io_inputs_0: !firrtl.uint<8>, in %io_inputs_1: !firrtl.uint<8>, in %io_inputs_2: !firrtl.uint<8>, in %io_inputs_3: !firrtl.uint<8>, in %io_select: !firrtl.uint<2>, out %io_output: !firrtl.uint<8>) {
      %io_output_0 = firrtl.wire {name = "io_output"} : !firrtl.uint<8>
      firrtl.matchingconnect %io_output, %io_output_0 : !firrtl.uint<8>
      %0 = firrtl.multibit_mux %io_select, %io_inputs_3, %io_inputs_2, %io_inputs_1, %io_inputs_0 : !firrtl.uint<2>, !firrtl.uint<8>
      firrtl.matchingconnect %io_output_0, %0 : !firrtl.uint<8>
    }
    firrtl.module @Mux4(in %clock: !firrtl.clock, in %reset: !firrtl.uint<1>, in %io_inputs_0: !firrtl.uint<8>, in %io_inputs_1: !firrtl.uint<8>, in %io_inputs_2: !firrtl.uint<8>, in %io_inputs_3: !firrtl.uint<8>, in %io_select: !firrtl.uint<2>, out %io_output: !firrtl.uint<8>) attributes {convention = #firrtl<convention scalarized>} {
      %io_output_0 = firrtl.wire {name = "io_output"} : !firrtl.uint<8>
      firrtl.matchingconnect %io_output, %io_output_0 : !firrtl.uint<8>
      %io_output_mux_io_inputs_0, %io_output_mux_io_inputs_1, %io_output_mux_io_inputs_2, %io_output_mux_io_inputs_3, %io_output_mux_io_select, %io_output_mux_io_output = firrtl.instance io_output_mux @Multiplexer(in io_inputs_0: !firrtl.uint<8>, in io_inputs_1: !firrtl.uint<8>, in io_inputs_2: !firrtl.uint<8>, in io_inputs_3: !firrtl.uint<8>, in io_select: !firrtl.uint<2>, out io_output: !firrtl.uint<8>)
      %io_output_mux.io_output = firrtl.wire : !firrtl.uint<8>
      firrtl.matchingconnect %io_output_mux_io_inputs_0, %io_inputs_0 : !firrtl.uint<8>
      firrtl.matchingconnect %io_output_mux_io_inputs_1, %io_inputs_1 : !firrtl.uint<8>
      firrtl.matchingconnect %io_output_mux_io_inputs_2, %io_inputs_2 : !firrtl.uint<8>
      firrtl.matchingconnect %io_output_mux_io_inputs_3, %io_inputs_3 : !firrtl.uint<8>
      firrtl.matchingconnect %io_output_mux_io_select, %io_select : !firrtl.uint<2>
      firrtl.matchingconnect %io_output_mux.io_output, %io_output_mux_io_output : !firrtl.uint<8>
      firrtl.matchingconnect %io_output_0, %io_output_mux.io_output : !firrtl.uint<8>
    }
  }
}
