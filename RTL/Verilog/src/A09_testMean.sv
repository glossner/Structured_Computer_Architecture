module testMean;
    logic [31:0]    out     ;
    logic [31:0]    in      ;
    logic           reset  	;
    logic           clock   ;

    initial begin               clock = 0       ;
                    forever #3  clock = ~clock  ;
            end

    initial begin       reset = 1       ;
                        in    = 32'b1000;
                    #6  reset = 0       ;
                    #60 $finish         ;
            end

    mean dut(   out     ,
                in      ,
                reset   ,
                clock   );
endmodule