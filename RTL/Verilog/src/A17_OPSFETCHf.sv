module OPSFETCH(    input   logic [31:0]    instruction1   ,
                    input   logic [31:0]    instruction4   ,
                    input   logic [9:0]     pc1            ,
                    input   logic           inta           ,
                    input   logic           we4            ,
                    input   logic [31:0]    result4        ,
                    input   logic [3:0]     opSel          ,
                    output  logic           zero           ,
                    output  logic [31:0]    leftOp, leftOp2, 
		    output  logic [31:0]    rightOp2       ,
                    output  logic [3:0]     opSel2         ,
                    output  logic [31:0]    instruction2   ,
                    input   logic           clk            );
    logic   [31:0]  rf[0:31]    ;
    logic   [31:0]  rightOp     ;
    logic           we          ;
    logic   [4:0]   destAddr    ;
    logic   [31:0]  result      ;
    logic   [4:0]   leftAddr    ;
    logic   [4:0]   rightAddr   ;
    always_ff @(posedge clk) if (we) rf[destAddr] <= result ;
    assign leftOp       = ((destAddr == leftAddr) && we) ?
                            result : rf[leftAddr]   ;
    assign rightOp      = ((destAddr == rightAddr) && we) ?
                            result : rf[rightAddr]  ;
    assign zero         = leftOp == 0           ;
    assign rightAddr    = instruction1[15:11]   ;
    // PipeReg2:
    always_ff @(posedge clk)
        begin
            leftOp2         <=  leftOp          ;
            rightOp2        <=  rightOp         ;
            opSel2          <=  opSel           ;
            instruction2    <=  instruction1    ;
        end
    intUnit intunit(.we         (we                     ),
                    .destAddr   (destAddr               ),
                    .result     (result                 ),
                    .leftAddr   (leftAddr               ),
                    .we4        (we4                    ),
                    .destAddr4  (instruction4[25:21]    ),
                    .result4    (result4                ),
                    .leftAddr1  (instruction1[20:16]    ),
                    .pc1        (pc1                    ),
                    .inta       (inta                   ));
endmodule