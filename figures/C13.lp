{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 00_toyRISCprogram.v
Description: at #20 the simulator genereate an interrupt
************************************************************************/
            VAL(0,11);
            VAL(1,1);
            EINT;
    LB(0);  SUB(0,0,1);
            NZJMP(0,0);
            CALL(1);
            NOP;
            NOP;
            VAL(2,33);
            HALT;
    LB(1);  VAL(0,66);
            RET;
            NOP;
            NOP;
            NOP;
            NOP;
            VAL(3,13);
            NOP;
            RET;
\end{lstlisting}
\end{shaded*}
}
