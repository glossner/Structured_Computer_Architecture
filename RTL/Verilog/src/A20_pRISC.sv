module pRISC(   input   logic [63:0]    i2aProg     ,
                output  logic           a2iPread    ,
                input   logic           i2aPempty   ,
                input   logic [63:0]    i2aData     ,
                output  logic           a2iDread    ,
                input   logic           i2aDempty   ,
                output  logic [63:0]    a2i         ,
                output  logic           a2iDwrite   ,
                input   logic           i2aDfull    ,
                output  logic           a2hInt      ,
                input   logic           h2aInta     ,
                input   logic           reset, clock);
    logic [2:0]     dataTransCom;
    logic [`n+12:0] contr2array ;
    logic [`n-1:0]  red         ;

    controller controller(  i2aProg     ,
                            a2iPread    ,
                            i2aPempty   ,
                            dataTransCom,
                            a2iDread    ,
                            i2aDempty   ,
                            a2iDwrite   ,
                            i2aDfull    ,
                            contr2array ,
                            red         ,
                            a2hInt      ,
                            h2aInta     ,
                            reset, clock);
    array array(i2aData     ,
                a2i         ,
                dataTransCom,
                contr2array ,
                red         ,
                clock       );
endmodule