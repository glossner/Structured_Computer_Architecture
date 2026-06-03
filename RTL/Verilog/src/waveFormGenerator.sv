module waveFormGenerator();
    logic randomWave;
    logic clock     ;
    initial begin       randomWave = 0  ;
                    #2  randomWave = 1  ;
                    #6  randomWave = 0  ;
                    #4  randomWave = 1  ;
                    #8  randomWave = 0  ;
                    #5  $stop           ;
            end
    initial begin               clock = 0       ;
                    forever #2  clock = ~clock  ;
            end
endmodule