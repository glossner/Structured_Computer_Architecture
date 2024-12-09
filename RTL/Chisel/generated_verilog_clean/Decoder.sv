module Decoder(
  input  logic       io_in,
  output logic [1:0] io_out
);
  assign io_out[0] = ~io_in; // Output bit 0 active when io_in is 0
  assign io_out[1] = io_in;  // Output bit 1 active when io_in is 1
endmodule
