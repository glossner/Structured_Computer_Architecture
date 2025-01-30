module lifo (   output logic [31:0] stack0, stack1,
                input  logic [31:0] in            ,
                input  logic [2:0]  com           ,
                input  logic        reset, clock  );
/*  The command codes: 	nop   = 3'b000, // no operation
                        write = 3'b001, // we
                        pop   = 3'b010, // dec
                        popwr = 3'b011, // dec, we
                        push  = 3'b101; // inc, we  */
    logic [4:0] leftAddr;   // the main pointer
    logic [4:0] nextAddr;

    assign nextAddr =    com[2] ? (leftAddr + 1'b1) :
                        (com[1] ? (leftAddr - 1'b1) :
                        leftAddr);
    always_ff @(posedge clock)
        if (reset)  leftAddr <= 0       ;
         else       leftAddr <= nextAddr;
// The register file
    registerFile rf(.left_op    (stack0        ),
                    .right_op   (stack1        ),
                    .result     (in            ),
                    .left_addr  (leftAddr      ),
                    .right_addr (leftAddr - 1  ),
                    .dest_addr  (nextAddr      ),
                    .we         (com[0]        ),
                    .clock      (clock         ));
endmodule