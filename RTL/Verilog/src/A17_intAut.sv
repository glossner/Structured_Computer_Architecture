module intAut(  output  logic       inta    ,
                output  logic [2:0] intState, // state register
                input   logic [5:0] opCode1 ,
                input   logic       intIn   ,
                input   logic       reset   ,
                input   logic       clk     );

    // intState = 000: intDisable
    // intState = 001: intEnable
    // intState = 010: intExec1
    // intState = 011: intExec2
    // intState = 100: intExec3
    // intState = 101: intExec4

    always_ff @(posedge clk)
     if (reset)                                 intState <= 3'b000  ;
      else begin
            if (opCode1 == `eint)               intState <= 3'b001  ;
            if (opCode1 == `dint)               intState <= 3'b000  ;
            if (intIn && (intState == 3'b001))  intState <= 3'b010  ;
            if (intState == 3'b010)             intState <= 3'b011  ;
            if (intState == 3'b011)             intState <= 3'b100  ;
            if (intState == 3'b100)             intState <= 3'b101  ;
            if (intState == 3'b101)             intState <= 3'b000  ;
         end
    assign inta = intState == 3'b011    ;
endmodule