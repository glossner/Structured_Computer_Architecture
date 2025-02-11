module simulator;
    `include "codeGenerator.v"
    reg [22:0]  mem[0:31]   ; // ROM
    integer i;
    initial
    for (i=0; i<16; i=i+1)
        $display
        ("ROM[%0d] \t = mode:%b addr:%b test:%b com1:%b com2:%b com3:%b val:%d",
        i,
        mem[i][22:21],
        mem[i][20:16],
        mem[i][15],
        mem[i][14:13],
        mem[i][12:10],
        mem[i][9:8],
        mem[i][7:0])    ;
endmodule