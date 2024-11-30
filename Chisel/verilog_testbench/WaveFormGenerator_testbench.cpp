#include "VWaveFormGenerator.h" // Verilated top module
#include "verilated.h"
#include "verilated_vcd_c.h" // For waveform tracing (optional)

int main(int argc, char **argv) {
    Verilated::commandArgs(argc, argv);

    // Instantiate the DUT (Device Under Test)
    VWaveFormGenerator* dut = new VWaveFormGenerator;

    // Enable waveform tracing
    Verilated::traceEverOn(true);
    VerilatedVcdC* trace = new VerilatedVcdC;
    dut->trace(trace, 99); // Trace 99 levels of hierarchy
    trace->open("waveform.vcd");

    // Simulate for 20 clock cycles
    for (int cycle = 0; cycle < 20; ++cycle) {
        // Toggle clock
        dut->clock = !dut->clock;

        // Evaluate the DUT
        dut->eval();

        // Dump waveform data to the VCD file
        trace->dump(cycle);

        // Print output signals
        if (dut->clock) { // Print on the rising edge of the clock
            printf("Cycle %d: randomWave=%d, clockWave=%d\n", cycle / 2, dut->io_randomWave, dut->io_clockWave);
        }
    }

    // Clean up
    trace->close();
    delete trace;
    delete dut;
    return 0;
}
