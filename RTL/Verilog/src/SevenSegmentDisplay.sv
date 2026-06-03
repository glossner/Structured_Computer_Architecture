// Licensed under CERN-OHL-S v2. See https://cern.ch/cern-ohl for details.
module sevenSegmDys(output  logic [6:0] seg     ,
                    input   logic [3:0] number  );

    always_comb begin
        case(number)
            4'b0000: seg = 7'b1111110; // 0: a, b, c, d, e, f
            4'b0001: seg = 7'b0110000; // 1: b, c
            4'b0010: seg = 7'b1101101; // 2: a, b, g, e, d
            4'b0011: seg = 7'b1111001; // 3: a, b, g, c, d
            4'b0100: seg = 7'b0110011; // 4: f, g, b, c
            4'b0101: seg = 7'b1011011; // 5: a, f, g, c, d
            4'b0110: seg = 7'b1011111; // 6: a, f, g, e, d, c
            4'b0111: seg = 7'b1110000; // 7: a, b, c
            4'b1000: seg = 7'b1111111; // 8: All segments on
            4'b1001: seg = 7'b1111011; // 9: a, f, b, g, c, d
            default: seg = 7'b0000000; // Default: All segments off
        endcase
    end
endmodule