module {
  firrtl.circuit "BehavioralAdderSubtractor64" {
    firrtl.module @BehavioralAdderSubtractor64(in %clock: !firrtl.clock, in %reset: !firrtl.uint<1>, in %io_a: !firrtl.uint<64>, in %io_b: !firrtl.uint<64>, in %io_subtract: !firrtl.uint<1>, out %io_result: !firrtl.uint<64>) attributes {convention = #firrtl<convention scalarized>} {
      %c1_ui1 = firrtl.constant 1 : !firrtl.uint<1>
      %io_result_0 = firrtl.wire {name = "io_result"} : !firrtl.uint<64>
      firrtl.matchingconnect %io_result, %io_result_0 : !firrtl.uint<64>
      %_bAdjusted_T_1 = firrtl.not %io_b {name = "_bAdjusted_T_1"} : (!firrtl.uint<64>) -> !firrtl.uint<64>
      %_bAdjusted_T_2 = firrtl.add %_bAdjusted_T_1, %c1_ui1 {name = "_bAdjusted_T_2"} : (!firrtl.uint<64>, !firrtl.uint<1>) -> !firrtl.uint<65>
      %_bAdjusted_T_3 = firrtl.bits %_bAdjusted_T_2 63 to 0 {name = "_bAdjusted_T_3"} : (!firrtl.uint<65>) -> !firrtl.uint<64>
      %bAdjusted = firrtl.mux(%io_subtract, %_bAdjusted_T_3, %io_b) {name = "bAdjusted"} : (!firrtl.uint<1>, !firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<64>
      %_fullResult_T = firrtl.add %io_a, %bAdjusted {name = "_fullResult_T"} : (!firrtl.uint<64>, !firrtl.uint<64>) -> !firrtl.uint<65>
      %fullResult = firrtl.bits %_fullResult_T 63 to 0 {name = "fullResult"} : (!firrtl.uint<65>) -> !firrtl.uint<64>
      firrtl.matchingconnect %io_result_0, %fullResult : !firrtl.uint<64>
    }
  }
}
