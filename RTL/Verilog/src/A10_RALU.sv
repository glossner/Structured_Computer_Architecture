module RALU(output  logic [31:0]    leftOut     ,
            output  logic [31:0]    righttOut   ,
            output  logic           crOut       ,
            input   logic [4:0]     leftAddr    ,
            input   logic [4:0]     rightAddr   ,
            input   logic [4:0]     destAddr    ,
            input   logic [31:0]    in          ,
            input   logic           load        ,
            input   logic           we          ,
            input   logic [3:0]     func        ,
            input   logic           clock       );
    logic [31:0]    out  ;

    regFile rf( .leftOut    (leftOut    ),
                .righttOut  (righttOut  ),
                .in         (out        ),
                .leftAddr   (leftAddr   ),
                .rightAddr  (rightAddr  ),
                .destAddr   (destAddr   ),
                .we         (we         ),
                .clock      (clock      ));

    ALU alu(.func   (func               ),
            .left   (load ? in : leftOut),
            .right  (righttOut          ),
            .crOut  (crOut              ),
            .out    (out                ));
endmodule