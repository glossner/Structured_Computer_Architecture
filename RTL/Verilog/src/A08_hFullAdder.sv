module hFullAdder(  input   logic a, b, crIn,
                    output  logic sum, crOut);
    logic sum0, cr0, cr1;
    halfAdder   ha0(.a  (a      ),
                    .b  (b      ),
                    .sum(sum0   ),
                    .cr (cr0    )),
                ha1(.a  (sum0   ),
                    .b  (crIn   ),
                    .sum(sum    ),
                    .cr (cr1    ));
    or outOr(crOut, cr0, cr1);
endmodule