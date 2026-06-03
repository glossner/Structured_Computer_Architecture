module threeInputFunctions( output  logic         out          ,
                            input   logic [7:0]   func         ,
                            input   logic         in0, in1, in2);
    assign  out = func[{in0, in1, in2}];
endmodule