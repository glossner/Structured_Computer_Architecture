module DECODE(  input   logic [31:0]    instruction1,
                input   logic [31:0]    instruction2,
                input   logic [31:0]    instruction3,
                input   logic           zero        ,
                input   logic           intIn       ,
                output  logic           inta        ,
                output  logic [1:0]     nextPCsel   ,
                output  logic [3:0]     opSel       ,
                output  logic           memSel      ,
                output  logic           we          ,
                output  logic           dataRead    ,
                output  logic           dataWrite   ,
                input   logic           reset       ,
                input   logic           clk         );
    logic [5:0] opCode1     ;
    logic [4:0] destAddr1   ;
    logic [4:0] leftAddr1   ;
    logic [4:0] rightAddr1  ;
    logic [4:0] destAddr2   ;
    logic [5:0] opCode3     ;
    logic [4:0] destAddr3   ;
    logic       leftFwd     ;
    logic       rightFwd    ;
    logic [2:0] intState    ;

    assign opCode1      = instruction1[31:26]   ;
    assign leftAddr1    = instruction1[20:16]   ;
    assign rightAddr1   = instruction1[15:11]   ;
    assign destAddr2    = instruction2[25:21]   ;
    assign destAddr3    = instruction3[25:21]   ;
    assign opCode3      = instruction3[31:26]   ;
// Interrupt automaton
    intAut intAut(  inta    ,
                    intState,
                    opCode1 ,
                    intIn   ,
                    reset   ,
                    clk     );
// Program control
    logic [5:0] jmpSel  ;

    assign jmpSel = (intState > 3'b011) ? `nop : opCode1    ;

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
// Forwarding section
    assign leftFwdY  = 
           (leftAddr1  == destAddr2) && (opCode1[4] == 1'b1);
    assign rightFwdY = 
           (rightAddr1 == destAddr2) && (opCode1[4] == 1'b1);
    assign leftFwdO  = 
           (leftAddr1  == destAddr3) && (opCode1[4] == 1'b1);
    assign rightFwdO = 
           (rightAddr1 == destAddr3) && (opCode1[4] == 1'b1);
    always_comb
        case(opCode1)
            `addv   :                       opSel[1:0] = 2'b11  ;
            `multv  :                       opSel[1:0] = 2'b11  ;
            `addvc  :                       opSel[1:0] = 2'b11  ;
            `val    :                       opSel[1:0] = 2'b11  ;
            default : if (rightFwdY)        opSel[1:0] = 2'b01  ;
                        else if (rightFwdO) opSel[1:0] = 2'b10  ;
                               else         opSel[1:0] = 2'b00  ;
        endcase
    always_comb
        if (leftFwdY)       opSel[3:2] = 2'b01  ;
         else if (leftFwdO) opSel[3:2] = 2'b10  ;
               else         opSel[3:2] = 2'b00  ;
// end forwarding section
    assign dataWrite = opCode3 == `store                          ;
    assign dataRead  = opCode3 == `read                           ;
    assign memSel    = opCode3 == `read                           ;
    assign we        = ((opCode3[4] == 1'b1) |
                        (opCode3 == `read)) & !(intState > 3'b001);
endmodule