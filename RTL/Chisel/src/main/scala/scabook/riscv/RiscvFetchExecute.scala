// Licensed under the Solderpad Hardware License v 2.1
// See: https://solderpad.org/licenses/SHL-2.1/
package scabook.riscv

import chisel3._, chisel3.util._
import scabook.automata.{ProgramCounter, RALU}
import scabook.memory.RiscvDataMemory

/** Instruction memory bus port (Harvard Loop 4). */
class ImemPort(val addrWidth: Int = 32, val dataWidth: Int = 32) extends Bundle {
  val addr = Output(UInt(addrWidth.W))
  val inst = Input(UInt(dataWidth.W))
}

/** Byte-addressable data memory bus port (Harvard Loop 5). */
class DmemPort(val addrWidth: Int = 32, val dataWidth: Int = 32) extends Bundle {
  val addr      = Output(UInt(addrWidth.W))
  val funct3    = Output(UInt(3.W))
  val memRead   = Output(Bool())
  val memWrite  = Output(Bool())
  val writeData = Output(UInt(dataWidth.W))
  val readData  = Input(UInt(dataWidth.W))
}

/** RISC-V RV32I_Zmmul 5-OS Harvard Fetch-Execute Processor Core.
  *
  * Modularly integrates library components developed across prior chapters:
  *   - ProgramCounter: Program counter automaton (Chapter 10, Loop 2 & Loop 3)
  *   - RiscvDecoder: Instruction decoder & immediate generator (Chapter 8)
  *   - RALU: Executive subsystem (Chapter 10) combining RiscvRegFile (Chapter 9)
  *     and RiscvALU (Chapter 8) (Loop 1 & Loop 2)
  *
  * Executes each instruction in the classic Fetch-Execute cycle as unified
  * logical functions without stall delays using unbuffered memories.
  */
class RiscvFetchExecute(val xlen: Int = 32, val initPC: BigInt = 0x80000000L, val enableZmmul: Boolean = true) extends Module {
  val io = IO(new Bundle {
    val imem     = new ImemPort(xlen, xlen)
    val dmem     = new DmemPort(xlen, xlen)
    val pc       = Output(UInt(xlen.W))
    val inst     = Output(UInt(xlen.W))
    val aluOut   = Output(UInt(xlen.W))
    val regWrite = Output(Bool())

    // Architectural Profiling Signals
    val instRetired   = Output(Bool())
    val isBranch      = Output(Bool())
    val isBranchTaken = Output(Bool())
    val isJal         = Output(Bool())
    val isJalr        = Output(Bool())
  })

  // Subsystem Instantiation (Reusing Prior Library Elements)
  val pc      = Module(new ProgramCounter(width = xlen, initPC = initPC))
  val decoder = Module(new RiscvDecoder)
  val ralu    = Module(new RALU(width = xlen, enableZmmul = enableZmmul))

  // ==========================================
  // FUNCTION 1: INSTRUCTION FETCH (IF)
  // ==========================================
  io.imem.addr := pc.io.pc
  val inst      = io.imem.inst

  // ==========================================
  // FUNCTION 2: INSTRUCTION DECODE (ID)
  // ==========================================
  decoder.io.inst := inst
  val d = decoder.io.decoded
  val c = d.control
  val f = RiscvFields(inst)

  // ==========================================
  // FUNCTION 3: OPERAND FETCH & ALU EXECUTION (EX)
  // ==========================================
  ralu.io.rs1Addr  := d.rs1
  ralu.io.rs2Addr  := d.rs2
  ralu.io.rdAddr   := d.rd
  ralu.io.useImm   := c.aluSrc
  ralu.io.immVal   := d.imm
  ralu.io.aluOp    := c.aluOp
  ralu.io.memToReg := true.B

  // Branch condition evaluation
  val branchCond = MuxCase(false.B, Seq(
    (f.funct3 === "b000".U) -> ralu.io.zeroFlag,
    (f.funct3 === "b001".U) -> !ralu.io.zeroFlag,
    (f.funct3 === "b100".U) -> ralu.io.lessThanFlag,
    (f.funct3 === "b101".U) -> !ralu.io.lessThanFlag,
    (f.funct3 === "b110".U) -> ralu.io.lessThanUFlag,
    (f.funct3 === "b111".U) -> !ralu.io.lessThanUFlag
  ))
  val branchTaken = c.branch && branchCond
  val isJal       = (f.opcode === RiscvOpcodes.JAL)
  val isJalr      = (f.opcode === RiscvOpcodes.JALR)
  val isLui       = (f.opcode === RiscvOpcodes.LUI)
  val isAuipc     = (f.opcode === RiscvOpcodes.AUIPC)

  // ==========================================
  // FUNCTION 4: DATA MEMORY & WRITE-BACK (MEM/WB)
  // ==========================================
  io.dmem.addr      := ralu.io.aluResult
  io.dmem.funct3    := f.funct3
  io.dmem.memRead   := c.memRead
  io.dmem.memWrite  := c.memWrite
  io.dmem.writeData := ralu.io.rs2Data

  val wbData = MuxCase(ralu.io.aluResult, Seq(
    c.memToReg -> io.dmem.readData,
    c.jump     -> (pc.io.pc + 4.U),
    isLui      -> d.imm,
    isAuipc    -> (pc.io.pc + d.imm)
  ))
  ralu.io.extData  := wbData
  ralu.io.regWrite := c.regWrite

  // ==========================================
  // FUNCTION 5: NEXT PROGRAM COUNTER GENERATION (PC)
  // ==========================================
  val target = Mux(isJalr, ralu.io.aluResult, pc.io.pc + d.imm)
  pc.io.branchImm  := d.imm
  pc.io.jalrTarget := target
  pc.io.mode := Mux(branchTaken || isJal || isJalr,
    ProgramCounter.Mode.Jalr,
    ProgramCounter.Mode.Plus4
  )

  // Observability
  io.pc            := pc.io.pc
  io.inst          := inst
  io.aluOut        := ralu.io.aluResult
  io.regWrite      := ralu.io.regWrite
  io.instRetired   := true.B
  io.isBranch      := c.branch
  io.isBranchTaken := branchTaken
  io.isJal         := isJal
  io.isJalr        := isJalr
}

/** Complete Harvard 5-OS Computing System Harness.
  * Reuses RiscvDataMemory (ByteMemory) from Chapter 9.
  */
class RiscvSystem(val program: Seq[BigInt], val memWords: Int = 1024, val initPC: BigInt = 0) extends Module {
  val io = IO(new Bundle {
    val pc       = Output(UInt(32.W))
    val inst     = Output(UInt(32.W))
    val aluOut   = Output(UInt(32.W))
    val regWrite = Output(Bool())
  })

  val core       = Module(new RiscvFetchExecute(xlen = 32, initPC = initPC))
  val byteMemory = Module(new RiscvDataMemory(depthWords = memWords))

  val progSize   = 1 << log2Ceil(math.max(2, program.length))
  val paddedProg = program ++ Seq.fill(progSize - program.length)(BigInt(0x00000013))
  val progVec    = VecInit(paddedProg.map(_.U(32.W)))
  val pcWordAddr = core.io.imem.addr(log2Ceil(progSize) + 1, 2)
  core.io.imem.inst := progVec(pcWordAddr)

  byteMemory.io.addr          := core.io.dmem.addr
  byteMemory.io.funct3        := core.io.dmem.funct3
  byteMemory.io.memRead       := core.io.dmem.memRead
  byteMemory.io.memWrite      := core.io.dmem.memWrite
  byteMemory.io.writeData     := core.io.dmem.writeData
  core.io.dmem.readData       := byteMemory.io.readData

  io.pc       := core.io.pc
  io.inst     := core.io.inst
  io.aluOut   := core.io.aluOut
  io.regWrite := core.io.regWrite
}
