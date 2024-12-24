module WaveFormGenerator (
	clock,
	reset,
	io_randomWave,
	io_clockWave
);
	input clock;
	input reset;
	output wire io_randomWave;
	output wire io_clockWave;
	wire [7:0] _GEN = 8'h0a;
	reg [2:0] randomIndex;
	reg clockToggle;
	always @(posedge clock)
		if (reset) begin
			randomIndex <= 3'h0;
			clockToggle <= 1'h0;
		end
		else begin
			randomIndex <= (randomIndex == 3'h4 ? 3'h0 : randomIndex + 3'h1);
			clockToggle <= ~clockToggle;
		end
	initial begin : sv2v_autoblock_1
		reg [31:0] _RANDOM [0:0];
	end
	assign io_randomWave = _GEN[randomIndex];
	assign io_clockWave = clockToggle;
endmodule
