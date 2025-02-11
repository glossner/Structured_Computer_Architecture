module A10_randomCirc(output   logic f, g,
                      input    logic a, b, c, d, e);
   logic  w1, w2;
   and  and1(w1, a, b),
        and2(w2, c, d);
   or   or1(f, w1, c),
        or2(g, e, w2);
endmodule