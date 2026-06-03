module ALUp(output  logic   [31:0]  out     ,
            input   logic   [31:0]  leftIn  ,
                                    rightIn ,
            input   logic   [5:0]   opCode  );
    logic [32:0]    sum ;
    logic [32:0]    dif ;
    assign sum = leftIn + rightIn   ;
    assign dif = leftIn - rightIn   ;
    always_comb 
        case(opCode)
            `add    : out = sum[31:0]                       ;
            `sub    : out = dif[31:0]                       ;
            `addv   : out = sum[31:0]                       ;
            `mult   : out = leftIn * rightIn                ;
            `multv  : out = leftIn * rightIn                ;
            `addc   : out = sum[32]                         ;
            `subc   : out = dif[32]                         ;
            `addvc  : out = sum[32]                         ;
            `lsh    : out = leftIn >> 1                     ;
            `ash    : out = {leftIn[31], leftIn[31:1]}      ;
            `move   : out = leftIn                          ;
            `swap   : out = {leftIn[15:0], leftIn[31:16]}   ;
            `bwnot  : out = ~leftIn                         ;
            `bwand  : out = leftIn & rightIn                ;
            `bwor   : out = leftIn | rightIn                ;
            `bwxor  : out = leftIn ^ rightIn                ;
            `load   : out = rightIn                         ;
            `val    : out = rightIn                         ;
            default : out = leftIn                          ;
        endcase
endmodule