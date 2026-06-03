// Licensed under CERN-OHL-S v2. See https://cern.ch/cern-ohl for details.
module dMux(output  logic [15:0]    dMuxOut     ,
            input   logic [3:0]     dMuxIn      ,
            input   logic           dMuxEnable  );

     assign dMuxOut = dMuxEnable << dMuxIn ;
endmodule