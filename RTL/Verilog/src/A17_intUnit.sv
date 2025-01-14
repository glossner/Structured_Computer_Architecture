module intUnit( output  logic           we          ,
                output  logic   [4:0]   destAddr    ,
                output  logic   [31:0]  result      ,
                output  logic   [4:0]   leftAddr    ,
                input   logic           we3         ,
                input   logic   [4:0]   destAddr3   ,
                input   logic   [31:0]  result3     ,
                input   logic   [4:0]   leftAddr1   ,
                input   logic   [9:0]   pc1         ,
                input   logic           inta        );

    assign we       = inta ? 1'b1           : we3       ;
    assign destAddr = inta ? 5'b11110       : destAddr3 ;
    assign result   = inta ? {21'b0, pc1}   : result3   ;
    assign leftAddr = inta ? 5'b11111       : leftAddr1 ;
endmodule