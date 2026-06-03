module intUnit( output  logic           we          ,
                output  logic   [4:0]   destAddr    ,
                output  logic   [31:0]  result      ,
                output  logic   [4:0]   leftAddr    ,
                input   logic           we4         ,
                input   logic   [4:0]   destAddr4   ,
                input   logic   [31:0]  result4     ,
                input   logic   [4:0]   leftAddr1   ,
                input   logic   [9:0]   pc1         ,
                input   logic           inta        );
    assign we       = inta ? 1'b1           : we4       ;
    assign destAddr = inta ? 5'b11110       : destAddr4 ;
    assign result   = inta ? {21'b0, pc1}   : result4   ;
    assign leftAddr = inta ? 5'b11111       : leftAddr1 ;
endmodule