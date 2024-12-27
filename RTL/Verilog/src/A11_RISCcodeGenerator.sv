    reg [5:0]   opCode          ;
    reg [4:0]   d               ;
    reg [4:0]   l               ;
    reg [4:0]   r               ;
    reg [15:0]  v               ;
    reg [9:0]   addrCounter     ;
    reg [9:0]   labelTab[0:1023];

    `include "DEFINES.vh"

    task endLine;
     begin
        progMemory[addrCounter][31:0] =
            {   opCode  ,
                d       ,
                l       ,
                v       }                   ;
            addrCounter = addrCounter + 1   ;
     end
    endtask

    // sets labelTab in the first pass
    // associating 'counter' with 'labelIndex'
    task LB ;
        input [5:0] labelIndex;

        labelTab[labelIndex] = addrCounter;
    endtask
    // uses the content of labelTab in the second pass
    task ULB;
        input [5:0] labelIndex;

        v = labelTab[labelIndex] - addrCounter;
    endtask

// CONTROL INSTRUCTIONS
    task NOP; // no operation
        begin   opCode  = `addv ;
                d       = 5'b0  ;
                l       = 5'b0  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endtask

    task RJMP; // relative jump
        input   [15:0]  label   ;

        begin   opCode  = `rjmp ;
                d       = 5'b0  ;
                l       = 5'b0  ;
                ULB(label)      ;
                endLine         ;
        end
    endtask

    task BRZ; // branch if zero
        input   [4:0]   left    ;
        input   [9:0]   label   ;

        begin   opCode  = `zbr  ;
                d       = 5'b0  ;
                l       = left  ;
                ULB(label)      ;
                endLine         ;
        end
    endtask

   // ...

// ARITHMETIC & LOGIC INSTRUCTIONS
    task ADD; // addition
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `add          ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endtask

   // ...
    task LSH; // logic shift with one position
        input   [4:0]   dest    ;
        input   [4:0]   left    ;

        begin   opCode  = `lsh  ;
                d       = dest  ;
                l       = left  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endtask

    // ...

    task AND; // bitwise AND
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `bwand        ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endtask

	// ...

// DATA TRANSFER INSTRUCTIONS
    task READ; // data read
        input   [4:0]   left    ;

        begin   opCode  = `read ;
                d       = 5'b0  ;
                l       = left  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endtask

    task LOAD; // data load
        input   [4:0]   dest    ;

        begin   opCode  = `load ;
                d       = dest  ;
                l       = 5'b0  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endtask

    task STORE; // data store
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `store        ;
                d       = 5'b0          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endtask

   // ...
   
    // RUNNING
    initial begin   addrCounter = 0;
                    `include "program.sv" // first pass
                    addrCounter = 0;
                    `include "program.sv" // second pass
            end