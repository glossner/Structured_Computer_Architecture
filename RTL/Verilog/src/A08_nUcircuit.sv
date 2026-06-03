module nU_circuit #(`include "parameter.sv")
    (   output  logic                     out     ,
        input   logic [(1'b1 << n)-1:0]   program ,
        input   logic [n-1:0]             data    );
    assign  out = program[data];
endmodule