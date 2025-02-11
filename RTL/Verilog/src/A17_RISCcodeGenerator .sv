    reg [5:0]   opCode          ;
    reg [4:0]   d               ;
    reg [4:0]   l               ;
    reg [4:0]   r               ;
    reg [15:0]  v               ;
    reg [9:0]   addrCounter     ;
    reg [9:0]   labelTab[0:1023];

    function endLine;
     begin
		// for the pipelined version
		//dut.memSys.progMemory[addrCounter][31:0] =
        progMemory[addrCounter][31:0] = // for the generic version
            {   opCode  ,
                d       ,
                l       ,
                v       }                   ;
            addrCounter = addrCounter + 1   ;
     end
    endfunction

    // sets labelTab in the first pass
    // associating 'counter' with 'labelIndex'
    function LB ;
        input [5:0] labelIndex;

        labelTab[labelIndex] = addrCounter;
    endfunction
    // uses the content of labelTab in the second pass
    function ULB;
        input [5:0] labelIndex;

        v = labelTab[labelIndex] - addrCounter;
    endfunction

// CONTROL INSTRUCTIONS
    function NOP; // no operation
        begin   opCode  = `addv ;
                d       = 5'b0  ;
                l       = 5'b0  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

    function RJMP; // relative jump
        input   [15:0]  label   ;

        begin   opCode  = `rjmp ;
                d       = 5'b0  ;
                l       = 5'b0  ;
                ULB(label)      ;
                endLine         ;
        end
    endfunction

    function BRZ; // branch if zero
        input   [4:0]   left    ;
        input   [9:0]   label   ;

        begin   opCode  = `zbr  ;
                d       = 5'b0  ;
                l       = left  ;
                ULB(label)      ;
                endLine         ;
        end
    endfunction

    function BRNZ; // branch if not zero
        input   [4:0]   left    ;
        input   [9:0]   label   ;

        begin   opCode  = `nzbr ;
                d       = 5'b0  ;
                l       = left  ;
                ULB(label)      ;
                endLine         ;
        end
    endfunction

    function RET; // return from subroutine
        input   [4:0] left  ;

        begin   opCode  = `ret  ;
                d       = 5'b0  ;
                l       = left  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

    function HALT; // halt running
        begin   opCode  = `halt ;
                d       = 5'b0  ;
                l       = 5'b0  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

    function EI; // enable interrupt
        begin   opCode  = `eint ;
                d       = 5'b0  ;
                l       = 5'b0  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

    function DI; // disable interrupt
        begin   opCode  = `dint ;
                d       = 5'b0  ;
                l       = 5'b0  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

// ARITHMETIC & LOGIC INSTRUCTIONS
    function ADD; // addition
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `add          ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endfunction

    function SUB; // subtract
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `sub          ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endfunction

    function ADDV; // addition with value
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [15:0]  value   ;

        begin   opCode  = `addv ;
                d       = dest  ;
                l       = left  ;
                v       = value ;
                endLine         ;
        end
    endfunction

    function MULT; // multiplication
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `mult         ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endfunction

    function MULTV; // multiplication with value
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [15:0]  value   ;

        begin   opCode  = `multv;
                d       = dest  ;
                l       = left  ;
                v       = value ;
                endLine         ;
        end
    endfunction

    function ADDC; // carry from addition
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `addc         ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endfunction

    function SUBC; // carry from subtract
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `subc         ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endfunction

    function ADDVC; // carry from addition with value
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [15:0]  value   ;

        begin   opCode  = `addvc;
                d       = dest  ;
                l       = left  ;
                v       = value ;
                endLine         ;
        end
    endfunction

    function LSH; // logic shift with one position
        input   [4:0]   dest    ;
        input   [4:0]   left    ;

        begin   opCode  = `lsh  ;
                d       = dest  ;
                l       = left  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

    function ASH; // arithmetic shift with one porition
        input   [4:0]   dest    ;
        input   [4:0]   left    ;

        begin   opCode  = `ash  ;
                d       = dest  ;
                l       = left  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

    function MOVE; // data move inside register file
        input   [4:0]   dest    ;
        input   [4:0]   left    ;

        begin   opCode  = `move ;
                d       = dest  ;
                l       = left  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

    function SWAP; // swap in register
        input   [4:0]   dest    ;
        input   [4:0]   left    ;

        begin   opCode  = `swap ;
                d       = dest  ;
                l       = left  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

    function NOT; // bitwise NOT
        input   [4:0]   dest    ;
        input   [4:0]   left    ;

        begin   opCode  = `bwnot;
                d       = dest  ;
                l       = left  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

    function AND; // bitwise AND
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `bwand        ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endfunction

    function OR; // bitwise OR
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `bwor     ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endfunction

    function XOR; // bitwise XOR
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `bwxor        ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endfunction

// DATA TRANSFER INSTRUCTIONS
    function READ; // data read
        input   [4:0]   left    ;

        begin   opCode  = `read ;
                d       = 5'b0  ;
                l       = left  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

    function LOAD; // data load
        input   [4:0]   dest    ;

        begin   opCode  = `load ;
                d       = dest  ;
                l       = 5'b0  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endfunction

    function STORE; // data store
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `store        ;
                d       = 5'b0          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endfunction

    function VAL; // value load
        input   [4:0]   dest    ;
        input   [15:0]  value   ;

        begin   opCode  = `val  ;
                d       = dest  ;
                l       = 5'b0  ;
                v       = value ;
                endLine         ;
        end
    endfunction
//*
    // RUNNING FOR GENERIC toyRISC
    initial begin   addrCounter = 0;
                    `include "program.sv" // first pass
                    addrCounter = 0;
                    `include "program.sv" // second pass
            end
//*/
/*
     // RUNNING FOR PIPELINED toyRISC
    initial begin   addrCounter = 0;
                    `include "0_program.sv" // first pass
                    addrCounter = 0;
                    `include "0_program.sv" // second pass
            end
//*/