// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.adders

import chisel3._

/** 4-bit behavioral adder specializing BehavioralAdder. */
class BehavioralAdder4 extends BehavioralAdder(width = 4)
