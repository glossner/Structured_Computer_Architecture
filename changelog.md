# 09/27/2026 06:40 Performance Optimized Organization (Chapter 16) Enhancements & Refinements
* **Chapter Renaming (`Latex/04_16_Performance_Optimized_Organization.tex`)**: Renamed `04_16_ILP.tex` to `04_16_Performance_Optimized_Organization.tex` and updated `Latex/00_main.tex`.
* **Pedagogical Framing & Performance Equations**: Connected Chapter 15 gate-level logic optimization to Chapter 16 microarchitectural organization; formalized processor iron law ($\text{Execution Time} = \text{IC} \times \text{CPI} \times T_{clock}$), pipeline stalls, and speedup formulas.
* **Interleaved/Barrel Multithreading Analysis**: Foreshadowed $N = 4$ round-robin interleaved barrel multithreading matching the $k = 4$ stage toyRISC pipeline, structurally guaranteeing zero hazards without stalls or forwarding multiplexers.
* **Structural Hazards & Hardware Replication (Section 16.3.1)**: Added rigorous treatment of resource contention and hardware duplication, contrasting dedicated PC incrementers (+4) and Harvard memory ports in basic pipelines with multiple ALUs, AGUs, and multi-ported register files in superscalar cores, removing redundant numerical prefixes from subsubsection titles.
* **Figure Floating & Whitespace Elimination**: Allowed Figures 16.1, 16.2, and 16.3 to float with `[htbp]`, moved Figure 16.2 outside the 4-stage description list, and repositioned Figure 16.3 adjacent to textual analysis to eliminate page-break gaps and excess whitespace.
* **Forwarding Logic Table & Priority Equations**: Added Table 16.3 and Boolean equations for `ForwardA` and `ForwardB` multiplexer selection, formalizing the imperative priority rule when back-to-back instructions target the same register.
* **1-Bit vs. 2-Bit Branch Prediction Trace Table**: Corrected 2-bit counter description to a 4-state automaton and added step-by-step trace Table 16.5 comparing 1-bit vs 2-bit dynamic predictors on a 4-iteration loop, showing why 2-bit counters eliminate loop re-entry mispredictions ($(N-1)/N$ vs $(N-2)/N$).
* **Two-Level Adaptive Branch Prediction & Yale Patt Citations**: Authored dynamic branch prediction historical foundations citing J. E. Smith (1981) and Lee & A. J. Smith (1984). Added Subsection 16.3.4 crediting Tse-Yu Yeh and Yale N. Patt (1991, 1992) for two-level adaptive branch prediction, Yale Patt's HPS speculative architecture (1985), the taxonomy of Global/Local History Registers (GHR/BHR) and Pattern History Tables (PHT), Scott McFarling's gshare and tournament combining predictors (1993), and their integration into modern out-of-order superscalar processors.
* **Out-of-Order Execution, ROB & Inventor Citations**: Authored comprehensive treatment of dynamic scheduling with Seymour Cray and James E. Thornton's CDC 6600 centralized Scoreboard (4 stages, stalls on WAR/WAW), Robert M. Tomasulo's IBM 360/91 algorithm (3 stages, distributed reservation stations, dynamic register renaming, CDB broadcast), and James E. Smith and Andrew R. Pleszkun's Reorder Buffer (ROB) supporting precise interrupts and in-order retirement with speculative branch recovery. Added verified bibliography entries and expanded comparison Table 16.6 to cover all three landmark generations.
* **Analytical Practice Problems with Worked Solutions**: Added Problems 16.5 (Pipeline Speedup and Imbalance Analysis), 16.6 (Forwarding Unit Hazard Equations and Priority), and 16.7 (Branch Predictor Performance and CPI Penalty) complete with step-by-step worked mathematical solutions.


# 09/27/2026 05:40 RISC-V Processor Architecture, Verification & Chapter Structure Refinement

* **RISC-V Chapter 18 Structure (`Latex/04_18_1_RISCV.tex` & `Latex/04_18_2_RISCV_pipelined.tex`)**: Renamed Chapter 18 single-cycle document to `04_18_1_RISCV.tex` and created `04_18_2_RISCV_pipelined.tex` as a pipelined implementation placeholder, both included in `Latex/00_main.tex`.
* **Streamlined Chapter 13 (`03_13_harvard_5-OS.tex`)**: Removed duplicated Chisel core listings from Chapter 13 and replaced them with a direct cross-reference to Chapter 18.
* **Refined Conformance Results Table**: Streamlined Table 18.2 to two columns (`Category` and `Architectural Test Suites`) bounded to `\textwidth` via `tabularx` to cleanly eliminate table margin overflow.
* **Modular Pipeline Alignment**: Removed Section 18.5 (ZeroNyte comparative study) to defer structural hazards and comparative analysis to the upcoming pipelined design chapter.
* **Error-Free Textbook Compilation**: Verified full book compilation (599 pages) with XeLaTeX, MakeIndex, and 100% test pass rate across all Chisel suites.

# 09/26/2026 12:00 Modular RV32I_Zmmul Architecture & Datapath RTL Integration

* **Modular RISC-V Decoder (`RiscvDecoder.scala`)**: Implemented modular instruction decoder targeting RV32I_Zmmul, leveraging synthesizable Scala OOP abstractions (`RiscvFields`, `DecodedInstruction`, `DecodedControl`, `RiscvOpcodes`, and `AluOp`). Operates with zero gate-delay direct-wire opcode derivation for R-type instructions (`aluOp = {inst[25], inst[30], inst[14:12]}`). Verified via unit test suite `RiscvDecoderTest.scala`.
* **Multi-Format Immediate Generator (`RiscvImmGen.scala`)**: Implemented immediate generation module supporting concurrent sign-extension and slicing for I, S, B, U, and J instruction formats. Verified via unit test suite `RiscvImmGenTest.scala`.
* **RV32I_Zmmul Arithmetic Logic Unit (`RiscvALU.scala`)**: Implemented complete 32-bit execution unit handling all 10 standard RV32I integer operations and 4 Zmmul extension multiplication operations (`MUL`, `MULH`, `MULHSU`, `MULHU`), generating direct condition flags (`zero`, `lessThan`, `lessThanU`) using idiomatic `MuxCase`. Verified via unit test suite `RiscvALUTest.scala`.
* **Synchronous Byte-Addressable Data Memory (`RiscvDataMemory.scala`)**: Implemented byte-addressable synchronous RAM utilizing `SyncReadMem`, supporting sub-word store operations (`SB`, `SH`, `SW`) with byte mask vectors (without `switch` statements) and load operations (`LB`, `LH`, `LW`, `LBU`, `LHU`) with sign/zero extension. Verified via unit test suite `RiscvDataMemoryTest.scala`.
* **Program Counter Enhancement (`ProgramCounter.scala`)**: Added sequential link address port `pcPlus4` to the 32-bit RV32 program counter automaton, eliminating datapath adder duplication during jump-and-link (`JAL`/`JALR`) operations.
* **Modular RALU Refactoring (`RALU.scala`)**: Refactored the 2-OS execution engine to modularly instantiate `RiscvRegFile` and `RiscvALU` with direct-wire 5-bit opcode routing and named bundle wiring. Verified via unit test suite `RALUTest.scala`.
* **LaTeX Textbook & Code Listing Integration**: Integrated `RiscvDecoder.scala` and `RiscvALU.scala` in Chapter 8 (0-OS), `RiscvDataMemory.scala` in Chapter 9 (1-OS), updated `ProgramCounter.scala` and `RALU.scala` in Chapter 10 (2-OS), and added listings to Appendix B (`A99_Code_Listings.tex`). All listings strictly formatted to <= 42 lines to guarantee unbroken, single-page presentation.

# 09/26/2026 10:55 Chisel Listing Layout Refinements and RALU MuxCase Migration

* **RALU MuxCase Refactor (`RALU.scala`)**: Replaced `switch(io.aluOp)` with idiomatic `MuxCase` construct in `RALU.scala` adhering to architectural guidelines against decoder/ALU switch statements, verified with `RALUTest.scala`.
* **Listing Contiguity Enforcement (Chapters 9 & 10)**: Eliminated all multi-page listing splits across Chapter 9 and 10 Chisel modules (`SRAM.scala`, `PipelinedSRAM.scala`, `RegFile2R1WVec.scala`, `RegFile2R1WSRAM.scala`, `RiscvRegFile.scala`, `LanguageRecognizer.scala`, `GCD.scala`, `ProgramCounter.scala`, and `RALU.scala`), ensuring every listing is completely unbroken and contiguous on a single page.
* **Pipelined SRAM Listing Correction (`02_09_memory_circuits_1-OS.tex`)**: Corrected Listing 9.6 to include `PipelinedSRAM.scala` (utilizing `RegNext`) rather than replicating `SRAM.scala`.
* **ProgramCounter Truncation Fix (`02_10_automata_2-OS.tex`)**: Removed line slicing so the listing presents complete code from line 1 including package, imports, and `Mode` enum.
* **LanguageRecognizer Syntax Highlighting Fix (`LanguageRecognizer.scala`)**: Cleaned comment formatting and quotation marks that triggered LaTeX Scala parser coloring anomalies, ensuring crisp syntax highlighting.

# 09/26/2026 10:10 RISC-V Hardware Bridge & Automata Integration (Phase 1)

* **RISC-V Register File (`RiscvRegFile.scala`)**: Implemented 32-entry × 32-bit RV32 register file enforcing hardwired `x0 === 0.U` on both read and write, with dual combinational read ports and synchronous write port. Added unit test suite `RiscvRegFileTest.scala` verifying zero-invariance, dual-read isolation, and boundary addressing.
* **Mealy Language Recognizer (`LanguageRecognizer.scala`)**: Implemented type-safe FSM recognizing regular language $a^+ b^+$ using Chisel 7 `ChiselEnum` and `switch/is` pattern matching, with Mealy output logic and default-wire latch prevention. Added unit test suite `LanguageRecognizerTest.scala`.
* **RISC-V Program Counter (`ProgramCounter.scala`)**: Implemented 32-bit RISC-V program counter automaton supporting sequential +4 execution, PC-relative branches/jumps, register-indirect JALR with explicit LSB masking to zero, and pipeline stalls. Added unit test suite `ProgramCounterTest.scala`.
* **Registers with Arithmetic & Logic Unit (`RALU.scala`)**: Implemented 2-OS execution engine loop-connecting `RiscvRegFile`, 32-bit 10-operation ALU, immediate selection mux, and write-back mux. Added unit test suite `RALUTest.scala`.
* **LaTeX Dual-Language Integration (Chapters 9 & 10)**: Wrapped SystemVerilog and Chisel implementations in conditional macros (`chiselversion` / `verilogversion`) across `02_09_memory_circuits_1-OS.tex` and `02_10_automata_2-OS.tex`, integrated `GCD.scala` complex automaton, updated `A99_Code_Listings.tex`, fixed environment scoping to ensure clean single-language and dual-language compilation, and validated 100% test pass rate across all 24 suites.

# 09/26/2026 08:35 Comprehensive Categorized Subject Index and Glossary Generation

* **Categorized Subject Index & Glossary**: Expanded the textbook index into a comprehensive, 490-entry Subject Index and Glossary spanning all 18 chapters and 4 appendices, with structured hierarchical sub-entries, concise architectural definitions (`term: definition`), cross-references (`|see{...}`), and biographical coverage of historical pioneers.
* **MakeIndex Custom Styling**: Created `Latex/index_style.ist` configuring two-column alphabetical section groupings with large bold letter headers (`Symbols`, `A`--`Z`), horizontal leader spacing, and non-breaking headers.
* **Cross-Platform Font Configuration**: Enhanced `Latex/00_main.tex` with font existence checks (`\IfFontExistsTF{Times New Roman}{...}{\setmainfont{Liberation Serif}}`) ensuring robust XeLaTeX compilation across Linux and Windows/macOS environments.

# 09/26/2026 08:08 Comprehensive Improvement of Chapter 4.15 (Performance Optimized Digital Logic)

* **Code Formatting & Listing Standardization**: Replaced fragile math-mode code snippets with standard `chiselcode` environments for `MuxCase` and `when`, fixed raw markdown formatting, and corrected punctuation in multi-port register file descriptions.
* **High-Performance Adder Consolidation**: Unified disparate adder sections into `High-Performance Adders`, providing mathematical formulations for Generate/Propagate (CLA), speculative square-root sizing (Carry Select), 3:2 compressor logic (Carry Save), and the associative prefix operator (Kogge-Stone), along with a summary comparison table.
* **Register File Implementation Integration**: Added coverage and code listings for the combinational flip-flop register file (`RegFile2R1WVec.scala`) and multi-banked SRAM register file (`RegFile.scala`), completing the design spectrum in the text.
* **Sequential Timing Closure Analysis**: Reframed the automata section around timing closure, contrasting Moore and Mealy architectures in terms of setup/hold timing paths, glitch propagation, and maximum operating frequency ($f_{\max}$).
* **Indexing & Cumulative Changelog**: Added comprehensive index terms across Chapter 4.15 and updated `Latex/changelog.tex` with a concise summary.

# 09/26/2026 07:48 Update LaTeX Source for Chisel 7.15 and EphemeralSimulator with Cumulative Changelog

* **Combinational Circuits Chapter**: Updated Chisel version reference regarding `switch` function deprecation to Chisel 7.15 (and Chisel 6.6) in `Latex/02_08_combinational_circuits_0-OS.tex`.
* **Front Matter & Preface**: Added explicit Scala 2.13.18 version mention alongside Chisel 7.15.0 in `Latex/00_main.tex`, and integrated `changelog.tex` into the front matter.
* **Installation Guide Corrections**: Updated `build.sbt` location to `RTL/Chisel/`, corrected the code listing caption to `Chisel Build Configuration (build.sbt)`, and refreshed the `sbt test` terminal transcript to reflect the 20-test suite execution under Chisel 7.15.0 and sbt 1.11.7 in `Latex/A01_1a_Install.tex`.
* **Testing Documentation Modernization**: Modernized Appendix A testing documentation (`Latex/A01_1_Chisel.tex` and `Latex/A01_3_Chisel_vs_System_Verilog.tex`) from legacy `ChiselTest` to Chisel's built-in `EphemeralSimulator` (`chisel3.simulator.EphemeralSimulator._`), matching the repository's testbenches.
* **Cumulative LaTeX Changelog**: Added `Latex/changelog.tex` tracking all updates since the First Edition PDF (`Structured_Computer_Architecture_1st_edition_2026_06_03.pdf`) with a single bullet point per topic.

# 09/26/2026 07:15 Upgrade Chisel to 7.15.0 and Scala to 2.13.18 with Test Suite Adaptations

* **Chisel & Scala Version Upgrade**: Upgraded `chiselVersion` to `7.15.0` and `scalaVersion` to `2.13.18` in `RTL/Chisel/build.sbt` and updated the `chisel-plugin` compiler plugin cross-version reference to align with Maven Central releases and CIRCT backend.
* **Simulation Testbench Adaptations**: Updated `RTL/Chisel/src/test/scala/scabook/waveFormGeneratorSpec.scala` with an explicit initial reset pulse so registers enter their reset initial states under Chisel 7 default randomized simulation. Updated `RTL/Chisel/src/test/scala/scabook/memory/RegFileMT2R1WSRAMTest.scala` to preload thread 1 register 1 to 0 during the preload stage, ensuring thread isolation is verified deterministically under SRAM randomization.
* **SystemVerilog Artifact Generation**: Re-emitted `RTL/Chisel/GCD.sv` and `RTL/Chisel/generated_verilog_annotated/WaveFormGenerator.sv` with CIRCT firtool 1.158.0 bundled with Chisel 7.15.0.
* **Documentation & Build Configuration**: Updated Chisel version references to 7.15.0 in `README.md` and `Latex/00_main.tex`. Added `filelist.f` and `verification/` to `.gitignore` to prevent CIRCT verification layer artifacts from cluttering the working tree.
* **Verification & Validation**: Verified with `sbt clean test` with 100% pass rate across all 20 test suites (20 tests passed, 0 failures), verified hardware generation execution via `sbt runMain`, and validated Verilator C++ driver compilation.
