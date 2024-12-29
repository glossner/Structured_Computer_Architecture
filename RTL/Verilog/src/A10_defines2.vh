// input independent signals
    `define eint    2'b01 // enable interrupt instruction
    `define dint    2'b10 // disable interrupt instruction
    `define int     1'b1  // external interrupt signal
// output codes	
    `define idle    2'b00 // idle state enabled or disabled
    `define inta    2'b01 // interrupt acknowledge
    `define flush   2'b10 // flush the pipe command
// internal states
    `define enableState  ...
    `define disableState ...
    // ...