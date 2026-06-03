`include "defineParamSyncPipeRAM.vh"
module syncPipeRAM( output  logic [`wordSize-1:0]       out      ,
                    input   logic [`wordSize-1:0]       in       ,
                    input   logic [`addressSize-1:0]    addr     ,
                    input   logic                       we, clock);
    logic [`wordSize-1:0]    mem[0:2**{`addressSize}-1]   ;
    always_ff @(posedge clock)  
	begin if (we)   mem[addr] <= in     ;
                        out  <= mem[addr]   ;
        end
endmodule