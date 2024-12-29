module outCLC(  input       [1:0]   state   ,
                input               in      ,
                output  bit [1:0]   out     );

    always_comb begin: out_clc
        case(state)
            `init   : out = (in == `a) ? `recA : `fail   ;
            `runA   : out = (in == `a) ? `recA : `recB   ;
            `runB   : out = (in == `a) ? `fail : `recB   ;
            `error  : out = `fail                        ;
        endcase
    end: out_clc
endmodule