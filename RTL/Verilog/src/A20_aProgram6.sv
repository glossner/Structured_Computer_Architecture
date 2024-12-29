            cPLOAD(0);      ACTIVATE;
            cNOP;           GETIO(1);
            cNOP;           IXLOAD;

            `include "00_theKernel.sv"

    LB(32); cHALT;          NOP;
            cPRUN(0);       NOP;