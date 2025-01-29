/*
            NOP         ;
            VAL(0,1)    ;
            VAL(1,2)    ;
            VAL(2,3)    ;
            VAL(3,4)    ;
            VAL(4,5)    ;
            ADD(0,0,1)  ;
            ADD(0,0,2)  ;
            ADD(0,0,3)  ;
            ADD(0,0,4)  ;
            HALT        ;
//*/


/*
			VAL(0,2)	;
			VAL(1,4)	;
			VAL(2,8)	;
			MULT(3,1,2)	;
			HALT		;
//*/
//*
			VAL(31,10)	;
			VAL(2,23)	;
			VAL(0,13)	;
			EI			;
			ADDV(0,0,2)	;
			NOP			;
			ADDV(0,0,4)	;
			HALT		;
			NOP			;
			NOP			;
		// subroutine triggered by interrupt
			DI			;
			VAL(3,44)	;
			RET(30)		;
//*/
/*
			VAL(0,3)	;
	LB(1);	ADDV(0,0,-1);
			NOP			;
			RJMP(1)		;
			HALT		;
//*/
/*
			VAL(0,1)	;
			VAL(1,55)	;
			STORE(0,1)	;
			READ(0)		;
			LOAD(2)		;
			HALT		;
//*/










