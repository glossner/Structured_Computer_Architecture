module array(   input   logic [63:0]    i2aData     ,
                output  logic [63:0]    a2i         ,
                input   logic [2:0]     dataTransCom,
                input   logic [`n+12:0] contr2array ,
                output  logic [`n-1:0]  red         ,
                input   logic           clock       );
// dataTransCom : 000 : nop
// dataTransCom : 100 : even insert
// dataTransCom : 101 : odd insert
// dataTransCom : 110 : even extract
// dataTransCom : 111 : odd extract
    logic [`n+1:0]  map2reduce[0:`p-1]  ; // func[1:0], acc
    logic [0:`p-1]  boolean             ;
    logic [0:`p-1]  first               ;
    logic [0:`p-1]  next                ;
    logic [`n+1:0]  red2contr           ;

    map map(.int2accData    (i2aData        ),
            .a2i            (a2i            ),
            .dataTransCom   (dataTransCom   ),
            .contr2array    (contr2array    ),
            .map2reduce     (map2reduce     ),
            .red            (red            ),
            .boolean        (boolean        ),
            .first          (first          ),
            .next           (next           ),
            .clock          (clock          ));
    scan SCAN(  boolean ,
                first   ,
                next    ,
                clock   );
    reduce REDUCE(  red2contr   ,
                    map2reduce  ,
                    clock       );

    assign red = red2contr[`n-1:0]                              ;
endmodule