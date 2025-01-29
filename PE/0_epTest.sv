`include "0_define.vh"
module epTest;
	logic [31:0] 	DataIn	;
	logic [31:0]	DataOut	;
	logic [7:0]		Func	;
	logic 			Empty	;
	logic 			Full	;
	logic  			Read	;
	logic 			Write	;
	logic 			Init	;
	logic 			clock	;
// CLOCK GENERATOR
	initial begin 				clock = 0		;
					forever	#1 	clock = ~clock	;
			end
// GENERATE THE MICRO-CODE		
	`include "0_microCodeGenerator.sv"
// DEVICE UNDER TEST			
	elementaryProcessor dut(DataIn	,
							DataOut	,
							Func	,
							Empty	,
							Full	,
							Read	,
							Write	,
							Init	,
							clock	);
	// DISPLAY THE ROM CONTENT
	integer i ;
	initial for (i=0; i<16; i=i+1) 
				$display("epROM[%0d] \t = %b", 
					i, dut.epCROM.epROM.rom[i]);
	// APPLY STIMULUS
	initial	begin			Init 	= 1'b1	;
							Empty	= 1'b0	;
							Full	= 1'b0	;
							DataIn	= 32'b11;
							Func	= 8'b0	;
					#2		Init 	= 1'b0	;
					#2		DataIn	= 32'b1;
					#2		DataIn	= 32'b10;
					#164 $stop 	;
			end
	// MONITOR THE BEHAVIOR
	initial $monitor("t=%0d Func=%0d Init=%0d stateReg=%0d DataOut=%0d test=%b RF=[%0d %0d %0d %0d %0d %0d %0d %0d %0d]",
				$time, 
				Func,
				Init,
				dut.epCROM.stateReg,
				DataOut,
				dut.epCROM.test,
				dut.epRALU.rf.mem[0],
				dut.epRALU.rf.mem[1],
				dut.epRALU.rf.mem[2],
				dut.epRALU.rf.mem[3],
				dut.epRALU.rf.mem[4],
				dut.epRALU.rf.mem[5],
				dut.epRALU.rf.mem[6],
				dut.epRALU.rf.mem[7],
				dut.epRALU.rf.mem[31]);
endmodule