module registerFile(
        output  logic   [31:0]  leftOp, rightOp                 ,
        input   logic   [31:0]  in                              ,
        input   logic   [4:0]   leftAddr, rightAddr, destAddr   ,
        input                   inta                            ,
        input   logic   [5:0]   opCode                          ,
        input   logic           clk                             );

    logic   [31:0]  rf[0:31];
    logic           we      ;

    assign  we = inta | (opCode[5:4] == 2'b11) |
                        (opCode[5:4] == 2'b01) |
                        (opCode == 6'b10_0111)  ;

    always_ff @(posedge clk)
        if (we) rf[inta ? 5'b11110 : destAddr] <= in;

    assign leftOp   = rf[inta ? 5'b11111 : leftAddr];
    assign rightOp  = rf[rightAddr]                 ;
endmodule