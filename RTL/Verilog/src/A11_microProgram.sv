            D(0); LD; MOVE; WB; LL; // rf[0] <= DataIn
            D(5); LD; MOVE; WB; LL; // rf[5] <= DataIn
            D(6); LD; MOVE; WB; LL; // rf[6] <= DataIn
    LB(0);  D(1); L(0); R(0); ADD; WB; LL; // rf[1] <= rf[0]+rf[0]
            D(2); L(1); R(1); ADD; WB; LL; // rf[2] <= rf[1]+rf[1]
            D(3); L(2); R(2); ADD; WB; LL; // rf[3] <= rf[2]+rf[2]
            D(0); L(3); MOVE; WB; LL; // rf[0] <= rf[3]
            D(6); L(6); R(5); SUB; WB; ZJMP(55); LL; 
             // rf[6] <= rf[6]-rf[5]; if (rf[6]=0) jump to LB(55)
            JMP(0); LL; jump to LB(0)
    LB(55); JMP(55); LL; jump to LB(55); i.e., halt micro-program