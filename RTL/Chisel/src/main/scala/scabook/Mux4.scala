// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook

import chisel3._

/** 4-to-1 multiplexer operating on 8-bit buses,
  * specializing the parameterized Multiplexer base class.
  */
class Mux4 extends Multiplexer(n = 4, width = 8)
