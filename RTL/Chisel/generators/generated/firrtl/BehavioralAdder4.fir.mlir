module {
  firrtl.circuit "BehavioralAdder4" {
    firrtl.module private @BehavioralAdder(in %io_a: !firrtl.uint<4>, in %io_b: !firrtl.uint<4>, out %io_sum: !firrtl.uint<4>) {
      %io_sum_0 = firrtl.wire {name = "io_sum"} : !firrtl.uint<4>
      firrtl.matchingconnect %io_sum, %io_sum_0 : !firrtl.uint<4>
      %_io_sum_T = firrtl.add %io_a, %io_b {name = "_io_sum_T"} : (!firrtl.uint<4>, !firrtl.uint<4>) -> !firrtl.uint<5>
      %_io_sum_T_2 = firrtl.bits %_io_sum_T 3 to 0 {name = "_io_sum_T_2"} : (!firrtl.uint<5>) -> !firrtl.uint<4>
      firrtl.matchingconnect %io_sum_0, %_io_sum_T_2 : !firrtl.uint<4>
    }
    firrtl.module @BehavioralAdder4(in %clock: !firrtl.clock, in %reset: !firrtl.uint<1>, in %io_a: !firrtl.uint<4>, in %io_b: !firrtl.uint<4>, out %io_sum: !firrtl.uint<4>) attributes {convention = #firrtl<convention scalarized>} {
      %io_sum_0 = firrtl.wire {name = "io_sum"} : !firrtl.uint<4>
      firrtl.matchingconnect %io_sum, %io_sum_0 : !firrtl.uint<4>
      %io_sum_adder_io_a, %io_sum_adder_io_b, %io_sum_adder_io_sum = firrtl.instance io_sum_adder @BehavioralAdder(in io_a: !firrtl.uint<4>, in io_b: !firrtl.uint<4>, out io_sum: !firrtl.uint<4>)
      %io_sum_adder.io_sum = firrtl.wire : !firrtl.uint<4>
      firrtl.matchingconnect %io_sum_adder_io_a, %io_a : !firrtl.uint<4>
      firrtl.matchingconnect %io_sum_adder_io_b, %io_b : !firrtl.uint<4>
      firrtl.matchingconnect %io_sum_adder.io_sum, %io_sum_adder_io_sum : !firrtl.uint<4>
      firrtl.matchingconnect %io_sum_0, %io_sum_adder.io_sum : !firrtl.uint<4>
    }
  }
}
