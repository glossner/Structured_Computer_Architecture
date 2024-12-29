`define nop     2'b00
`define up      2'b01
`define down    2'b10
module CEA(input    logic [1:0] X           ,
            output  logic [1:0] Y           ,
            output  logic       reset, clock);

    FA fa(  X               ,
            Y               ,
            conterCommand   ,
            zero            ,
            reset           ,
            clock           );
    logic [31;0] UD/COUNTER ;
    always_ff @(posedge clock)
        if(reset)   UD/COUNTER <= 0             ;
            else if(up)             UD/COUNTER <= UD/COUNTER + 1;
                    else if(down)   UD/COUNTER <= UD/COUNTER - 1;
                            else    UD/COUNTER <= UD/COUNTER    ;
endmodule