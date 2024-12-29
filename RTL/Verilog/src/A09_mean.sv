module mean(output  logic [31:0]    out     ,
            input   logic [31:0]    in      ,
            input   logic           reset   ,
            input   logic           clock   );
    logic [31:0]    reg0;
    logic [31:0]    reg1;
    logic [31:0]    reg2;
    logic [31:0]    reg3;
    logic [32:0]    sum0;
    logic [32:0]    sum1;
    logic [33:0]    sum;

    always_ff @(posedge clock)  if( reset)  begin   reg0 <= 0   ;
                                                    reg1 <= 0   ;
                                                    reg2 <= 0   ;
                                                    reg3 <= 0   ;
                                            end
                                    else    begin   reg0 <= in  ;
                                                    reg1 <= reg0;
                                                    reg2 <= reg1;
                                                    reg3 <= reg2;
                                            end
    assign #1 sum0 = reg0 + reg1;
    assign #1 sum1 = reg2 + reg3;
    assign #1 sum  = sum0 + sum1;
    assign    out  = sum[33:2]  ;
endmodule
