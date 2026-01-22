// Licensed under CERN-OHL-S v2. See https://cern.ch/cern-ohl for details.
/**********************************************************************
File name: waveFormGenerator.sv
Circuit name: no circuit, only wave formes
Description: two waveforms are generated, a "random" one and 
             a periodical one: a clock signal
**********************************************************************/
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