        hVALUE(0,0);
        hPSEND(0,0);	
        hSQGENX(0);           // generate matrix X
        hMAIN(`p,1);          // generate matrix UNIT
        hSTART;
        hSQMMULT(2*`p,`p,0);  // multiply matrices
        hSTOP;	
        hHALT;
			
			
