            cPLOAD(0);      ACTIVATE;
            cNOP;           GETIO(1);
            cNOP;           IXLOAD;
            cNOP;           VAND(1);
            cNOP;           WHEREZERO;
            cNOP;           IXLOAD;
            cNOP;           VMULT(2);
            cNOP;           ELSEWHERE;
            cNOP;           IXLOAD;
            cNOP;           VMULT(3);
            cNOP;           ENDWHERE;
    LB(32); cHALT;          NOP;
            cPRUN(0);       NOP;


 