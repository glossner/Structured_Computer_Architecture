// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook

import chisel3._

/** 1-to-16 demultiplexer operating on 64-bit buses,
  * specializing the parameterized Demultiplexer base class.
  */
class DeMux16 extends Demultiplexer(numOutputs = 16, width = 64)