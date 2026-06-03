    task CHSGN; // 2's coomplement
        input   [4:0]   dest    ;
        input   [4:0]   left    ;

        begin   opCode  = `chsgn;
                d       = dest  ;
                l       = left  ;
                v       = 16'b0 ;
                endLine         ;
        end
    endtask
    task MAX; // maximum
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `max          ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endtask
    task MIN; // minimum
        input   [4:0]   dest    ;
        input   [4:0]   left    ;
        input   [4:0]   right   ;

        begin   opCode  = `min          ;
                d       = dest          ;
                l       = left          ;
                v       = {right, 11'b0};
                endLine                 ;
        end
    endtask
