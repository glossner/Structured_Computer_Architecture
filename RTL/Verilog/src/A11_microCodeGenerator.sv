    logic [7:0] nextState       ;
    logic       jmp             ;
    logic [3:0] flagSel         ;
    logic [4:0] leftAddr        ;
    logic [4:0] rightAddr       ;
    logic       crIn            ;
    logic [4:0] destAddr        ;
    logic       load            ;
    logic       we              ;
    logic [3:0] aluFunc         ;
    logic       read            ;
    logic       write           ;
    logic [7:0] addrCounter     ;
    logic [7:0] labelTab[0:255] ;
    // RUNNING
    initial begin   jmp         = 1'b0  ;
                    nextState   = 8'b0  ;
                    flagSel     = 3'b0  ;
                    leftAddr    = 5'b0  ;
                    rightAddr   = 5'b0  ;
                    crIn        = 1'b0  ;
                    destAddr    = 5'b0  ;
                    load        = 1'b0  ;
                    we          = 1'b0  ;
                    aluFunc     = 4'b0  ;
                    read        = 1'b0  ;
                    write       = 1'b0  ;
                    addrCounter = 8'b0  ;
                    `include "0_microProgram.sv" // first pass
                    addrCounter = 8'b0  ;
                    `include "0_microProgram.sv" // second pass
            end
    // LOAD LINE IN ROM
    function LL;    // load line
        dut.epCROM.epROM.rom[addrCounter] <= {  jmp         ,
                                                nextState   ,
                                                flagSel     ,
                                                leftAddr    ,
                                                rightAddr   ,
                                                crIn        ,
                                                destAddr    ,
                                                load        ,
                                                we          ,
                                                aluFunc     ,
                                                read        ,
                                                write       }   ;
        jmp         = 1'b0                                      ;
        nextState   = 8'b0                                      ;
        flagSel     = 3'b0                                      ;
        leftAddr    = 5'b0                                      ;
        rightAddr   = 5'b0                                      ;
        crIn        = 1'b0                                      ;
        destAddr    = 5'b0                                      ;
        load        = 1'b0                                      ;
        we          = 1'b0                                      ;
        aluFunc     = 4'b0                                      ;
        read        = 1'b0                                      ;
        write       = 1'b0                                      ;
        addrCounter = addrCounter + 1                           ;
    endfunction

    // sets labelTab in the first pass
    // associating 'addrCounter' with 'labelIndex'
    function LB(input logic [5:0] labelIndex);
        labelTab[labelIndex] = addrCounter;
    endfunction

    function JMP(input [15:0] label); // jump at label
        jmp = 1'b1                  ;
        nextState = labelTab[label] ;
        flagSel = 4'b0000           ;
    endfunction

    function CRJMP(input [15:0] label); // jump if carry at label
        nextState = labelTab[label] ;
        flagSel = 4'b0001           ;
    endfunction

    function ODDJMP(input [15:0] label); // jump if leftOp[0]=1
        nextState = labelTab[label] ;
        flagSel = 4'b0010           ;
    endfunction

    function SGNJMP(input [15:0] label); // jump if leftOp[31]=1
        nextState = labelTab[label] ;
        flagSel = 4'b0011           ;
    endfunction

    function ZJMP(input [15:0] label); // jump if leftOp=0
        nextState = labelTab[label] ;
        flagSel = 4'b0100           ;
    endfunction

    function LEQJMP(input [15:0] label); // jump if leftOp<=RightOp
        nextState = labelTab[label] ;
        flagSel = 4'b0101           ;
    endfunction

    function GEQJMP(input [15:0] label); // jump if leftOp>=RightOp
        nextState = labelTab[label] ;
        flagSel = 4'b0110           ;
    endfunction

    function EQJMP(input [15:0] label); // jump if leftOp==RightOp
        nextState = labelTab[label] ;
        flagSel = 4'b0111           ;
    endfunction

    function EMPTY(input [15:0] label); // jump empty
        nextState = labelTab[label] ;
        flagSel = 4'b1000           ;
    endfunction

    function FULL(input [15:0] label); // jump full
        nextState = labelTab[label] ;
        flagSel = 4'b1001           ;
    endfunction

    function L(input [4:0] address); //  loftOp=rf[address]
        leftAddr = address  ;
    endfunction

    function R(input [4:0] address); //  RightOp=rf[address]
        rightAddr = address ;
    endfunction

    function D(input [4:0] address); //  rf[address]=result
        destAddr = address  ;
    endfunction

    function CR; //  carry input
        crIn = 1'b1 ;
    endfunction

    function LD; //  leftOp==DataIn
        load = 1'b1 ;
    endfunction

    function WB; //  write back the result in register file
        we = 1'b1   ;
    endfunction

    function BIGV; // rf[dest] = {rf[left][15:0],rf[right][15:0]}
        aluFunc = `bigv ;
    endfunction

    function ADD; // rf[dest] = rf[left] + rf[right]
        aluFunc = `add  ;
    endfunction

    function SUB; // rf[dest] = rf[left] - rf[right]
        aluFunc = `sub  ;
    endfunction

    function ADDCR; // rf[dest] = (rf[left] + rf[right])[32]
        aluFunc = `addcr;
    endfunction

    function SUBCR; // rf[dest] = (rf[left] - rf[right])[32]
        aluFunc = `subcr;
    endfunction

    function LSH; // rf[dest] = rf[left] >> 1
        aluFunc = `lsh  ;
    endfunction

    function ASH; // rf[dest] = {rf[left][31],rf[left][31:1]}
        aluFunc = `ash  ;
    endfunction

    function MOVE; // rf[dest] =  rf[left]
        aluFunc = `move ;
    endfunction

    function MULT; // rf[dest] =  rf[left] * rf[right]
        aluFunc = `mult ;
    endfunction

    function AND; // rf[dest] =  rf[left] & rf[right]
        aluFunc = `bwand;
    endfunction

    function OR; // rf[dest] =  rf[left] | rf[right]
        aluFunc = `bwor ;
    endfunction

    function XOR; // rf[dest] =  rf[left] ^ rf[right]
        aluFunc = `bwxor;
    endfunction

    function GET; // rf[dest] =  dataIn
        read = 1'b1 ;
    endfunction

    function SEND; // dataOut is sent out
        write = 1'b1;
    endfunction