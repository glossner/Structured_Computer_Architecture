module pipelinedThreeAdder( output  logic [3:0] out     ,
                            input   logic [3:0] in0     ,
                            input   logic [3:0] in1     ,
                            input   logic [3:0] in2     ,
                            input   logic       clock   );
    logic [3:0] syncReg ;
    logic [3:0] pipeReg ;
    logic [3:0] sum     ;

    adder   inAdder(    .out(sum    ),
                        .in0(in0    ),
                        .in1(in1    )),
            outAdder(   .out(out    ),
                        .in0(syncReg),
                        .in1(pipeReg));
    always_ff @(posedge clock)  begin   syncReg <= in2  ;
                                        pipeReg <= sum  ;
                                end
endmodule