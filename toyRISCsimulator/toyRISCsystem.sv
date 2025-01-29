`include "DEFINES.vh"
module toyRISCsystem(	output	logic	inta			,
						//input	logic	[31:0]	progIn	,
						//input	logic	[9:0]	progAddr,
						input	logic	progRead, 
										//progLoad, 
										intIn, 
										reset, 
										clk);
										
	logic	[31:0]	instr  		;
	logic	[9:0]	nextPC 		;
	logic	[31:0]	dataIn  	;
	logic	[31:0]	dataOut 	;
	logic	[9:0]	addr    	;
	logic			dataRead	;	
	logic           dataWrite	;

	toyRISC processor( 	instr   	,
						nextPC  	,
						intIn   	,
						inta    	,
						dataIn  	,
						dataOut 	,
						addr    	,
						dataRead	,
						dataWrite	,
						reset   	,
						clk  		);

	logic	[31:0]	dataMemory[0:1023]	;
	logic	[31:0]	progMemory[0:1023]	;
	logic	[31:0]	progMemOut			;
	logic	[31:0]	dataMemOut			;
	
	always_ff @(posedge clk) begin	
		if (progRead) progMemOut <= progMemory[nextPC]	;
		//if (progLoad) progMemory[progAddr] <= progIn	;
	end
		
	always_ff @(posedge clk) begin
		if (dataRead) dataMemOut <= dataMemory[addr];
		if (dataWrite) dataMemory[addr] <= dataOut	;
	end
	
	assign instr 	= progMemOut;
	assign dataIn 	= dataMemOut;
endmodule