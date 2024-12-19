module accelerator( input   logic [63:0]    int2accProg ,
                    output  logic           i2aPread    ,
                    input   logic           i2aPempty   ,
                    input   logic [63:0]    int2accData ,
                    output  logic           i2aDread    ,
                    input   logic           i2aDempty   ,
                    output  logic [63:0]    acc2intData ,
                    output  logic           a2iDwrite   ,
                    input   logic           a2iDfull    ,
                    output  logic           acc2hostInt ,
                    input   logic           host2accInta,
                    input   logic           reset, clock);

    logic [2:0]     dataTransCom;
    logic [`n+12:0] contr2array ;
    logic [`n-1:0]  red         ;

    controller controller(  int2accProg ,
                            i2aPread    ,
                            i2aPempty   ,
                            dataTransCom,
                            i2aDread    ,
                            i2aDempty   ,
                            a2iDwrite   ,
                            a2iDfull    ,
                            contr2array ,
                            red         ,
                            acc2hostInt ,
                            host2accInta,
                            reset, clock);

    array array(int2accData ,
                acc2intData ,
                dataTransCom,
                contr2array ,
                red         ,
                clock       );
endmodule
