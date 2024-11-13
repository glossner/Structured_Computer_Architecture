{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 03_RALU.v
Description:
************************************************************************/
`include "0_ISAdefine.vh"
module  RALU(   input   [31:0]  instr       ,
                output  [31:0]  dataAddr    ,
                output  [31:0]  procOut     ,
                input   [31:0]  memOut      ,
                input   [15:0]  address     ,
                output  [31:0]  leftOp      ,
                input           writeEnable ,
                input           inta        ,
                input           clk         );

   wire    [31:0]  aluOut   ;
   wire    [31:0]  rightOp  ;
   wire    [31:0]  regFileIn;

   assign dataAddr = rightOp                ;
   assign procOut  = leftOp                 ;
   assign callIns  = instr[31:26] == `call  ;

   regFile
    regFile(.leftOut    (leftOp                                     ),
            .rightOut   (rightOp                                    ),
            .in         (regFileIn                                  ),
            .leftAddr   (instr[20:16]                               ),
            .rightAddr  (instr[15:11]                               ),
            .destAddr   ((inta || callIns) ? 5'b11111 : instr[25:21]),
            .writeEnable(writeEnable | inta                         ),
            .clk        (clk                                        ));

   mux4_32 mux(.out(regFileIn                       ),
               .in0({16'b0, address}                ),
               .in1({{16{instr[15]}}, instr[15:0]} 	),
               .in2(memOut                          ),
               .in3(aluOut                          ),
               .sel(instr[31:30] & {!inta, !inta}   ));

   alu alu(.out     (aluOut         ),
           .leftIn  (leftOp         ),
           .rightIn (rightOp        ),
           .func    (instr[31:26]   ));
endmodule
 \end{lstlisting}
\end{shaded*}
} 