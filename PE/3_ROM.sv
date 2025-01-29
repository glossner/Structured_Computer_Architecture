module ROM(	input	logic [7:0]	stateReg	,
			output	logic [7:0]	nextState	,
			output	logic 		jmp			,
			output	logic [3:0]	flagSel		,
			output	logic [4:0] leftAddr   	,
			output	logic [4:0] rightAddr  	,
			output	logic 		crIn		,
			output	logic [4:0] destAddr   	,
			output	logic		load		,
			output	logic		we			,
			output	logic [3:0] aluFunc    	,
			output	logic		Read		,
			output	logic		Write		);	
			
	logic [36:0] rom[0:255]	; // ROM: combinational circuit
	
	assign {jmp			,	// 0: increment; 1: jump to netState
			nextState	,   // jump address
			flagSel		,   // selects the flag for jump
			leftAddr	,   // selects the left operand
			rightAddr	,   // selects the right operand
			crIn		,	// carry input for ALU
			destAddr	,   // selects destination
			load		,   // select input as left operand
			we			,   // write back in register file
			aluFunc     ,	// ALU function
			Read	    ,	// get the input data
			Write		}   // send the output data
			= rom[stateReg];			
endmodule