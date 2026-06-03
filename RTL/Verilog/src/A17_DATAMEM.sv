module DATAMEM( input   logic [31:0]    result3, instruction3,
                input   logic           we                   ,
                input   logic           memSel               ,
                input   logic [31:0]    dataIn               ,
                output  logic [31:0]    result4, instruction4,
                output  logic           we4                  ,
                input   logic           clk                  );
    always_ff @(posedge clk)
        begin   result4         <= memSel ? dataIn : result3;
                instruction4    <= instruction3             ;
                we4             <= we                       ;
        end
endmodule