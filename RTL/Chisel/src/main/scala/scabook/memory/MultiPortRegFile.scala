// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.memory

import chisel3._
import chisel3.util._

class MultiPortRegFile(numRegs: Int, width: Int, numReadPorts: Int, numWritePorts: Int, numBanks: Int) extends Module {
  require(numRegs % numBanks == 0, "Number of registers must be evenly divisible by the number of banks")

  val io = IO(new Bundle {
    val readAddr  = Input(Vec(numReadPorts, UInt(log2Ceil(numRegs).W)))
    val readData  = Output(Vec(numReadPorts, UInt(width.W)))
    val writeAddr = Input(Vec(numWritePorts, UInt(log2Ceil(numRegs).W)))
    val writeData = Input(Vec(numWritePorts, UInt(width.W)))
    val writeEnable = Input(Vec(numWritePorts, Bool()))
  })

  val bankSize = numRegs / numBanks
  val regBanks = Seq.fill(numBanks)(SyncReadMem(bankSize, UInt(width.W)))

  // Read Logic: Each read port selects the appropriate bank and reads from it
  for (i <- 0 until numReadPorts) {
    val bankIndex = (io.readAddr(i) % numBanks.U).asUInt
    val bankOffset = (io.readAddr(i) / numBanks.U).asUInt
    val readData = regBanks(bankIndex.litValue.toInt).read(bankOffset, true.B)  
    io.readData(i) := RegNext(readData, 0.U)
  }

  // Write Logic: Each write port updates the appropriate bank
  for (i <- 0 until numWritePorts) {
    val bankIndex = (io.writeAddr(i) % numBanks.U).asUInt
    val bankOffset = (io.writeAddr(i) / numBanks.U).asUInt

    when(io.writeEnable(i)) {
      regBanks(bankIndex.litValue.toInt).write(bankOffset, io.writeData(i))
    }
  }
}

// Top-level wrapper instantiating an 8R/4W register file
class EightReadFourWriteRegFile extends MultiPortRegFile(
  numRegs = 64,       // Assume 64 registers
  width = 64,         // 64-bit registers
  numReadPorts = 8,   // 8 read ports
  numWritePorts = 4,  // 4 write ports
  numBanks = 4        // 4 memory banks
)
