// Licensed under CERN-OHL-S v2. See https://cern.ch/cern-ohl for details.
module dcd( output  logic [15:0]    dcdOut  ,
            input   logic [3:0]     dcdIn   );

     assign dcdOut = 1 << dcdIn ;
endmodule