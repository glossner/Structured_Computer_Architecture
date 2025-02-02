module accelerator( input   logic           h2iPwrite   ,
                    output  logic           i2hPfull    ,
                    input   logic [63:0]    h2i         ,
                    input   logic           h2iDwrite   ,
                    output  logic           i2hDfull    ,
                    output  logic [63:0]    i2h         ,
                    input   logic           h2iDread    ,
                    output  logic           i2hDempty   ,
                    output  logic           a2hInt      ,
                    input   logic           h2aInta     ,
                    input   logic           reset       ,
                    input   logic           clock       );
    logic [63:0]    i2aData ;
    logic [63:0]    a2i     ;
    logic [63:0]    i2aProg ;

    interfaces INTERFACES(  h2iPwrite   ,
                            i2hPfull    ,
                            h2i         ,
                            h2iDwrite   ,
                            i2hDfull    ,
                            i2h         ,
                            h2iDread    ,
                            i2hDempty   ,
                            i2aProg     ,
                            a2iPread    ,
                            i2aPempty   ,
                            i2aData     ,
                            a2iDread    ,
                            i2aDempty   ,
                            a2i         ,
                            a2iDwrite   ,
                            i2aDfull    ,
                            reset       ,
                            clock       );

    pRISC pRISC(i2aProg     ,
                a2iPread    ,
                i2aPempty   ,
                i2aData     ,
                a2iDread    ,
                i2aDempty   ,
                a2i         ,
                a2iDwrite   ,
                i2aDfull    ,
                a2hInt      ,
                h2aInta     ,
                reset       ,
                clock       );

endmodule
