/*************************************************************************
File name: DCDtoyRISC.sv
                        toyRISC's DCD
*************************************************************************/
//`include "DEFINES.vh"
module	DCDtoyRISC(	
		input	logic	[5:0]	opCode						,
		input	logic			intIn						,
		output	logic			inta, dataRead, dataWrite   ,
		input	logic			reset, clk					);
					
	logic	ei	;
    always_ff @(posedge clk) 
		if (reset)                         ei <= 0;
			else begin 	if (opCode == `eint)   ei <= 1;
						if (opCode == `dint)   ei <= 0;
						if (intIn & ei)        ei <= 0;
				 end
    assign inta = intIn & ei;

    always_comb begin
		dataRead  	= (opCode == `read) 	? 1'b1	: 1'b0	;
		dataWrite  	= (opCode == `store)	? 1'b1	: 1'b0	;
	end
endmodule