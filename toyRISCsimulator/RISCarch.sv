/*************************************************************************
                    toyRISC'S ARCHITECTURE
*************************************************************************/
    NOP         // no operation
    RJMP(lb)    // relative jumpto label 'lb'
    BRZ(l,lb)   // branch if rf[l]=zero at label 'lb'
    BRNZ(l,lb)  // branch if rf[l]!=zero at label 'lb'
    RET(l)      // return from subroutine: pc<=rf[l]
    HALT        // halt until interrupt is received, pc = pc
 // for the following instructions: pc<=pc+1;
    EINT        // set enable interrupt
    DINT        // set disable interrupt
    ADD(d,l,r)  // rf[d]<=rf[l]+rf[r];
    SUB(d,l,r)  // rf[d]<=rf[l]-rf[r];
    ADDV(d,l,v)	// rf[d]<=rf[l]+v;
    MULT(d,l,r)	// rf[d]<=rf[l]*rf[r];
    MULTV(d,l,v)// rf[d]<=rf[l]*v;
    ADDC(d,l,r)	// rf[d]<=(rf[l]+rf[r]}[32];
    SUBC(d,l,r)	// rf[d]<=(rf[l]-rf[r])[32];
    ADDVC(d,l,v)// rf[d]<=(rf[l]+v)[32];
    LSH(d,l)    // rf[d]<=rf[l] >> 1;
    ASH(d,l)    // rf[d]<={rf[l][31],rf[l][31:1]};
    MOVE(d,l)   // rf[d]<=rf[l];
    SWAP(d,l)   // rf[d]<={rf[l][15:0],rf[l][31:16]};
    NOT(d,l)    // rf[d]<=~rf[l];
    AND(d,l,r)  // rf[d]<=rf[l]&rf[r];
    OR(d,l,r)   // rf[d]<=rf[l]|rf[r];
    XOR(d,l,r)  // rf[d]<=rf[l]^rf[r];
    READ(l)     // read from dataMemory[rf[l]];
    LOAD(d)     // rf[d]<=dataOut;
    STORE(l,r)  // dataMemory[rf[l]]<=rf[r];
    VAL(d,v)    // rf[d]<={{16*{v[15]}},v};