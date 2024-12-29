        hVALUE(0,0);
        hPSEND(0,0);
        hSQGENX;                    // generate matrix X
        hUNIT(`p-1);                // generate matrix UNIT
        hSTART;
        hSQMMULT(2*`p,(2*`p)-1,0);  // multiply matrices
        hSTOP;
        hHALT;
			
			
