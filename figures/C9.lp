{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 04_alu.v
Description:
************************************************************************/
`include "0_ISAdefine.vh"
module  alu(output  reg [31:0]  out     ,
            input       [31:0]  leftIn  ,
            input       [31:0]  rightIn ,
            input       [5:0]   func    );

    wire    [32:0]  sum ;
    wire    [32:0]  dif ;
    wire    [31:0]  mult;

    assign sum  = leftIn + rightIn              ;
    assign dif  = leftIn - rightIn              ;
    assign mult = leftIn[15:0] * rightIn[15:0]  ;

    always @(*)
        case(func)
            `bigv  : out = {leftIn[15:0], rightIn[15:0]};
            `add   : out = sum[31:0]                    ;
            `sub   : out = dif[31:0]                    ;
            `addcr : out = sum[32]                      ;
            `subcr : out = dif[32]                      ;
            `lsh   : out = leftIn >> 1                  ;
            `ash   : out = {leftIn[31], leftIn[31:1]}   ;
            `move  : out = leftIn                       ;
            `mult  : out = mult                         ;
            `bwand : out = leftIn & rightIn             ;
            `bwor  : out = leftIn | rightIn             ;
            `bwxor : out = leftIn ^ rightIn             ;
        default :    out = leftIn                       ;
        endcase
endmodule

 \end{lstlisting}
\end{shaded*}
} 