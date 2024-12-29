// input codes
    `define a 1'b1
    `define b 1'b0
// output codes	
    `define recA    2'b10 // automaton receives a
    `define recB    2'b01 // automaton receives b
    `define fail    2'b00 // automaton failed to receive correct string
// internal states
    `define init    2'b10 // initial state
    `define runA    2'b11 // automaton received at least one b
    `define runB    2'b01 // automaton received at least one a
    `define eror    2'b00 // fail state