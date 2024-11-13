{\footnotesize
\begin{shaded*}
\begin{lstlisting}[language=Verilog]
/************************************************************************
File name: 04_mux4_32.v
Description:
************************************************************************/
module mux4_32( output  reg [31:0]  out,
                input       [31:0]  in0,
                input       [31:0]  in1,
                input       [31:0]  in2,
                input       [31:0]  in3,
                input       [1:0]   sel);

    always @(*) case(sel)
                    2'b00: out = in0;
                    2'b01: out = in1;
                    2'b10: out = in2;
                    2'b11: out = in3;
                endcase
 endmodule
 \end{lstlisting}
\end{shaded*}
} 