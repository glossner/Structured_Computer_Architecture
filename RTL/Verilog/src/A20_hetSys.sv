`include "0_DEFINES.vh"
module hetSys(  input   logic reset, clock  );
    logic [63:0]    h2a         ; // program & data
    logic           h2aProgWrite;
    logic           a2hProgFull ;
    logic           h2aDataWrite;
    logic           a2hDataFull ;
    logic [63:0]    a2h         ; // data
    logic           h2aRead     ;
    logic           a2hEmpty    ;
    logic           a2hInt      ; // interrupt
    logic           h2aInta     ; // interrupt acknowledge

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