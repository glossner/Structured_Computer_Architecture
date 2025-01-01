{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/*********************************************************************
File name: 01_theSystem.v
Description: a RISC processor and its memories
*********************************************************************/
module theSystem (  input           reset       ,
                    input           interrupt   ,
                    input   [15:0]  intAddr     ,
                    output          inta        ,
                    input           clk         );
                    
    wire            procWr      ;
    wire    [31:0]  memOut      ;
    wire    [31:0]  procOut     ;
    wire    [31:0]  dataAddr    ;
    wire    [31:0]  instr       ;   
    wire    [15:0]  instrAddr   ;   
                                                
    toyRISC PROCESSOR(  instrAddr   , // to program memory 
                        instr       , // from program memory
                        dataAddr    , // data memory address
                        procOut     , // data to data memory
                        memOut      , // data from data memory
                        procWr      , // write enable 
                        reset       ,
                        interrupt   ,
                        intAddr     ,
                        inta        ,
                        clk         );
                    
    memories MEMORIES(  instr       ,
                        instrAddr   ,
                        memOut      ,
                        procOut     ,
                        dataAddr    ,
                        procWr      ,
                        clk         );
endmodule                                                                       
 \end{lstlisting}
\end{shaded*}
}
