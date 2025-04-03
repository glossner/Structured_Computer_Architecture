        hVALUE(0,0);
        hPSEND(0,0);

        hSQGENX(0);             // generate matrix X at 0
        hVGENN(3,`p);           // generate constant vector
        hSTART;                 // start cycles counter
        hSQMVMULT(`p+1, 0, `p); // matrix-vector multiply
        hSTOP;                  // stop cycle counters

        hHALT;