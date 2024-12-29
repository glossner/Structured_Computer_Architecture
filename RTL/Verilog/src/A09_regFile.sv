module regFile( output  logic [31:0]    leftOut     ,
                output  logic [31:0]    righttOut   ,
                input   logic [31:0]    in          ,
                input   logic [4:0]     leftAddr    ,
                input   logic [4:0]     rightAddr   ,
                input   logic [4:0]     destAddr    ,
                input   logic           we          ,
                input   logic           clock       );
    logic [31:0]    mem[0:31]   ;

    always_ff @(posedge clock) if (we) mem[destAddr] <= in  ;
    
    assign leftOut  = mem[leftAddr] ;
    assign rightOut = mem[rightAddr];
endmodule
