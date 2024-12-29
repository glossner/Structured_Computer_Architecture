`include "counterDefines.vh"
module testCounter;
    logic [3:0] out ;
    logic [2:0] op  ;
    logic [3:0] in  ;
    logic       clk ;

    initial begin           clk = 0     ;
                forever #1  clk = ~clk  ;
            end

    initial begin       op = `reset ;
                        in = 4'b1010;
                    #2  op = `up    ;
                    #6  op = `load  ;
                    #2  op = `up    ;
                    #10 op = `down  ;
                    #16 $finish     ;
            end

    counter dut(out ,
                op  ,
                in  ,
                clk );
endmodule