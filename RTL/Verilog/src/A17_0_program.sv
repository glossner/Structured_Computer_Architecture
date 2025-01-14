// Instantiating registers and multiplications
//*
            VAL(0,1)    ;
            VAL(1,2)    ;
            VAL(2,3)    ;
            VAL(3,4)    ;
            VAL(4,5)    ;
            MULT(2,0,1) ;
            NOP;        ;
            MULT(5,3,4) ;
            HALT        ;
            HALT        ;
//*/

// Interrupt mechanism
/*
            VAL(31,13)      ;
            VAL(2,23)       ;
            VAL(0,13)       ;
            VAL(1,13)       ;
            EI              ;
            ADDV(0,0,1)     ;
            VAL(1,1)        ;
            VAL(2,222)      ;
            NOP             ;
            ADDV(0,0,4)     ;
            HALT            ;
            HALT            ;
            NOP             ;
            // subroutine triggered by interrupt
            ADDV(30,30,-1)  ;
            NOP             ;
            NOP             ;
            NOP             ;
            RET(30)         ;
            VAL(4,33)       ;
//*/

// Conditioned jump operation
/*
            VAL(1,0)    ;
            VAL(0,3)    ;
            NOP         ;
    LB(1);  ADDV(1,1,1) ;
            BRNZ(0,1)   ;
            ADDV(0,0,-1);
            HALT        ;
            HALT        ;
//*/

// Data memory use
/*
            VAL(0,1)    ;
            VAL(1,55)   ;
            NOP         ;
            NOP         ;
            STORE(0,1)  ;
            READ(0)     ;
            LOAD(2)     ;
            HALT        ;
            HALT        ;
//*/
