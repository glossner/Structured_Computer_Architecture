// Generate the binary content of the ROM
    reg [1:0]   mode            ;
    reg         test            ;
    reg [1:0]   field1          ;
    reg {2:0]   field2          ;
    reg [1:0]   field3          ;
    reg [7:0]   field4          ;
    reg [4:0]   address         ;
    reg [4:0]   counter         ;
    reg [4:0]   labelTab[0:31]  ;

    task eol;   // end of line
      begin
        mem[counter] =
          {mode, address, test, field1, field2, field3, field4};
        mode    = 0	;
        test    = 0 ;
        field1  = 0 ;
        field2  = 0 ;
        field3  = 0 ;
        field4  = 0 ;
        address = 0 ;
        counter = counter + 1;
      end
    endtask

    // loads labelTab in the first pass
    task lb; input [4:0] labelIndex;
        labelTab[labelIndex] = counter; endtask

    // commands
    task com11  ; field1   = 2'b01;     endtask
    task com12  ; field1   = 2'b10;     endtask
    task com13  ; field1   = 2'b11;     endtask
    task com21  ; field2   = 3'b001;    endtask
    task com22  ; field2   = 3'b010;    endtask
    task com23  ; field2   = 3'b011;    endtask
    task com24  ; field2   = 3'b100;    endtask
    task com31  ; field3   = 2'b01;     endtask
    task com32  ; field3   = 2'b10;     endtask
    task val; input [7:0] value; field4 = value; endtask

    // transition modes
    //task inc ; mode = 2'b00; endLine; end endtask
    task jmp ; input [4:0] label;
        begin mode = 2'b01; address = labelTab[label]; end
    endtask
    task cjmp; input [4:0] label;
        begin mode = 2'b10; address = labelTab[label]; end
    endtask
    task init; mode = 2'b11; endtask

    // flags selection
    task flag1  ; test = 1'b0; endtask
    task flag2 	; test = 1'b1; endtask

    initial begin   counter = 0;
                    mode    = 0 ;
                    test    = 0 ;
                    field1  = 0 ;
                    field2  = 0 ;
                    field3  = 0 ;
                    field4  = 0 ;
                    address = 0 ;
                    `include "theDefinition.v"; // first assembly pass
                    `include "theDefinition.v"; // second assembly pass
            end