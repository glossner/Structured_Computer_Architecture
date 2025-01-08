`define n 1024
module cellAut 	
    (   output  logic [`n-1:0]  out ,
        input   logic [7:0]     func,
        input   logic [`n-1:0]  init,
        input   logic           rst , clk );
    genvar i;
    generate for (i=0; i<`n; i=i+1) begin: C
        eCell eCell(.out    (out[i]                         ),
                    .func   (func                           ),
                    .init   (init[i]                        ),
                    .in0    ((i==0) ? out[`n-1] : out[i-1]  ),
                    .in1    ((i==(`n-1)) ? out[0] : out[i+1]),
                    .rst    (rst                            ),
                    .clk    (clk                            ));
        end
    endgenerate
endmodule