module waveform1;
    logic [1:0] W;
    parameter   a = 2'b01,
                b = 2'b10,
                c = 2'b11;

    initial begin       W = a   ;
                    #6  W = c  	;
                    #8  W = b  	;
                    #2  W = a  	;
                    #4  $stop   ;
            end
endmodule