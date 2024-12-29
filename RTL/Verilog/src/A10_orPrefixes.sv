module orPrefixes #(parameter n = 256)(output logic [0:n-1] out,
                                       input  logic [0:n-1] in);
    integer k;
    always_comb  begin  out[0] = in[0];
                        for (k=1; k<n; k=k+1) out[k] = in[k] | out[k-1];
                 end
 endmodule