module elDistribute (   input   logic [31:0]    in      ,
                        output  logic [31:0]    out[0:1],
                        input   logic           clock   );
    logic [31:0] pipe;

    always_ff @(posedge clock)  pipe <= in  ;
    assign out[0] = pipe;
    assign out[1] = pipe;
endmodule