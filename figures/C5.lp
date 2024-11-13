{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 03_Control.v
Description:
************************************************************************/
`include "0_ISAdefine.vh"
module  Control(output  [15:0]  instrAddr   ,
                input   [31:0]  instruction ,
                output  [15:0]  address     ,
                input   [31:0]  leftOp      ,
                input           reset       ,
                input           interrupt   ,
                input   [15:0]  intAddr     ,
                output          inta        ,
                input           clk         );

    // Interrupt section
    reg intEnable   ;

    always @(posedge clk)
     if (reset)                                     intEnable <= 0  ;
        else if (instruction[31:26] == `ei)         intEnable <= 1  ;
                else if (instruction[31:26] == `di) intEnable <= 0  ;
                        else if (interrupt)         intEnable <= 0  ;

    assign inta = intEnable & interrupt ;

    // Program counter section
    reg [15:0]  pc  ;

    always @(posedge clk) if (reset)    pc <= -1        ;
                            else        pc <= instrAddr ;

    nextPc nextPc(  .addr   (instrAddr          ),
                    .address(address            ),
                    .pc     (pc                 ),
                    .jmpVal (instruction[15:0]  ),
                    .leftOp (leftOp             ),
                    .opCode (instruction[31:26] ),
                    .inta   (inta               ),
                    .intAddr(intAddr            ));
endmodule
 \end{lstlisting}
\end{shaded*}
} 