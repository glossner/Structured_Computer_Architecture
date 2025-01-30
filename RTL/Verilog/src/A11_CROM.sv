module  CROM(   input   logic [7:0]     Func        ,
                input   logic           eq          ,
                input   logic           geq         ,
                input   logic           leq         ,
                input   logic           zero        ,
                input   logic           sgn         ,
                input   logic           oddity      ,
                input   logic           crOut       ,
                input   logic           Empty       ,
                input   logic           Full        ,
                output  logic [4:0]     leftAddr    ,
                output  logic [4:0]     rightAddr   ,
                output  logic           crIn        ,
                output  logic [4:0]     destAddr    ,
                output  logic           load        ,
                output  logic           we          ,
                output  logic [3:0]     aluFunc     ,
                output  logic           Read        ,
                output  logic           Write       ,
                input   logic           Init        ,
                input   logic           clock       );
    logic [7:0] stateReg    ;
    logic [7:0] nextState   ;
    logic       jmp         ;
    logic [3:0] flagSel     ;
    logic [9:0] flags       ;
    logic [1:0] nextSel     ;
    logic       test        ;

    assign flags = {Empty, Full,
                    eq, geq, leq, zero, sgn, oddity, crOut, jmp};
    assign test  = flags[flagSel]                               ;
    ROM epROM(  stateReg    ,
                nextState   ,
                jmp         ,
                flagSel     ,
                leftAddr    ,
                rightAddr   ,
                crIn        ,
                destAddr    ,
                load        ,
                we          ,
                aluFunc     ,
                Read        ,
                Write       );
    always_ff @(posedge clock)  casex({Init, test})
                                    2'b1x: stateReg <= Func         ;
                                    2'b01: stateReg <= nextState    ;
                                    2'b00: stateReg <= stateReg + 1 ;
                                endcase
endmodule