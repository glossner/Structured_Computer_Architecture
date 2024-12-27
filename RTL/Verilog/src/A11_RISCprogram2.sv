            VAL(31,10)  ; // loads in register 31 the subrutine address
            VAL(2,23)   ;
            VAL(0,13)   ;
            EI          ; // enable the action of the interrupt
            ADDV(0,0,2) ;
            NOP         ;
            ADDV(0,0,4) ;
            HALT        ;
            NOP         ;
            NOP         ;
// subroutine triggered by interrupt: loads 44 in register 3
            DI          ; // disable the action of the interrupt
            VAL(3,44)   ;
            RET(30)     ; // jump to the interrupted instruction