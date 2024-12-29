module A10_randomCirc(output   f, g,
                      input    a, b, c, d, e);
   wire     w1, w2;
   and  and1(w1, a, b),
        and2(w2, c, d);
   or   or1(f, w1, c),
        or2(g, e, w2);
endmodule