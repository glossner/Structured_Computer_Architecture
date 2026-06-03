`include "0_DEFINES.vh"
module hetSys(  input   logic reset, clock  );
  logic [63:0] h2a; // program & data
  logic        h2aProgWrite, a2hProgFull, 
               h2aDataWrite, a2hDataFull;
  logic [63:0] a2h; // data
  logic        h2aRead, a2hEmpty, a2hInt, h2aInta;
    host HOST(  h2a         ,
                h2aProgWrite,
                a2hProgFull ,
                h2aDataWrite,
                a2hDataFull ,
                a2h         ,
                h2aDataRead ,
                a2hDataEmpty,
                a2hInt      ,
                h2aInta     ,
                reset       ,
                clock       );
    accelerator ACCELERATOR(h2a         ,
                            h2aProgWrite,
                            a2hProgFull ,
                            h2aDataWrite,
                            a2hDataFull ,
                            a2h         ,
                            h2aDataRead ,
                            a2hDataEmpty,
                            a2hInt      ,
                            h2aInta     ,
                            reset       ,
                            clock       );
endmodule