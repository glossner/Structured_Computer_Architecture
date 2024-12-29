module syncRAM( output  logic [31:0]    out     ,
                input   logic [31:0]    in      ,
                input   logic [12:0]    addr    ,
                input   logic           we      ,
                input   logic           clock   );
    logic [31:0]    mem[0:8191]   ;

    always_ff @(posedge clock) if (we) mem[addr] <= in  ;
    assign out  = mem[addr] ;
endmodule