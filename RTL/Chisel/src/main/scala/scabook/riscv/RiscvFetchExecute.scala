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

/** RISC-V RV32I_Zmmul 5-OS Harvard Fetch-Execute Pipelined Core.
  * Features a 2-stage decoupled pipeline (Fetch | Execute) with
  * hardware interlocks guaranteeing zero RAW, WAW, and WAR hazards.
  */
class RiscvFetchExecute(val xlen: Int = 32, val initPC: BigInt = 0) extends Module {
  val io = IO(new Bundle {
    val imem     = new ImemPort(xlen, xlen)
    val dmem     = new DmemPort(xlen, xlen)
    val stall    = Input(Bool())
    val pc       = Output(UInt(xlen.W))
    val inst     = Output(UInt(xlen.W))
    val aluOut   = Output(UInt(xlen.W))
    val regWrite = Output(Bool())
  })

  val pc      = Module(new ProgramCounter(width = xlen, initPC = initPC))
  val decoder = Module(new RiscvDecoder)
  val ralu    = Module(new RALU(width = xlen))

  // ==========================================
  // STAGE 1: INSTRUCTION FETCH (IF)
  // ==========================================
  io.imem.addr := pc.io.pc

  // Pipeline Registers between Fetch and Execute (IF/EX)
  val regInst   = RegInit(0x00000013.U(32.W)) // NOP: ADDI x0, x0, 0
  val regPC     = RegInit(initPC.U(xlen.W))

  // Load-Use memory latency interlock (prevents RAW hazards on synchronous loads)
  val loadStall = RegInit(false.B)

  // ==========================================
  // STAGE 2: EXECUTE & WRITEBACK (EX/WB)
  // ==========================================
  val inst = regInst
  decoder.io.inst := inst
  val d = decoder.io.decoded
  val c = d.control
  val f = RiscvFields(inst)

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

  // Control transfer flush: discard fetched instruction on taken branch/jump
  val flush = branchTaken || isJal || isJalr

  // Target computation: Branch & JAL use regPC + imm; JALR uses rs1 + imm (ALU result)
  val target = Mux(isJalr, ralu.io.aluResult, regPC + d.imm)
  pc.io.branchImm  := d.imm
  pc.io.jalrTarget := target

  // Program Counter Next-Address Control (Loop 4)
  pc.io.mode := MuxCase(ProgramCounter.Mode.Plus4, Seq(
    flush                                   -> ProgramCounter.Mode.Jalr,
    (io.stall || (c.memRead && !loadStall)) -> ProgramCounter.Mode.Stall
  ))

  // Update IF/EX Pipeline Registers
  when(io.stall) {
    // Hold state during external stall
  }.elsewhen(c.memRead && !loadStall) {
    // 1-cycle load latency interlock: hold regInst and latch loadStall
    loadStall := true.B
  }.elsewhen(flush) {
    // Discard speculatively fetched instruction; insert NOP bubble
    regInst   := 0x00000013.U
    regPC     := pc.io.nextPC
    loadStall := false.B
  }.otherwise {
    regInst   := io.imem.inst
    regPC     := pc.io.pc
    loadStall := false.B
  }

  // Write-Back Multiplexer
  val wbData = MuxCase(ralu.io.aluResult, Seq(
    c.memToReg -> io.dmem.readData,
    c.jump     -> (regPC + 4.U),
    isLui      -> d.imm,
    isAuipc    -> (regPC + d.imm)
  ))

  // Executive Datapath (RALU - Loop 2 & Loop 3)
  ralu.io.rs1Addr  := d.rs1
  ralu.io.rs2Addr  := d.rs2
  ralu.io.rdAddr   := d.rd
  ralu.io.regWrite := c.regWrite && !io.stall && !(c.memRead && !loadStall)
  ralu.io.useImm   := c.aluSrc
  ralu.io.immVal   := d.imm
  ralu.io.aluOp    := c.aluOp
  ralu.io.memToReg := true.B
  ralu.io.extData  := wbData

  // Data Memory Port (Loop 5)
  io.dmem.addr      := ralu.io.aluResult
  io.dmem.funct3    := f.funct3
  io.dmem.memRead   := c.memRead && !io.stall
  io.dmem.memWrite  := c.memWrite && !io.stall && !loadStall
  io.dmem.writeData := ralu.io.rs2Data

  // Observability
  io.pc       := regPC
  io.inst     := inst
  io.aluOut   := ralu.io.aluResult
  io.regWrite := ralu.io.regWrite
}

/** Complete Harvard 5-OS Computing System Harness. */
class RiscvSystem(val program: Seq[BigInt], val memWords: Int = 1024) extends Module {
  val io = IO(new Bundle {
    val pc       = Output(UInt(32.W))
    val inst     = Output(UInt(32.W))
    val aluOut   = Output(UInt(32.W))
    val regWrite = Output(Bool())
  })

  val core = Module(new RiscvFetchExecute(xlen = 32))
  val dmem = Module(new RiscvDataMemory(depthWords = memWords))

  val progSize   = 1 << log2Ceil(math.max(2, program.length))
  val paddedProg = program ++ Seq.fill(progSize - program.length)(BigInt(0x00000013))
  val progVec    = VecInit(paddedProg.map(_.U(32.W)))
  val pcWordAddr = core.io.imem.addr(log2Ceil(progSize) + 1, 2)
  core.io.imem.inst := progVec(pcWordAddr)

  dmem.io.addr          := core.io.dmem.addr
  dmem.io.funct3        := core.io.dmem.funct3
  dmem.io.memRead       := core.io.dmem.memRead
  dmem.io.memWrite      := core.io.dmem.memWrite
  dmem.io.writeData     := core.io.dmem.writeData
  core.io.dmem.readData := dmem.io.readData

  core.io.stall := false.B
  io.pc         := core.io.pc
  io.inst       := core.io.inst
  io.aluOut     := core.io.aluOut
  io.regWrite   := core.io.regWrite
}
