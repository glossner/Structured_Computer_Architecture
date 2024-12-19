`include "0_DEFINES.vh"
module hetSys(  input   logic reset, clock  );
    // HOST-INTERFACE CONECTIONS
    logic           h2iPwrite   ; // write program to program FIFO
    logic           i2hPfull    ; // program FIFO full
    logic [63:0]    host2int    ; // program or data issued by host
    logic           h2iDwrite   ; // write data to input data FIFO
    logic           i2hDfull    ; // input data FIFO full
    logic [63:0]    int2hostData; // data get from output data FIFO
    logic           i2hDread    ; // data read by host
    logic           i2hDempty   ; // output data FIFO is empty
    // INTERFACE-ACCELERATOR CONNECTIONS
    logic [63:0]    int2accProg ; // program get from program FIFO
    logic           i2aPread    ; // read program from program FIFO
    logic           i2aPempty   ; // program FIFO empty
    logic [63:0]    int2accData ; // data get from input data FIFO  
    logic           i2aDread    ; // data read from input data FIFO
    logic           i2aDempty   ; // input data FIFO is empty
    logic [63:0]    acc2intData ; // data sent to output data FIFO
    logic           a2iDwrite   ; // write data to output data FIFO
    logic           a2iDfull    ; // output data FIFO is full
    // HOST-ACCELERATOR CONNECTIONS
    logic           acc2hostInt ; // interrupt send to host
    logic           host2accInta; // interrupt acknowledge from host

    host HOST(  h2iPwrite       ,
                i2hPfull        ,
                host2int        ,
                h2iDwrite       ,
                i2hDfull        ,
                int2hostData    ,
                i2hDread        ,
                i2hDempty       ,
                acc2hostInt     ,
                host2accInta    ,
                reset, clock    );

    interfaces INTERFACES(  h2iPwrite   ,
                            h2iPfull    ,
                            host2int    ,
                            h2iDwrite   ,
                            i2hDfull    ,
                            int2hostData,
                            i2hDread    ,
                            i2hDempty   ,
                            int2accProg ,
                            i2aPread    ,
                            i2aPempty   ,
                            int2accData ,
                            i2aDread    ,
                            i2aDempty   ,
                            acc2intData ,
                            a2iDwrite   ,
                            a2iDfull    ,
                            reset, clock);

    accelerator ACCELERATOR(int2accProg ,
                            i2aPread    ,
                            i2aPempty   ,
                            int2accData ,
                            i2aDread    ,
                            i2aDempty   ,
                            acc2intData ,
                            a2iDwrite   ,
                            a2iDfull    ,
                            acc2hostInt ,
                            host2accInta,
                            reset, clock);
endmodule