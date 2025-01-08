module ALU( output  logic   [31:0]  out                     ,
            input   logic   [31:0]  leftIn, rightIn, value  ,
            input   logic   [5:0]   opCode                  );

    always_comb
        case(opCode)
            `add    : out = leftIn + rightIn              ;
            `sub    : out = leftIn - rightIn              ;
            `addv   : out = leftIn + value                ;
            `mult   : out = leftIn * rightIn              ;
            `multv  : out = leftIn * value                ;
            `addc   : out = (leftIn + rightIn) > 2**32 - 1;
            `subc   : out = (leftIn - rightIn) > 2**32 - 1;
            `addvc  : out = (leftIn + value) > 2**32 - 1  ;
            `lsh    : out = leftIn >> 1                   ;
            `ash    : out = {leftIn[31], leftIn[31:1]}    ;
            `move   : out = leftIn                        ;
            `swap   : out = {leftIn[15:0], leftIn[31:16]} ;
            `bwnot  : out = ~leftIn                       ;
            `bwand  : out = leftIn & rightIn              ;
            `bwor   : out = leftIn | rightIn              ;
            `bwxor  : out = leftIn ^ rightIn              ;
            default : out = leftIn                        ;
        endcase
endmodule