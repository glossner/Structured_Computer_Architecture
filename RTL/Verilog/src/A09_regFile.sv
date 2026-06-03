module regFile(output  logic [31:0] leftOut, righttOut           ,
               input   logic [31:0] in                           ,
               input   logic [4:0]  leftAddr, rightAddr, destAddr,
               input   logic        we, clock                    );
    logic [31:0]    mem[0:31]   ;
    always_ff @(posedge clock) if (we) mem[destAddr] <= in  ;  
    assign leftOut  = mem[leftAddr] ;
    assign rightOut = mem[rightAddr];
endmodule
