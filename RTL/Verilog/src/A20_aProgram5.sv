            cPLOAD(0);              ACTIVATE;
            cNOP;                   GETIO(1);
            cNOP;                   IXLOAD;
            cNOP;                   VADD(5);
            cNOP;                   VMULT(9875);
            cNOP;                   SHR;
            cNOP;                   SHR;
            cNOP;                   SHR;
            cNOP;                   REDMAX;
            cVLOAD($clog2(`p)-1);   VAND(31);
    LB(33); cNOP;                   NOP;
            cBRNZDEC(33);           NOP;
            cCLOAD;                 NOP;
            cNOP;                   NOP;
			
			


 