{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 02_memories.v
Description: data memory and program memory used to simulate the 
                behavior of toyRISC
************************************************************************/
module memories(output  [31:0]  instr       ,
                input   [15:0]  instrAddr   ,
                output  [31:0]  memOut      ,
                input   [31:0]  procOut     ,
                input   [31:0]  dataAddr    ,
                input           procWr      ,
                input           clk         );
            
    programMemory   PROG_MEM(   .instr      (instr      ),
                                .instrAddr  (instrAddr  ),
                                .clk        (clk        ));
                    
    memory DATA_MEM(memOut  ,
                    procOut ,
                    dataAddr,
                    procWr  ,
                    clk     );
endmodule
 \end{lstlisting}
\end{shaded*}
}
