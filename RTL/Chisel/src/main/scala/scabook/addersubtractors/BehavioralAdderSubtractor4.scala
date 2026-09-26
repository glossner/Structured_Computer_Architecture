// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.addersubtractors

import chisel3._

/** 4-bit behavioral adder/subtractor specializing BehavioralAdderSubtractor. */
class BehavioralAdderSubtractor4
  extends BehavioralAdderSubtractor(width = 4)
