`include "defines.vh"
module regLang( input   logic       in      ,
                output  logic [1:0] out     ,
                input   logic       reset   ,
                                    clock   );
    logic [1:0]   state, nextState  ;

    always_ff @(posedge clock) begin: state_register
        if (reset) 	state   <= `init    ;
         else       state   <= nextState;
    end: state_register
    loopCLC lC( in          ,
                state       ,
                nextState   );
    outCLC oC( 	state   ,
                in      ,
                out     );
endmodule