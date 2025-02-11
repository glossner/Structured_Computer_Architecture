module testRegLang;
    logic       in      ;
    logic [1:0] out     ;
    logic       reset   ;
    logic       clock   ;

    initial begin           clock = 0       ;
                forever #1  clock = ~clock  ;
            end
    initial begin       reset   = 1 ;
                        in      = `a;
                    #2  reset   = 0 ;
                    #10 in      = `b;
                    #6  in      = `a;
                    #8  $finish     ;
            end
    regLang dut(in      ,
                out     ,
                reset   ,
                clock   );
    initial begin
        $monitor("t=%0d  clock=%0d  reset=%0d  in=%0d  out=%0d",
                  $time, clock,     reset,     in,     out);
    end
    initial begin
        $dumpfile("dump.vcd");
        $dumpvars;
        #100 $finish;
    end
endmodule