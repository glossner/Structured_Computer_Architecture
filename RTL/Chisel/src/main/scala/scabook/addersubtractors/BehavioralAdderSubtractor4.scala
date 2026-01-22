// Licensed under the Solderpad Hardware License v 2.1  
// See: https://solderpad.org/licenses/SHL-2.1/

package scabook.addersubtractors

import chisel3._

class BehavioralAdderSubtractor4 extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(4.W))    
    val b = Input(UInt(4.W))    
    val subtract = Input(UInt(1.W))
    val sum = Output(UInt(4.W)) 
  })

  // Directly use the BehavioralAdder module with UInt
  io.sum := BehavioralAdderSubtractor(io.a, io.b, io.subtract, 4)
}
