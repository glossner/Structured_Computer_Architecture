{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 03_programMemory.v
Description:
************************************************************************/
module programMemory(   output  reg [31:0]  instr       ,
                        input       [15:0]  instrAddr   ,
                        input               clk         );
    reg [31:0]  progMem[0:1023] ;

    always @(posedge clk)   instr <= progMem[instrAddr[9:0]]    ;
endmodule
 \end{lstlisting}
\end{shaded*}
} 