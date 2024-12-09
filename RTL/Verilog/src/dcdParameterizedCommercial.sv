module Decoder #(parameter WIDTH = 2)(
    input  logic [WIDTH-1:0] in,
    output logic [(1<<WIDTH)-1:0] out
);
    // Compile-time check
    generate
        if (WIDTH < 2) begin
            initial $error("WIDTH parameter must be at least 2. Current value: %0d", WIDTH);
        end
    endgenerate

    assign out = 1 << in;

endmodule
