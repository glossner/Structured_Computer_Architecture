            cPLOAD(0);      ACTIVATE;
            cNOP;           GETIO(1);
            cNOP;           IXLOAD;
  // initialization of values
            cVLOAD(0);      NOP; // initial value
            cSTORE(1);      NOP; // cMem[1] <= initial value
            cVLOAD(`p);     NOP; // number of scalars
            cSTORE(0);      NOP; // cMem[0] <= number of scalars
            cVLOAD(1);      NOP; // initial pointer
            cADDRLD;        NOP; // cAddr <= initial pointer
  // main loop
    // addition
    LB(11); cLOAD(1);       NOP;
            cRIADD(1);      NOP;
            cSTORE(1);      NOP;
    // control
            cLOAD(0);       NOP;
            cVSUB(1);       NOP;
            cSTORE(0);      NOP;
            cBRNZDEC(11);   NOP;
  // end main loop
    LB(32); cHALT;          NOP;
            cPRUN(0);       NOP;
			

