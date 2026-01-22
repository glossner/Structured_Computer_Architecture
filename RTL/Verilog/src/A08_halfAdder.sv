module halfAdder(   input   logic a, b,
                    output  logic sum, cr);
    xor myXor(sum, a, b);
    and myAnd(cr, a, b);
endmodule