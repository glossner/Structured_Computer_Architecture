/********************************************************************
File name: DEFINES.vh
                MICROARCHITECTURE
********************************************************************/
// CONTROL
`define nop     6'b00_0000 // no operation: pc<=pc+1;
`define rjmp   	6'b00_0001 // relative jump: pc<=pc+v;
`define zbr    	6'b00_0010 // pc<=(rf[l]=0) ? pc+v:pc+1
`define nzbr   	6'b00_0011 // pc<=!(rf[l]=0) ? pc+v:pc+1
`define ret    	6'b00_0101 // return: pc<=rf[l][15:0];
`define halt   	6'b00_0110 // halt unitil interrupt
`define eint   	6'b00_1000 // set enable interrupt
`define dint   	6'b00_1001 // set disable interrupt
// ARITHMETIC & LOGIC, for these instructions: pc<=pc+1;
`define add     6'b11_0000 // rf[d]<=rf[l]+rf[r];
`define sub     6'b11_0001 // rf[d]<=rf[l]-rf[r];
`define addv    6'b11_0010 // rf[d]<=rf[l]+v;
`define mult    6'b11_0011 // rf[d]<=rf[l]*rf[r];
`define multv   6'b11_0100 // rf[d]<=rf[l]*v;
`define addc    6'b11_0101 // rf[d]<=(rf[l]+rf[r]}[32];
`define subc    6'b11_0110 // rf[d]<=(rf[l]-rf[r])[32];
`define addvc   6'b11_0111 // rf[d]<=(rf[l]+v)[32];
`define lsh     6'b11_1000 // rf[d]<=rf[l] >> 1;
`define ash     6'b11_1001 // rf[d]<=
                      //     <={rf[l][31],rf[l][31:1]};
`define move    6'b11_1010 // rf[d]<=rf[l];
`define swap    6'b11_1011 // rf[d]<=
                      // <={rf[l][15:0],rf[l][31:16]};
`define bwnot   6'b11_1100 // rf[d]<=~rf[l];
`define bwand   6'b11_1101 // rf[d]<=rf[l]&rf[r];
`define bwor    6'b11_1110 // rf[d]<=rf[l]|rf[r];
`define bwxor   6'b11_1111 // rf[d]<=rf[l]^rf[r];
// MEMORY, for these instructions: pc=pc+1;
`define read   	6'b10_0000 // read from dataMemory[rf[l]];
`define load   	6'b10_0111 // rf[d]<=dataOut;
`define store  	6'b10_1000 // dataMemory[rf[l]]<=rf[r];
`define val    	6'b01_0111 // rf[d]<={{16*{v[15]}},v};