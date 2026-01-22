// Licensed under CERN-OHL-S v2. See https://cern.ch/cern-ohl for details.
/**********************************************************************
File name:      dcdParameterized.sv
Circuit name:   parameterized decoder
Description:
**********************************************************************/

module Decoder #(
    parameter INPUT_WIDTH = 4,                   // # of input bits
    parameter OUTPUT_WIDTH = (1 << INPUT_WIDTH)) // # of output bits 
    (input  logic [INPUT_WIDTH-1:0] in,          // Binary input
     output logic [OUTPUT_WIDTH-1:0] out );      // Decoded outputs

    always_comb begin
        out = 1'b1 << in; // Shift the bit based on the input value
    end
endmodule