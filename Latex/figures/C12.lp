{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 04_nextPc.v
Description:
************************************************************************/
`include "0_ISAdefine.vh"
module  nextPc( output  reg [15:0]  addr    ,
                output  reg [15:0]  address ,
                input       [15:0]  pc      ,
                input       [15:0]  jmpVal  ,
                input       [31:0]  leftOp  ,
                input       [5:0]   opCode  ,
                input               inta    ,
                input       [15:0]  intAddr );

    always @(*)
     if (inta)        begin addr = intAddr              ;
                            address = pc + 1            ;
                      end
      else case(opCode)
            `nop   :        addr = pc + 1               ;
            `rjmp  :        addr = pc + jmpVal          ;
            `zjmp  :        addr = (leftOp == 0) ? pc + jmpVal : pc + 1 ;
            `nzjmp :        addr = (leftOp == 0) ? pc + 1 : pc + jmpVal ;
            `ret   :        addr = leftOp               ;
            `ajmp  :        addr = jmpVal               ;
            `call  :  begin addr = jmpVal               ;
                            address = pc + 1            ;
                      end
            default :       addr = pc + 1               ;
           endcase
endmodule
 \end{lstlisting}
\end{shaded*}
}