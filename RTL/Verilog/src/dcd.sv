/*************************************************************************
File name:      dcd.sv
Circuit name:   4-input decoder
Description:
*************************************************************************/
module dcd( output  logic [15:0]    dcdOut  ,
            input   logic [3:0]     dcdIn   );

     assign dcdOut = 1 << dcdIn ;
endmodule