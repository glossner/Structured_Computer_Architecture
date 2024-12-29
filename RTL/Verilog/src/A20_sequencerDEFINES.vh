// CONTROLLER'S AUTOMATON STATES
`define	idle	2'b00
`define loading 2'b01
`define running 2'b10

// ADDRESSING MODES DEFINING THE RIGHT OPERAND OP
`define	imm 3'b000 // op: {{8{val[23]}}, val}
`define dir 3'b001 // op: cMem[val]
`define rel 3'b010 // op: cMem[cAddr+val]
`define rei 3'b011 // op: cMem[cAddr+val]; cAddr = cAddr+val
`define cim 3'b100 // op: coOp 
				   
// FUNCTIONAL INSTRUCTIONS 
`define cadd   	5'b00000 // {cr,acc} <= acc + op
`define caddcr 	5'b00001 // {cr,acc} <= acc + op + cr
`define csub   	5'b00010 // {cr,acc} <= acc - op
`define csubcr  5'b00011 // {cr,acc} <= acc - op - cr
`define cmult   5'b00100 // {cr,acc} <= {cr, acc * op}
`define cbwand  5'b00101 // {cr,acc} <= {cr, acc & op}
`define cbwor   5'b00110 // {cr,acc} <= {cr, acc | op}
`define cbwxor  5'b00111 // {cr,acc} <= {cr, acc ^ op}
// DATA MOVE INSTRUCTIONS		
`define cload   5'b01000 // {cr,acc} <= {cr, op}
`define cstore  5'b01001 // cMem <= acc
`define cinsval 5'b01010 // {cr,acc} <= {cr, (acc[23:0],val[7:0]} 

// SELECTS NO-OP INSTRUCTIONS						 
`define contr	5'b11111 // instruction: instr[23:19]

// CONTROL & GLOBAL INSTRUCTIONS for instr[31:24]= 11111_000 
`define	nop     5'b00000 // pc <= pc+1
`define jmp    	5'b00001 // pc <= pc+val
`define brz   	5'b00010 // pc <= (acc = 0) ? pc+val : pc+1
`define brnz  	5'b00011 // pc <= (acc = 0) ? pc+1 : pc+val
`define brzdec	5'b00100 // pc <= (acc = 0) ? pc+val : pc+1
`define brnzdec	5'b00101 // pc <= (acc = 0) ? pc+1 : pc+val
`define ajmp	5'b00110 // pc <= val
`define halt	5'b00111 // pc <= pc

`define setint	5'b01000 // set interrupt

`define datains	5'b01001 // insert data in array
`define dataext 5'b01010 // extract data from arrray

`define param	5'b01011 // load parameter
`define	pload	5'b01100 // load program starting to val
`define	prun	5'b01101 // run the program at val

`define start	5'b01110 // start cycle counter
`define stop    5'b01111 // stop cycle coounter

`define gshift 	5'b11101 // global_shift(val[2:0])

`define crela	5'b11110 // cAddr <= acc; load relative address
`define cshift 	5'b11111 // local_shift(val[2:0])
