# Changelog

All notable changes to the Structured Computer Architecture project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased] - 2026-09-26

### Fixed
- **Chisel Listing Splitting & Layout (Chapters 9 & 10)**:
  - Eliminated listing splitting across page turns for all Chisel listings in Chapters 9 and 10 (`SRAM.scala`, `PipelinedSRAM.scala`, `RegFile2R1WVec.scala`, `RegFile2R1WSRAM.scala`, `RiscvRegFile.scala`, `LanguageRecognizer.scala`, `GCD.scala`, `ProgramCounter.scala`, and `RALU.scala`), ensuring unbroken, contiguous single-page presentation.
  - Corrected Listing 9.6 to properly include `PipelinedSRAM.scala` (with `RegNext`) instead of replicating `SRAM.scala`.
  - Fixed `ProgramCounter.scala` truncation in `02_10_automata_2-OS.tex`, restoring full display from line 1.
  - Fixed all-red syntax highlighting on `LanguageRecognizer.scala` caused by quote parsing in LaTeX listings.
  - Refactored `RALU.scala` ALU multiplexing from `switch` to `MuxCase`, conforming with textbook architectural guidelines.

### Added
- **RISC-V Processor Architecture, Implementation & Verification (Chapter 18)**:
  - Authored and integrated Chapter 18 (`04_18_1_RISCV.tex`) into Part IV ("Practical Designs") of the textbook, alongside empty placeholder `04_18_2_RISCV_pipelined.tex` in `Latex/00_main.tex`.
  - Detailed the RISC-V RV32I base integer ISA with the `Zmmul` hardware multiplication extension, the Harvard 5-OS hardware composition and 5-function Fetch-Execute microarchitecture, and pure Chisel/Scala verification methodology using `EphemeralSimulator`.
  - Documented 100% pass verification results across all 42 official RISC-V architectural test suites (`riscv-arch-test`) with bit-exact signature matches against Spike golden reference models and updated test results table formatting to fit margins cleanly.
  - Streamlined Chapter 13 (`03_13_harvard_5-OS.tex`) by replacing redundant inline Chisel core listings with a clean cross-reference to Chapter 18.
- **Modular RV32I_Zmmul Architecture & Datapath Elements (Chapters 8, 9, & 10)**:
  - Implemented `RiscvConstants.scala`: Defined standard RV32I opcodes (`RiscvOpcodes`), direct-wire 5-bit ALU control codes (`AluOp`), object-oriented instruction view (`RiscvFields`), and typed interconnect bundles (`DecodedControl`, `DecodedInstruction`).
  - Implemented `RiscvDecoder.scala`: Synthesizable RV32I_Zmmul instruction decoder with zero gate-delay direct-wire opcode routing (`aluOp = {inst[25], inst[30], inst[14:12]}`) for R-type instructions and full control signal generation. Verified with `RiscvDecoderTest.scala`.
  - Implemented `RiscvImmGen.scala`: Dedicated sign-extending immediate generator for I, S, B, U, and J instruction formats. Verified with `RiscvImmGenTest.scala`.
  - Implemented `RiscvALU.scala`: 32-bit RV32I_Zmmul ALU implementing all 10 base integer operations and 4 multiplication operations with comparison flags (`zero`, `lessThan`, `lessThanU`) via `MuxCase`. Verified with `RiscvALUTest.scala`.
  - Implemented `RiscvDataMemory.scala`: Byte-addressable synchronous data RAM utilizing `SyncReadMem` with byte write enables (`SB`, `SH`, `SW`) without `switch` statements, and load sign/zero extension (`LB`, `LH`, `LW`, `LBU`, `LHU`). Verified with `RiscvDataMemoryTest.scala`.
  - Enhanced `ProgramCounter.scala`: Added sequential link output port `pcPlus4` for JAL/JALR return address capture without datapath adder duplication.
  - Refactored `RALU.scala`: Modularly integrated `RiscvRegFile` and `RiscvALU` with direct-wire control and named bundle wiring. Verified with `RALUTest.scala`.
  - Updated LaTeX chapters `02_08_combinational_circuits_0-OS.tex`, `02_09_memory_circuits_1-OS.tex`, `02_10_automata_2-OS.tex`, and `A99_Code_Listings.tex` with single-page unbroken listings.
- **Phase 1 RISC-V Hardware Bridge & Automata (Chapters 9 & 10)**:
  - Implemented `RiscvRegFile.scala` (32-register × 32-bit RV32 register file with hardwired `x0 === 0.U`) and unit test suite `RiscvRegFileTest.scala`.
  - Implemented `LanguageRecognizer.scala` (type-safe Mealy FSM for regular language $a^+ b^+$ with `ChiselEnum`) and unit test suite `LanguageRecognizerTest.scala`.
  - Implemented `ProgramCounter.scala` (RV32 instruction sequencer supporting Plus4, Branch, JALR with LSB zeroing, and Stall) and unit test suite `ProgramCounterTest.scala`.
  - Implemented `RALU.scala` (2-OS execution engine loop coupling register file, ALU, immediate mux, and write-back) and unit test suite `RALUTest.scala`.
  - Updated `Latex/02_09_memory_circuits_1-OS.tex` and `Latex/02_10_automata_2-OS.tex` with dual-language switches (`verilogversion` and `chiselversion`), ensuring clean single-language and dual-target PDF generation.
  - Added new memory and automata code listings to `Latex/A99_Code_Listings.tex`.
- **Comprehensive Subject Index & Glossary**:
  - Expanded index into a 490-entry Subject Index and Glossary covering mathematical foundations, OS abstraction levels (0-OS through N-OS), microarchitectures, and hardware description languages.
  - Implemented hierarchical sub-entries (`!`), architectural definition glosses (`term: definition`), cross-references (`|see`), and historical biographical entries.
  - Added custom MakeIndex style file `Latex/index_style.ist` supporting alphabetical division headers and compact column alignment.
  - Added robust cross-platform font detection in `Latex/00_main.tex` falling back to Liberation Serif when Times New Roman is not installed.
- **Performance Optimized Digital Logic (Chapter 15)**:
  - Formulated analytical Generate/Propagate equations, unrolled CLA carry recurrences, 3:2 compressor logic, and associative prefix operator logic.
  - Added architectural comparison table for high-performance adders (RCA, CLA, CSA, Kogge-Stone, Brent-Kung).
  - Integrated Chisel code listings for combinational register files (`RegFile2R1WVec.scala`) and multi-banked register files (`RegFile.scala`).
  - Added detailed sequential timing closure analysis contrasting Mealy and Moore machine critical path constraints and glitch sensitivity.
  - Added index markers across all major topics in Chapter 4.15.
- **LaTeX Changelog**:
  - Added `Latex/changelog.tex` documenting changes since the First Edition PDF release by topic.
  - Included `changelog.tex` in `Latex/00_main.tex` front matter.

### Changed
- **Chapter 15 Code Formatting & Structure**:
  - Standardized code snippets to `chiselcode` environments, eliminating math-mode code blocks.
  - Unified fragmented adder sections into a cohesive `High-Performance Adders` section.
  - Fixed raw markdown artifacts and punctuation errors in register file descriptions.
- **LaTeX Chisel & Testing Modernization**:
  - Updated Chisel version reference regarding `switch` deprecation in `Latex/02_08_combinational_circuits_0-OS.tex`.
  - Added Scala 2.13.18 version specification alongside Chisel 7.15.0 in `Latex/00_main.tex`.
  - Updated testing framework documentation from legacy `ChiselTest` to `EphemeralSimulator` in `Latex/A01_1_Chisel.tex` and `Latex/A01_3_Chisel_vs_System_Verilog.tex`.
  - Corrected `RTL/Chisel/` path, `build.sbt` listing caption, and updated `sbt test` terminal transcript in `Latex/A01_1a_Install.tex`.
- **Chisel & Scala Version Upgrade**:
  - Upgraded `chiselVersion` to `7.15.0` and `scalaVersion` to `2.13.18` in `RTL/Chisel/build.sbt`.
  - Updated `chisel-plugin` compiler plugin cross-version resolution for Scala 2.13.18.
- **Documentation**:
  - Updated Chisel version references to 7.15.0 in `README.md` and `Latex/00_main.tex`.

### Fixed
- **Simulation Testbench Compatibility**:
  - Updated `RTL/Chisel/src/test/scala/scabook/waveFormGeneratorSpec.scala` with an initial reset pulse to ensure registers start in their reset state under Chisel 7's randomized simulation.
  - Updated `RTL/Chisel/src/test/scala/scabook/memory/RegFileMT2R1WSRAMTest.scala` to preload thread 1 register 1 to 0 during preload, ensuring multi-threaded SRAM register isolation verification passes reliably.
- **Build & Artifact Cleanup**:
  - Added `filelist.f` and `verification/` to `.gitignore` for CIRCT firtool 1.158.0 verification layer emissions.
  - Re-emitted `RTL/Chisel/GCD.sv` and `RTL/Chisel/generated_verilog_annotated/WaveFormGenerator.sv` with the updated firtool backend.
