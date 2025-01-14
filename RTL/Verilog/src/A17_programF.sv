// Instantiating registers and adding & multiplying 
//*
            VAL(0,1)    ;
            VAL(1,2)    ;
            VAL(2,3)    ;
            VAL(3,4)    ;
            VAL(4,5)    ;
            ADD(5,4,3)  ;
            MULT(6,5,4) ;
            HALT        ;
            HALT        ;
//*/

// Adding with value
/*
            VAL(0,2)    ;
            VAL(1,4)    ;
            VAL(2,8)    ;
            MULT(3,1,2) ;
            ADDV(0,3,5) ;
            NOP         ;
            HALT        ;
            HALT        ;
//*/

// Interrupt mechanism
/*
            VAL(31,13)  ;
            VAL(2,23)   ;
            VAL(0,24)   ;
            VAL(1,13)   ;
            EI          ;
            ADDV(2,31,1);
            VAL(1,1)    ;
            VAL(2,222)  ;
            NOP         ;
            ADD(0,1,2)  ;
            HALT        ;
            HALT        ;
            NOP         ;
        // subroutine triggered by interrupt
            ADDV(30,30,-2)  ;
            VAL(3,44)       ;
            NOP             ;
            NOP             ;
            NOP             ;
            RET(30)         ;
            NOP             ;
//*/

// Conditioned branch 1
/*
            VAL(0,5)    ;
    LB(1);  ADDV(0,0,-1);
            NOP         ;
            NOP         ;
            BRNZ(0,1)   ;
            NOP         ;
            MOVE(1,0)   ;
            VAL(2,33)   ;
            HALT        ;
            HALT        ;
//*/

// Conditioned branch 2
/*
            VAL(0,-3)   ;
            NOP         ;
            NOP         ;
    LB(1);  NOP         ;
            NOP         ;
            BRNZ(0,1)   ;
            ADDV(0,0,1) ;
            HALT        ;
            HALT        ;
//*/

// Data memory use
/*
            VAL(1,55)   ;
            VAL(0,1)    ;
            NOP         ;
            NOP         ;
            STORE(0,1)  ;
            NOP         ;
            NOP         ;
            READ(2,0)   ;
            HALT        ;
            HALT        ;
//*/

// Change sign operation
/*
            VAL(2, 7);
            CHSGN(3,2);
            CHSGN(1,3);
            //NOP;
            //NOP;
            HALT;
            HALT;
//*/

// Min/max operations
/*
            VAL(0, 5);
            VAL(1, 9);
            MAX(2,0,1);
            MIN(3,0,1);
            HALT;
            HALT;

//*/

// Test if RF[0] is divisible by 4?
/*      
            VAL(0,9);
            //VAL(0,8);
            VAL(1,3);
            AND(2,0,1);
            NOP;
            NOP;
            BRZ(2,22);
            NOP;
            RJMP(33);
            VAL(3,0);
    LB(22); VAL(3,1);
    LB(33); HALT;
            HALT;
//*/    //yes/no: rf[3] = 1/0.

// Absolute difference 
/*
            VAL(0,5);
            VAL(1,9);
            ASUB(2,0,1);
            ASUB(3,1,0);
            HALT;
            HALT;
//*/

/*
            VAL(1,-1);
            LSH(1,1);
            NOT(1,1); // mask in r[1]
            //VAL(0,9);
            VAL(0,11);
    LB(33); ADDV(0,0,-3);
            AND(2,1,0); // sgn in r[2]
            NOP;
            NOP;
            BRZ(2,33);
            NOP;
            ADDV(0,0,3);
            NOP;
            NOP;
            HALT;
            HALT;

//*/