module fullAdder(   input   logic a, b, crIn,
                    output  logic sum, crOut);
    logic sum0, cr0, cr1;

    xor myXor(sum0, a, b);
    xor outXor(sum, sum0, crIn);
    and myAnd1(cr0, a, b);
    and myAnd2(cr1, sum0, crIn);
    or myOr(crOut, cr0, cr1);
endmodule