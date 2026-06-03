            cPLOAD(0);      ACTIVATE;
            cNOP;           GETIO(1);
            cNOP;           IXLOAD;
    // here coomes the accelerator program
    LB(32); cHALT;          NOP;
            cPRUN(0);       NOP;