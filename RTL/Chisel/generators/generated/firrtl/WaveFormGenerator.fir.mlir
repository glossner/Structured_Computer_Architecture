module {
  firrtl.circuit "WaveFormGenerator" {
    firrtl.module @WaveFormGenerator(in %clock: !firrtl.clock, in %reset: !firrtl.uint<1>, out %io_randomWave: !firrtl.uint<1>, out %io_clockWave: !firrtl.uint<1>) attributes {convention = #firrtl<convention scalarized>} {
      %c0_ui1 = firrtl.constant 0 : !firrtl.uint<1> {name = "randomWaveSeq_0"}
      %c1_ui1 = firrtl.constant 1 : !firrtl.uint<1> {name = "randomWaveSeq_1"}
      %c4_ui3 = firrtl.constant 4 : !firrtl.uint<3>
      %c0_ui3 = firrtl.constant 0 : !firrtl.uint<3>
      %io_randomWave_0 = firrtl.wire {name = "io_randomWave"} : !firrtl.uint<1>
      %io_clockWave_1 = firrtl.wire {name = "io_clockWave"} : !firrtl.uint<1>
      firrtl.matchingconnect %io_randomWave, %io_randomWave_0 : !firrtl.uint<1>
      firrtl.matchingconnect %io_clockWave, %io_clockWave_1 : !firrtl.uint<1>
      %randomIndex = firrtl.regreset %clock, %reset, %c0_ui3 {firrtl.random_init_start = 0 : ui64} : !firrtl.clock, !firrtl.uint<1>, !firrtl.uint<3>, !firrtl.uint<3>
      %0 = firrtl.multibit_mux %randomIndex, %c0_ui1, %c1_ui1, %c0_ui1, %c1_ui1, %c0_ui1 : !firrtl.uint<3>, !firrtl.uint<1>
      firrtl.matchingconnect %io_randomWave_0, %0 : !firrtl.uint<1>
      %_randomIndex_T = firrtl.eq %randomIndex, %c4_ui3 {name = "_randomIndex_T"} : (!firrtl.uint<3>, !firrtl.uint<3>) -> !firrtl.uint<1>
      %_randomIndex_T_1 = firrtl.add %randomIndex, %c1_ui1 {name = "_randomIndex_T_1"} : (!firrtl.uint<3>, !firrtl.uint<1>) -> !firrtl.uint<4>
      %_randomIndex_T_2 = firrtl.bits %_randomIndex_T_1 2 to 0 {name = "_randomIndex_T_2"} : (!firrtl.uint<4>) -> !firrtl.uint<3>
      %_randomIndex_T_3 = firrtl.mux(%_randomIndex_T, %c0_ui3, %_randomIndex_T_2) {name = "_randomIndex_T_3"} : (!firrtl.uint<1>, !firrtl.uint<3>, !firrtl.uint<3>) -> !firrtl.uint<3>
      firrtl.matchingconnect %randomIndex, %_randomIndex_T_3 : !firrtl.uint<3>
      %clockToggle = firrtl.regreset %clock, %reset, %c0_ui1 {firrtl.random_init_start = 3 : ui64} : !firrtl.clock, !firrtl.uint<1>, !firrtl.uint<1>, !firrtl.uint<1>
      %_clockToggle_T = firrtl.not %clockToggle {name = "_clockToggle_T"} : (!firrtl.uint<1>) -> !firrtl.uint<1>
      firrtl.matchingconnect %clockToggle, %_clockToggle_T : !firrtl.uint<1>
      firrtl.matchingconnect %io_clockWave_1, %clockToggle : !firrtl.uint<1>
    }
  }
}
