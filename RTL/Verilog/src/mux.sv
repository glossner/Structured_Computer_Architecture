// Licensed under CERN-OHL-S v2. See https://cern.ch/cern-ohl for details.
module mux( output  logic           muxOut  ,
            input   logic [15:0]    muxIn   ,
            input   logic [3:0]     muxSel  );

     assign muxOut = muxIn[muxSel] ;
endmodule