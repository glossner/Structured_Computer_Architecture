module DECODE(  input   logic [5:0] opCode1     ,
                input   logic       zero        ,
                input   logic [5:0] opCode2     ,
                input   logic       intIn       ,
                output  logic       inta        ,
                output  logic [1:0] nextPCsel   ,
                output  logic [1:0] opSel       ,
                output  logic       we          ,
                output  logic       dataRead    ,
                output  logic       dataWrite   ,
                input   logic       reset       ,
                input   logic       clk         );
                
// INTERRUPT automaton
    logic [2:0] intState; // Interrupt automaton state
/* Interrupt automaton state definition
    intState = 000: intDisable
    intState = 001: intEnable
    intState = 010: intExec1
    intState = 011: intExec2
    intState = 100: intExec3
*/
    always_ff @(posedge clk)
     if (reset)                             intState <= 3'b000  ;
      else  begin
        if (opCode1 == `eint)               intState <= 3'b001  ;
        if (opCode1 == `dint)               intState <= 3'b000  ;
        if (intIn && (intState == 3'b001))  intState <= 3'b010  ;
        if (intState == 3'b010)             intState <= 3'b011  ;
        if (intState == 3'b011)             intState <= 3'b100  ;
        if (intState == 3'b100)             intState <= 3'b000  ;
        end
    assign inta = intState == 3'b010    ;
    logic [5:0] jmpSel  ;
    assign jmpSel = (intState > 3'b001) ? `halt : opCode1   ;
    
// Selection for the INSTRFETCH stage in pipeline
    always_comb
        if (inta)             nextPCsel = 2'b11                 ;
         else   case(jmpSel)
                    `halt   : nextPCsel = 2'b00                 ;
                    `rjmp   : nextPCsel = 2'b10                 ;
                    `zbr    : nextPCsel = zero ? 2'b10 : 2'b01  ;
                    `nzbr   : nextPCsel = zero ? 2'b01 : 2'b10  ;
                    `ret    : nextPCsel = 2'b11                 ;
                    default : nextPCsel = 2'b01                 ;
                endcase
                
// Selection for the EXECUTE stage in pipeline
    always_comb case(opCode1)
                    `load   : opSel = 2'b10 ;
                    `addv   : opSel = 2'b01 ;
                    `multv  : opSel = 2'b01 ;
                    `addvc  : opSel = 2'b01 ;
                    `val    : opSel = 2'b01 ;
                    default : opSel = 2'b00 ;
                endcase

// External signals for DATA MEMORY
    assign dataWrite    = opCode2 == `store ;
    assign dataRead     = opCode2 == `read  ;

// Write enable signal for WRITEBACK stage in pipeline
    assign we = ((opCode2[5:4] == 2'b11)  |
                (opCode2 == `load)        |
                (opCode2 == `val)) & !(intState > 3'b001) ;
endmodule