            VAL(0,1)    ; // loads the address 1 in rf[0]
            VAL(1,55)   ; // loads the value 55 in rf[1]
            STORE(0,1)  ; // store at the address 1 the value 55
            READ(0)     ; // reads from address stored in rf[0]
            LOAD(2)     ; // load 55, read for address 1, in fr[2]
            HALT        ;