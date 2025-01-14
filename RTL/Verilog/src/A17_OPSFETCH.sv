module OPSFETCH(input   logic [31:0]    instruction1,
                input   logic [9:0]     pc1         ,
                input   logic           inta        ,
                input   logic           we3         ,
                input   logic [4:0]     destAddr3   ,
                input   logic [31:0]    result3     ,
                input   logic [1:0]     opSel       ,
                output  logic [1:0]     opSel2      ,
                output  logic           zero        ,
                output  logic [31:0]    leftOp      ,
                output  logic [31:0]    leftOp2     ,
                output  logic [31:0]    rightOp2    ,
                output  logic [31:0]    value2      ,
                output  logic [5:0]     opCode2     ,
                output  logic [4:0]     destAddr2   ,
                input   logic           clk         );
    logic   [31:0]  rf[0:31]    ;
    logic   [31:0]  rightOp     ;
    logic           we          ;
    logic   [4:0]   destAddr    ;
    logic   [31:0]  result      ;
    logic   [4:0]   leftAddr    ;
    logic   [4:0]   rightAddr   ;

    always_ff @(posedge clk) if (we) rf[destAddr] <= result ;

    assign leftOp       = rf[leftAddr]          ;
    assign rightOp      = rf[rightAddr]         ;
    assign zero         = leftOp == 0           ;
    assign rightAddr    = instruction1[15:11]   ;
// PipeReg2:
    always_ff @(posedge clk) begin
        leftOp2     <=  leftOp                                      ;
        rightOp2    <=  rightOp                                     ;
        value2      <=  {{16{instruction1[15]}}, instruction1[15:0]};
        opSel2      <=  opSel                                       ;
        opCode2     <=  instruction1[31:26]                         ;
        destAddr2   <=  instruction1[25:21]                         ;
    end

    intUnit intunit(.we         (we                     ),
                    .destAddr   (destAddr               ),
                    .result     (result                 ),
                    .leftAddr   (leftAddr               ),
                    .we3        (we3                    ),
                    .destAddr3  (destAddr3              ),
                    .result3    (result3                ),
                    .leftAddr1  (instruction1[20:16]    ),
                    .pc1        (pc1                    ),
                    .inta       (inta                   ));
endmodule