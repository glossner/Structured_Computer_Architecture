            cPLOAD(0);              ACTIVATE;
            cNOP;                   GETIO(1);
            cNOP;                   IXLOAD;
    // begin add indexes
            cVLOAD($clog2(`p)-1);   REDADD;
    LB(1);  cNOP;                   NOP;
            cBRNZDEC(1);            NOP;
            cCLOAD;                 NOP;
    // end add indexes
    LB(32); cHALT;                  NOP;
            cPRUN(0);               NOP;
			

