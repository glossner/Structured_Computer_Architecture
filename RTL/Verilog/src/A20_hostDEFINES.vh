// CONTROL INSTRUCTIONS
`define hnop     6'b000000 // pc <= pc+1
`define hjmp     6'b000001 // pc <= pc+val
`define hjmpz    6'b000010 // pc <= (rf[left]==0) ? pc+val:pc+1
`define hjmpnz   6'b000011 // pc <= (rf[left]==0) ? pc+1:pc+val
`define hpsend   6'b100000 // pc <= (host2int[31:24]==
                           // {`prun,`ctl}) ? pc+1:pc; h2iPwrite=1;
                           // rf[left] <= rf[left]+!i2hPfull
`define hdget    6'b100010 // pc <= (leftOut==rightOut) ? pc+1:pc;
                           // i2hDread = 1;
                           // rf[left] <= rf[left]+!i2hDfull
`define hdsend   6'b100001 // pc <= (leftOut==rightOut) ? pc+1pc;
                           // h2iDwrite = 1;
                           // rf[left] <= rf[left]+!i2hDfull
`define hintwait 6'b000111 // pc <= (int) ? pc+1:pc
`define hajmp    6'b001000 // pc <= val
`define hhalt    6'b001001 // pc <= pc
`define hsttcc   6'b001010 // start host cycle counter
`define hstpcc   6'b001011 // stop host cycle counter

// FUNCTIONAL INSTRUCTIONS
`define hadd     6'b010000
    // {cr,rf[dest]} <= rf[left]+rf[right]
`define haddcr   6'b010001
    // {cr,rf[dest]} <= rf[left]+rf[right]+cr
`define hsub     6'b010010
    // {cr,rf[dest]} <= rf[left]-rf[right]
`define hsubcr   6'b010011
    // {cr,rf[dest]} <= rf[left]-rf[right]-cr
`define hmult    6'b010100
    // {cr,rf[dest]} <= rf[left]*rf[right]
`define hbwand   6'b010101
    // {cr,rf[dest]} <= {cr, rf[left] & rf[right]}
`define hbwor    6'b010110
    // {cr,rf[dest]} <= {cr, rf[left] | rf[right]}
`define hbwxor   6'b010111
    // {cr,rf[dest]} <= {cr, rf[left] ^ rf[right]}
`define haddv    6'b011000
    // {cr,rf[dest]} <= rf[left]+{{16{val[15}},val}
`define haddcrv  6'b011001
    // {cr,rf[dest]} <= rf[left]+{{16{val[15}},val}+cr
`define hsubv    6'b011010
    // {cr,rf[dest]} <= rf[left]-{{16{val[15}},val}
`define hsubcrv  6'b011011
    // {cr,rf[dest]} <= rf[left]-{{16{val[15}},val}-cr
`define hmultv   6'b011100
    // {cr,rf[dest]} <= rf[left]*{{16{val[15}},val}
`define hbwandv  6'b011101
    // {cr,rf[dest]} <= {cr,rf[left]&{{16{val[15}},val}}
`define hbworv   6'b011110
    // {cr,rf[dest]} <= {cr,rf[left]|{{16{val[15}},val}}
`define hbwxorv  6'b011111
    // {cr,rf[dest]} <= {cr,rf[left]^{{16{val[15}},val}}

// DATA TRANSFER INSTRUCTIONS
`define hfsend   6'b100011 // function send
    // {code[5:0], `prun, `ctl, initAddress}
`define hssend   6'b100100 // scalar send
    // {code[5:0], scalar}
`define hvalue   6'b101000
    // {cr,rf[dest]} <= {cr, {{16{val[15}},val}}
`define hinsval  6'b101001
    // {cr,rf[dest]} <= {cr, {rf[left][15:0],val}
`define hload    6'b101010
    // {cr,rf[dest]} <= {cr, hDmem[rf[left]}
`define hstore   6'b111011
    // hDmem[rf[left] <= rf[right]