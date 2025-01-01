{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 04_regFile.v
Description:
************************************************************************/
module regFile(	output	[31:0]	leftOut    	,
                output	[31:0]	rightOut   	,
                input	[31:0]	in         	,
                input	[4:0]	leftAddr   	,
                input	[4:0]	rightAddr  	,
                input	[4:0]	destAddr   	,
                input			writeEnable	,
				input			clk		);			
	reg	[31:0]	registers[0:31]	;
	
	always @(posedge clk)	
		if (writeEnable) registers[destAddr] <= in	;
	
	assign leftOut	= registers[leftAddr ]	;
	assign rightOut	= registers[rightAddr]	;
endmodule

 \end{lstlisting}
\end{shaded*}
} 