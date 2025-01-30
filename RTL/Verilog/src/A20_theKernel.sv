            cHALT;      NOP;
            cJMP(1);    NOP; // hSTART
            cJMP(2);    NOP; // hSTOP
            cJMP(3);    NOP; // hINTRQ
            cJMP(4);    NOP; // hSQGENX(d)
            cJMP(5);    NOP; // hSQGENN
            cJMP(6);    NOP; // hMSEND(addr-1,size)
            cJMP(7);    NOP; // hMGET(addr-1,size)
            cJMP(8);    NOP; // hMAIN(addr-1)
            cJMP(9);    NOP; // hSQADD(dest,left,right)
            cJMP(10);   NOP; // hVGENX(addr)
            cJMP(11);   NOP; // hVGENN(addr,value)
            cJMP(12);   NOP; // hSQMVMULT(matrix,vectror,dest)
            cJMP(13);   NOP; // hSQMMULT(dest,left,right)
            cJMP(14);   NOP; // hSQMMAC(dest,left,right)
            cJMP(15);   NOP; // hTRANS(dest,left)
            cJMP(16);   NOP; // hPRANDOM(dest)
//********** START COUNTER *******************************************
    LB(1);  cSTART;         VLOAD(22);
            cJMP(32);       NOP;
//********** STOP COUNTER ********************************************
    LB(2);  cSTOP;          VLOAD(33);
            cJMP(32);       NOP;
//********** INTERRUPT REQEST ****************************************
    LB(3);  cSETINT;        VLOAD(44);
            cJMP(32);       NOP;
//********** SQUARE MATRIX X GENERATE ********************************
    LB(4);  cPARAM;         NOP;
            cNOP;           CLOAD;
            cVLOAD(`p-1);   VSUB(1);
            cNOP;           ADDRLD;
            cNOP;           IXLOAD;
    LB(17); cNOP;           RISTORE(1);
            cBRNZDEC(17);   VADD(1);
            cJMP(32);       NOP;
//********** SQUARE MATRIX N GENERATE ********************************
    LB(5);  cPARAM;         NOP;
            cNOP;           CLOAD;
            cVLOAD(`p-1);   VSUB(1);
            cNOP;           ADDRLD;
            cNOP;           VLOAD(0);
    LB(18); cNOP;           RISTORE(1);
            cBRNZDEC(18);   VADD(1);
            cJMP(32);       NOP;
//********** SEND MATRIX *********************************************
    LB(6);  cPARAM;         NOP;
            cPARAM;         CLOAD;
            cNOP;           ADDRLD;
            cNOP;           NOP;
            cNOP;           RISENDIO(1);
    LB(19); cNOP;           NOP;
            cBRZDEC(32);    NOP;
            cNOP;           NOP;
            cNOP;           NOP;
            cNOP;           NOP;
            cDATAEXT(`p/2); NOP;
            cJMP(19);       RISENDIO(1);
//********** GET MATRIX **********************************************
    LB(7);  cPARAM;         GETIO(0);
            cPARAM;         CLOAD;
            cNOP;           NOP;
            cNOP;           NOP;
            cNOP;           NOP;
            cNOP;           ADDRLD;
    LB(20); cDATAINS(`p/2); NOP;
            cNOP;           RIGETIO(1);
            cBRZDEC(32);    NOP;
            cNOP;           NOP;
            cNOP;           NOP;
            cNOP;           NOP;
            cJMP(20);       NOP;
//********** MAIN MATRIX GENERATE ************************************
    LB(8);  cPARAM;         NOP;
            cNOP;           CLOAD;
            cPARAM;         ADDRLD;
            cNOP;           IXLOAD;
            cNOP;           WHEREZERO;
            cNOP;           CLOAD;
            cNOP;           ELSEWHERE;
            cNOP;           VLOAD(0);
            cNOP;           ENDWHERE;
            cVLOAD(`p-2);   SENDSR;
            cGRSHIFT;       RSTORE(0);
    LB(22); cNOP;           GETSR;
            cGRSHIFT;       RISTORE(1);
            cBRNZDEC(22);   NOP;
            cJMP(32);       NOP;
//********** ADD SQUARE MATRICES *************************************
    LB(9);  cPARAM;         NOP;
            cSTORE(3);      NOP;    // dest at mem[3]
            cPARAM;         NOP;
            cSTORE(4);      NOP;    // left at mem[4]
            cPARAM;         NOP;
            cSTORE(5);      NOP;    // right at mem[5]
            cSUB(4);        NOP;
            cSTORE(0);      NOP;    // right-left at mem[0]
            cLOAD(3);       NOP;
            cSUB(5);        NOP;
            cSTORE(1);      NOP;    // dest-right at mem[1]
            cLOAD(4);       NOP;
            cSUB(3);        NOP;
            cVADD(1);       NOP;
            cSTORE(2);      NOP;    // left-dest+1 at mem[2]
            cVLOAD(`p);     NOP;
            cSTORE(6);      NOP;
    LB(23); cLOAD(4);       NOP;
            cADD(0);        CALOAD;
            cADD(1);        CAADD;
            cADD(2);        CSTORE;
            cSTORE(4);      NOP;
            cLOAD(6);       NOP;
            cVSUB(1);       NOP;
            cSTORE(6);      NOP;
            cBRNZ(23);      NOP;
            cJMP(32);       NOP;
//************ INDEX VECTOR GENERATE *********************************
    LB(10); cPARAM;         IXLOAD;
            cJMP(32);       CSTORE;
//************ N VECTOR GENERATE *************************************
    LB(11); cPARAM;         NOP;
            cPARAM;         CLOAD;
            cJMP(32);       CSTORE;
//************ MATRIX-VECTOR MULTIPLY ********************************
    LB(12); cPARAM;                 NOP;
            cVADD(`p-1);            NOP;
            cPARAM;                 CLOAD;
            cSTORE(0);              VADD(1);    // mem[0] = right addr
            cPARAM;                 ADDRLD;     // matrix end addr + 1
            cSTORE(1);              REDADD;     // mem[1] = dest addr
            cVLOAD(`p-1);           RILOAD(-1);
    LB(24); cLREDINS;               MULT(`p);
            cBRNZDEC(24);           RILOAD(-1);
            cVLOAD($clog2(`p)-2);   NOP;
    LB(27); cBRNZDEC(27);           NOP;
            cLOAD(1);               GETSR;
            cJMP(32);               CSTORE;
//********** MULTIPLY SQUARE MATRICES ********************************
    LB(13); cPARAM;                 REDADD;
            cVSUB(1);               NOP;
            cSTORE(3);              NOP;    // dest => 3
            cPARAM;                 NOP;
            cVADD(`p-1);            NOP;
            cSTORE(0);              NOP;    // left => 0
            cPARAM;                 CLOAD;
            cSTORE(2);              ADDRLD; // right => 2
            cVLOAD(`p);             NOP;
            cSTORE(1);              NOP;
            cLOAD(2);               NOP;
            cVADD(1);               CALOAD;
            cSTORE(2);              STORE(3*`p);
            cVLOAD(`p-1);           RILOAD(0);
    LB(25); cLREDINS;               MULT(3*`p);
            cBRNZDEC(25);           RILOAD(-1);
            cVLOAD($clog2(`p)-3);   NOP;
    LB(28); cBRNZDEC(28);           NOP;
            cLOAD(3);               NOP;
            cVADD(1);               NOP;
            cSTORE(3);              GETSR;
            cLOAD(2);               CSTORE;
            cVADD(1);               CALOAD;
            cSTORE(2);              NOP;
            cLOAD(0);               STORE(3*`p);
            cLOAD(1);               CLOAD;
            cVSUB(1);               NOP;
            cBRZ(32);               ADDRLD;
            cSTORE(1);              NOP;
            cVLOAD(`p-1);           RILOAD(0);
            cJMP(25);               NOP;
//********** MULTIPLY & ACCUMULATE SQUARE MATRICES *******************
    LB(14); cPARAM;                 REDADD;
            cVSUB(1);               NOP;
            cSTORE(3);              NOP;    // dest => 3
            cPARAM;                 NOP;
            cVADD(`p-1);            NOP;
            cSTORE(0);              NOP;    // left => 0
            cPARAM;                 CLOAD;
            cSTORE(2);              ADDRLD; // right => 2
            cVLOAD(`p);             NOP;
            cSTORE(1);              NOP;
            cLOAD(2);               NOP;
            cVADD(1);               CALOAD;
            cSTORE(2);              STORE(3*`p);
            cVLOAD(`p-1);           RILOAD(0);
    LB(26); cLREDINS;               MULT(3*`p);
            cBRNZDEC(26);           RILOAD(-1);
            cVLOAD($clog2(`p)-3);   NOP;
    LB(29); cBRNZDEC(29);           NOP;
            cLOAD(3);               NOP;
            cVADD(1);               NOP;
            cSTORE(3);              GETSR;
            NOP;                    CAADD;
            cLOAD(2);               CSTORE;
            cVADD(1);               CALOAD;
            cSTORE(2);              NOP;
            cLOAD(0);               STORE(3*`p);
            cLOAD(1);               CLOAD;
            cVSUB(1);               NOP;
            cBRZ(32);               ADDRLD;
            cSTORE(1);              NOP;
            cVLOAD(`p-1);           RILOAD(0);
            cJMP(26);               NOP;
//********** TRANSPOSE ***********************************************
    LB(15); //cSTART;                   NOP;
            cPARAM;                 IXLOAD;
            cSTORE(0);              SENDSR; // mem[0]=dest
            cPARAM;                 NOP;
            cSTORE(1);              NOP;    // mem[1]= source
            cVLOAD(2*`p);           NOP;
            cSTORE(2);              NOP;    // mem[2]=temp
            cVLOAD(`p+1);           NOP;    // counter
            cSTORE(3);              NOP;
// READ ALL
    LB(33); cLOAD(1);               GETSR;
            cGRROTATE;              CADD;
            cLOAD(3);               ADDRLD;
            cVSUB(1);               NOP;
            cNOP;                   NOP;
            cBRZ(34);               NOP;
            cSTORE(3);              NOP;
            cLOAD(2);               RLOAD(0);
            cVADD(1);               CSTORE;
            cSTORE(2);              NOP;
            cJMP(33);               NOP;
// ROTATE ALL
    LB(34); cVLOAD(`p);             VLOAD(3*`p);
            cSTORE(3);              ADDRLD;
            cNOP;                   NOP;
    LB(35); cVSUB(1);               RILOAD(-1);
            cNOP;                   NOP;
            cBRZ(37);               SENDSR;
            cSTORE(3);              NOP;
    LB(36); cGLROTATE;              GETSR;
            cBRNZDEC(36);           NOP;
            cLOAD(3);               NOP;
            cJMP(35);               RSTORE(0);
// STORE ALL
    LB(37); cVLOAD(`p-1);           IXLOAD;
            cSTORE(3);              SENDSR;
            cGRROTATE;              NOP;
    LB(38); cLOAD(0);               GETSR;
            cLOAD(3);               CADD;
            cVADD(2*`p);            ADDRLD;
            cGRROTATE;              CALOAD;
            cLOAD(3);               RSTORE(0);
            cNOP;                   NOP;
            cBRZDEC(32);            NOP;
            cSTORE(3);              NOP;
            cJMP(38);               NOP;
//********** PSEUDO-RANDOM MATRIX ************************************
    LB(16); cPARAM;                 ACTIVATE;
            cNOP;                   CLOAD;
            cNOP;                   ADDRLD;
            cVLOAD(`p-1);           IXLOAD;
    LB(33); cNOP;                   VADD(29);
            cNOP;                   VMULT(98765);
            cNOP;                   SHR;
            cNOP;                   SHR;
            cNOP;                   SHR;
            cNOP;                   SHR;
            cNOP;                   SHR;
            cNOP;                   VAND(31);
            cNOP;                   CRSTORE;
            cBRNZDEC(33);           NOP;
            cJMP(32);               NOP;