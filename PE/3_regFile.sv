/*************************************************************************
File name:      regFile.sv
Circuit name:   Register File
Description:    Three-pport, 32 32-bit words implemented with a 
				synchronous RAM
*************************************************************************/
module regFile( output  logic [31:0] 	leftOut    	,
                output  logic [31:0] 	rightOut  	,
				input  	logic [31:0]	in 			,
                input   logic [4:0] 	leftAddr    ,
                input   logic [4:0] 	rightAddr  	,
                input   logic [4:0] 	destAddr    ,
				input	logic			we			,
                input   logic       	clock  		);				
	logic [31:0]	mem[0:31]	;
	
	always_ff @(posedge clock) if (we) mem[destAddr] <= in	;
	assign leftOut  = mem[leftAddr]	;
	assign rightOut = mem[rightAddr];			
endmodule