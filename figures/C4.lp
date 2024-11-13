{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 03_Decode.v
Description:
************************************************************************/
`include "0_ISAdefine.vh"
module Decode(output          procWr      , // data memory write enable 
              output          writeEnable , // register file write enable
              input   [31:0]  instr       ,
              input           inta        );
                
    assign procWr       = instr[31:26] == `store                ;
    assign writeEnable  = &instr[31:30] | &instr[28:26] | inta  ;
endmodule

 \end{lstlisting}
\end{shaded*}
}
