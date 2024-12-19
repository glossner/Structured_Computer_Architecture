
// ADDRESSING MODE DEFINING THE RIGHT OPERAND OP
`define	imm 3'b000 // op: {{8{val[23]}}, val}
`define dir 3'b001 // op: mem[val]
`define rel 3'b010 // op: mem[addr+val]
`define rei 3'b011 // op: mem[addr+val]; addr = addr+val
`define cim 3'b100 // op: coOp
`define cdr 3'b101 // op: mem[coOp]
`define	crl 3'b110 // op: mem[addr+coOp]
`define cri 3'b111 // op: mem[addr+coOp]; addr = addr+coOp	

// FUNCTIONAL INSTRUCTIONS
`define add     5'b00000 // {cr[i],acc[i]} <=  acc[i] + op
`define addcr   5'b00001 // {cr[i],acc[i]} <=  acc[i] + op + cr
`define sub     5'b00010 // {cr[i],acc[i]} <=  acc[i] - op
`define subcr   5'b00011 // {cr[i],acc[i]} <=  acc[i] - op - cr
`define mult    5'b00100 // {cr[i],acc[i]} <= {cr[i], acc[i] * op}
`define bwand   5'b00101 // {cr[i],acc[i]} <= {cr[i], acc[i] & op}
`define bwor    5'b00110 // {cr[i],acc[i]} <= {cr[i], acc[i] | op}
`define bwxor   5'b00111 // {cr[i],acc[i]} <= {cr[i], acc[i] ^ op}
// DATA MOVE INSTRUCTIONS
`define load    5'b01000 // {cr[i],acc[i]} <= {cr[i], op}
`define store   5'b01001 // aMem[address[i]] <= aAcc
`define insval 	5'b01010 // acc[i] <= {acc[i][23:0], val[7:0]}

`define sendio 	5'b01011 // ioReg[i] <= op; ioSet
`define getio  	5'b01100 // mem[(addr[i])+val] <= ioReg[i]; ioRst

// SEARCH INSTRUCTIONS
`define search  5'b01101 // act[i]<=(b[i]&acc[i]=op)?act[i]:act[i]+1
`define csearch	5'b01110 // act[i]<=(b[i-1]&acc[i]=op)?0:act[i]+1
`define insert  5'b01111 // insert in the first active position in sr

// SELECTS NO-OP INSTRUCTIONS
`define contr   5'b11111 // instruction: instr[23:19]

// CONTROL & GLOBAL INSTRUCTIONS for instr[31:24]= 11111_000
`define allact  5'b00000 // act[i] <= 0
`define where   5'b00001 // act[i] <= (b[i]&cond) ? act[i] : [i]+1
`define elsew   5'b00010 // act[i] <= (act[i]=0)?1:(act[i]=1)?0:act[i]
`define back    5'b00011 // act[i] <= (act[i]=0)? 0 : act[i]-1
`define selsh   5'b00100 // selection global shift right

`define delete  5'b00110 // delete in first position in sr
`define getsr   5'b00111 // acc[i] <= sr[i]
`define sendsr  5'b01000 // sr[i] <= acc[i]

`define ixload  5'b01001 // acc[i] <= i

`define setred 	5'b01010 // set reduce's function

`define sradd   5'b11011 // acc[i] <= acc[i] + sr[i]
`define srmin   5'b11100 // acc[i] <= min(acc[i], sr[i])
`define srmax   5'b11101 // acc[i] <= max(acc[i], sr[i])

`define arela   5'b11110 // addr <= aAcc
`define shift   5'b11111 // local_shift(val[2:0])