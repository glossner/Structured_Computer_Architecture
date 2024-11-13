{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 03_memory.v
Description:
************************************************************************/
module memory(  output  [31:0]  memOut  ,
                input   [31:0]  procOut ,
                input   [31:0]  dataAddr,
                input           procWr  ,
                input           clk     );
    reg [31:0]  mem[0:1023] ;

    assign memOut = mem[dataAddr[9:0]]  ;
    always @(posedge clk) if (procWr)
        mem[dataAddr[9:0]] <= procOut   ;
endmodule
 \end{lstlisting}
\end{shaded*}
} 