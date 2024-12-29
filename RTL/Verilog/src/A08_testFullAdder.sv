module testFullAdder
    logic a, b, crIn, sum, crOut;

    hFullAdder dut(a, b, crIn, sum, crOut);

    initial begin       {a,b,crIn} = 3'b000 ;
                    #1  {a,b,crIn} = 3'b001 ;
                    #1  {a,b,crIn} = 3'b010 ;
                    #1  {a,b,crIn} = 3'b011 ;
                    #1  {a,b,crIn} = 3'b100 ;
                    #1  {a,b,crIn} = 3'b101 ;
                    #1  {a,b,crIn} = 3'b110 ;
                    #1  {a,b,crIn} = 3'b111 ;
                    #1  $stop               ;
            end
endmodule