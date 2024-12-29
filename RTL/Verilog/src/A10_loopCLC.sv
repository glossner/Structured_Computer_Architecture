module loopCLC( input   logic         in          ,
                input   logic [1:0]   state       ,
                output  logic [1:0]   nextState   );

    always_comb begin: loop_clc
        case(state)
            `init   : nextState = (in == `a) ? `runA   : `error ;
            `runA   : nextState = (in == `a) ? `runA   : `runB  ;
            `runB   : nextState = (in == `b) ? `runB   : `error ;
            `error  : nextState = `error                        ;
        endcase
    end: loop_clc
endmodule