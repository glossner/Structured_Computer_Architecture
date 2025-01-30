module RALU(output  logic [31:0]    leftOut     ,
            output  logic           crOut       ,
            output  logic           eq          ,
            output  logic           geq         ,
            output  logic           leq         ,
            output  logic           zero        ,
            input   logic [4:0]     leftAddr    ,
            input   logic [4:0]     rightAddr   ,
            input   logic           crIn        ,
            input   logic [4:0]     destAddr    ,
            input   logic [31:0]    in          ,
            input   logic           load        ,
            input   logic           we          ,
            input   logic [3:0]     aluFunc      ,
            input   logic           clock       );
    logic [31:0]    out     ;
    logic [31:0]    rightOut;

    regFile rf( .leftOut    (leftOut    ),
                .rightOut   (rightOut   ),
                .in         (out        ),
                .leftAddr   (leftAddr   ),
                .rightAddr  (rightAddr  ),
                .destAddr   (destAddr   ),
                .we         (we         ),
                .clock      (clock      ));
    ALU alu(.func   (aluFunc            ),
            .left   (load ? in : leftOut),
            .right  (rightOut           ),
            .crIn   (crIn               ),
            .crOut  (crOut              ),
            .out    (out                ));
    assign eq   = leftOut == rightOut   ;
    assign geq  = leftOut >= rightOut   ;
    assign leq  = leftOut <= rightOut   ;
    assign zero = leftOut == 0          ;
endmodule