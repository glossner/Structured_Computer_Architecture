module outCLC(  input   logic [1:0]   state   ,
                input   logic         in      ,
                output  logic [1:0]   out     );

    always_comb begin: out_clc
        case(state)
            `init   : out = (in == `a) ? `recA : `fail   ;
            `runA   : out = (in == `a) ? `recA : `recB   ;
            `runB   : out = (in == `a) ? `fail : `recB   ;
            `error  : out = `fail                        ;
        endcase
    end: out_clc
endmodule