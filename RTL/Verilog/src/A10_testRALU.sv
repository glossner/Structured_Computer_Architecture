`include "defineRALU.vh"
module testRALU;
    logic [31:0]    leftOut     ;
    logic [31:0]    righttOut   ;
    logic           crOut       ;
    logic [4:0]     leftAddr    ;
    logic [4:0]     rightAddr   ;
    logic [4:0]     destAddr    ;
    logic [31:0]    in          ;
    logic           load        ;
    logic           we          ;
    logic [3:0]     func        ;
    logic           clock       ;
    logic [51:0]    mInstr      ;

    initial begin               clock = 0       ;
                    forever #1  clock = ~clock  ;
            end

    RALU dut(   leftOut     ,
                righttOut   ,
                crOut       ,
                leftAddr    ,
                rightAddr   ,
                destAddr    ,
                in          ,
                load        ,
                we          ,
                func        ,
                clock       );

    // Define MICRO-INSTRUCTION
    assign {func, destAddr, leftAddr, rightAddr, load, in} = mInstr ;

    initial begin
        we = 1'b1   ;
        mInstr = {`move, 5'b00000,5'b00110,5'b00111,1'b1,32'b1000} ;
    #2  mInstr = {`move, 5'b00001,5'b00010,5'b00010,1'b1,32'b1001} ;
    #2  mInstr = {`move, 5'b00010,5'b00110,5'b00111,1'b1,32'b1010} ;
    #2  mInstr = {`move, 5'b00011,5'b00010,5'b00010,1'b1,32'b1011} ;
    #2  mInstr = {`add,  5'b00011,5'b00010,5'b00001,1'b0,32'b111}  ;
    #2  mInstr = {`sub,  5'b00011,5'b00010,5'b00001,1'b0,32'b111}  ;
    #2  mInstr = {`bwand,5'b00011,5'b00010,5'b00001,1'b0,32'b111}  ;
    #2  $finish;
    end

    initial begin
        $monitor("t=%0d clock=%b func=%b destAddr=%0d leftAddr=%0d
                rightAddr=%0d load=%0d in=%0d RF=[%0d, %0d, %0d, %0d]",
                $time, clock, func, destAddr, leftAddr, rightAddr, load, in,
                dut.rf.mem[0],
                dut.rf.mem[1],
                dut.rf.mem[2],
                dut.rf.mem[3]);
        end
endmodule