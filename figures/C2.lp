{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 02_toyRISC.v
Description: a RISC processor
************************************************************************/
module toyRISC(output   [15:0]  instrAddr   , // to program memory      
               input    [31:0]  instr       , // from program memory    
               output   [31:0]  dataAddr    , // data memory address    
               output   [31:0]  procOut     , // data to data memory    
               input    [31:0]  memOut      , // data from data memory  
               output          	procWr     	, // write enable           
               input            reset       , 
               input            interrupt   ,
               input    [15:0]  intAddr     ,
               output           inta        ,              
               input            clk         );                           
           
    wire            writeEnable ;
    wire    [15:0]  address     ;
    wire    [31:0]  leftOp      ;
               
    Decode  Decode( .procWr     (procWr     ),
                    .writeEnable(writeEnable),
                    .instr      (instr      ),
                    .inta       (inta       )); 
                    
    Control Control(instrAddr   ,
                    instr       ,
                    address     ,
                    leftOp      ,
                    reset       ,
                    interrupt   ,
                    intAddr     ,
                    inta        ,
                    clk         );
                    
    RALU RALU(  instr       ,
                dataAddr    ,
                procOut     ,
                memOut      ,
                address     ,
                leftOp      ,
                writeEnable ,
                inta        ,
                clk         );              
endmodule
 \end{lstlisting}
\end{shaded*}
}
