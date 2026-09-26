// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.addersubtractors

import chisel3._

/** 4-bit two's complement adder/subtractor specializing BehavioralAdderSubtractorHW. */
class BehavioralAdderSubtractorHW4
  extends BehavioralAdderSubtractorHW(width = 4)
