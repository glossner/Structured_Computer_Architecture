module eCell(   output  logic       out ,
                input   logic [7:0] func,
                input   logic       init, in0, in1, rst, clk );
    always_ff @(posedge clk) if (rst)   out <= init                 ;
                               else     out <= func[{in1, out, in0}];
endmodule