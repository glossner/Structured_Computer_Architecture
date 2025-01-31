module distribute #(parameter p=16)(
        input   logic [31:0]    fromSQ          ,
        output  logic [31:0]    toCell[0:p-1]   ,
        input   logic           clock           );
    logic [31:0] out[0:1];

    genvar i;
    generate
     if (p == 2) begin
        elDistribute elDis( .in     (fromSQ     ),
                            .out    (toCell[0:1]),
                            .clock  (clock      ));
        end
      else  begin
        elDistribute elDis0(.in     (fromSQ),
                            .out    (out        ),
                            .clock  (clock      ));
        distribute #(.p(p/2))
            dis0(   .fromSQ (out[0]             ),
                    .toCell (toCell[0:p/2-1]    ),
                    .clock  (clock              )),
            dis1(   .fromSQ (out[1]             ),
                    .toCell (toCell[p/2:p-1]    ),
                    .clock  (clock              ));
           end
    endgenerate
endmodule