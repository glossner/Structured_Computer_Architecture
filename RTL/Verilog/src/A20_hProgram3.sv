        hVALUE(0,0);
        hPSEND(0,0);

        hSQGENX;
        hVGENN(3,`p);
        hSTART;
        hSQMVMULT(`p-1, `p, `p+1);
        hSTOP;

        hHALT;
