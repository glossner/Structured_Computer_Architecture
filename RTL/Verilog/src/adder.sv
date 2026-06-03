// Licensed under CERN-OHL-S v2. See https://cern.ch/cern-ohl for details.
module adder(   output logic [3:0] out ,    // 4-bit output
                input  logic [3:0] in0 ,    // 4-bit input
                input  logic [3:0] in1 );   // 4-bit input

    assign out = in0 + in1;
endmodule