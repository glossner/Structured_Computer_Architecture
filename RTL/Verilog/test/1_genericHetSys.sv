/************************************************************
File: 1_hetSys.sv
Name: Generic Heterogenous System
Description:
************************************************************/
module hetSys(	input	logic reset, clock	);
	logic [63:0]	h2aProgData	; // program & data
	logic 			h2aProgWrite;
	logic 			a2hProgFull	;
	logic 			h2aDataWrite;
	logic 			a2hDataFull ;
	logic [63:0]	a2hData		;
	logic 			h2aDataRead ;
	logic 			a2hDataEmpty;
	logic 			a2hInt		;
	logic 			h2aInta		;
											
	host host(	h2aProgData	,
				h2aProgWrite,
				a2hProgFull	,
				h2aDataWrite,
				a2hDataFull ,
				a2hData		,/************************************************************
File: 1_hetSys.sv
Name: Generic Heterogenous System
Description:
************************************************************/
module hetSys(  input   logic reset, clock  );
    logic [63:0]    h2aProgData ; // program & data
    logic           h2aProgWrite;
    logic           a2hProgFull ;
    logic           h2aDataWrite;
    logic           a2hDataFull ;
    logic [63:0]    a2hData     ;
    logic           h2aDataRead ;
    logic           a2hDataEmpty;
    logic           a2hInt      ;
    logic           h2aInta     ;

    host host(  h2aProgData ,
                h2aProgWrite,
                a2hProgFull ,
                h2aDataWrite,
                a2hDataFull ,
                a2hData     ,
                h2aDataRead ,
                a2hDataEmpty,
                a2hInt      ,
                h2aInta     ,
                reset       ,
                clock       );

    accelerator accelerator(h2aProgData ,
                            h2aProgWrite,
                            a2hProgFull ,
                            h2aDataWrite,
                            a2hDataFull ,
                            a2hData     ,
                            h2aDataRead ,
                            a2hDataEmpty,
                            a2hInt      ,
                            h2aInta     ,
                            reset       ,
                            clock       );
endmodule
				h2aDataRead ,
				a2hDataEmpty,
				a2hInt		,
				h2aInta		,
				reset		, 
				clock		);
				
	accelerator accelerator(h2aProgData	,
							h2aProgWrite,
							a2hProgFull	,
							h2aDataWrite,
							a2hDataFull ,
							a2hData		,
							h2aDataRead ,
							a2hDataEmpty,
							a2hInt		,
							h2aInta		,
							reset		, 
							clock		);
endmodule				



