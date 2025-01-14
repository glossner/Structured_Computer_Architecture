module memorySystem(output  logic   [31:0]  instruction ,
                    input   logic   [9:0]   PC          ,
                    output  logic   [31:0]  dataIn      ,
                    input   logic   [31:0]  dataOut     ,
                    input   logic   [9:0]   dataAddr    ,
                    input   logic           dataRead    ,
                    input   logic           dataWrite   ,
                    input   logic           clk         );
    logic   [31:0]  dataMemory[0:1023]  ;
    logic   [31:0]  progMemory[0:1023]  ;

    assign instruction = progMemory[PC] ;

    always_ff @(posedge clk) begin
        if (dataWrite) dataMemory[dataAddr] <= dataOut  ;
    end

    assign dataIn = dataMemory[dataAddr];
endmodule