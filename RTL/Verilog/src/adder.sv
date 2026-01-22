// Licensed under CERN-OHL-S v2. See https://cern.ch/cern-ohl for details.
/**********************************************************************
File name:      adder.sv
Circuit name:   Adder
Description:    The module 'adder' has 2 4-bit inputs and one 4-bit 
		output. The circuit adds modulo 16; do not use or 
		provide carry signal
**********************************************************************/
module adder(   output logic [3:0] out ,    // 4-bit output
                input  logic [3:0] in0 ,    // 4-bit input
                input  logic [3:0] in1 );   // 4-bit input

    assign out = in0 + in1;
endmodule