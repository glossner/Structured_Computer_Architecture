# 09/27/2026 08:20 Terminology & Historical Attribution Alignment (Chapters 12 & 13)
* **Terminology Corrections (`Latex/03_12_von_neumann_4-OS.tex`)**:
  * Corrected "four-organ system" to "fourth-order system" (4-OS) and "Memory Organ" to "Memory" in Section 12.4.
  * Replaced "functional organ" with "functional component" and "arithmetic organ" with "central processing unit (3-OS)".
  * Audited the entire repository and confirmed zero remaining instances of "organ" across all `.tex` files.
* **Historical Attribution Alignment with Section 13.4.2**:
  * Harmonized the stored-program writeup in Section 12.4 with co-author Stefan Gheorghe's attribution in Section 13.4.2 (`Latex/03_13_harvard_5-OS.tex`).
  * Explicitly cross-referenced Section 13.4.2 to acknowledge the collaborative genesis of the EDVAC stored-program design by J. Presper Eckert, John Mauchly, Arthur Burks, Herman Goldstine, and John von Neumann at the Moore School of Electrical Engineering, grounded in Alan Turing's mathematical model.

# 09/27/2026 08:00 Instruction Compounding (SCISM) & Modern Superscalar Dispatch Frontiers (Chapter 16)
* **Instruction Compounding & SCISM (Section 16.5.6 - `Latex/04_16_Performance_Optimized_Organization.tex`)**:
  * Documented the quadratic $O(W^2)$ dependency checking bottleneck in wide superscalar issue logic and credited Stamatis Vassiliadis, Bart Blaner, and Richard J. Eickemeyer for inventing the Scalable Compound Instruction Set Machine (SCISM, IBM 1994) and Compound ALU (CALU, 1993).
  * Analyzed how SCISM pre-groups compatible instructions into compound units without runtime interlocks and folds branches out of the instruction queue, achieving $>90\%$ of dual-issue performance with scalar clock simplicity.
* **Modern Frontiers in Superscalar Dispatch & Execution (Section 16.5.7)**:
  * *Macro-Op and Micro-Op Fusion*: Detailed the commercial evolution of compounding across Intel Core (Gochman et al., 2006), AMD Zen, Apple Silicon, ARM, and RISC-V (`lui`+`addi`, `auipc`+`jalr`, and indexed load/stores), merging adjacent instructions in decode to save ROB entries and issue bandwidth.
  * *Zero-Cycle Move Elimination*: Analyzed register renaming optimizations (Jourdan et al., 1999) that handle register-to-register moves entirely within the Register Alias Table (RAT) without allocating ALU execution cycles or reservation stations (0 issue slots, 0 ALU latency).
  * *Unified Physical Register File (PRF) Architecture vs. Data-Carrying ROB*: Contrasted classic P6 data-carrying ROBs (limited to 32--40 entries by data wiring and multiplexer overhead) with decoupled Unified PRF architectures (MIPS R10000, DEC Alpha 21264, Intel Sandy Bridge/Golden Cove, AMD Zen, Apple M-Series) where the ROB holds only tags and status while data lives in a 128--640+ entry centralized PRF.
  * *Speculative Memory Disambiguation & Store Sets*: Covered Load/Store Queues (LSQ), store-to-load forwarding, and speculative memory dependence prediction (Moshovos et al., 1997; Chrysos & Emer, 1998) with order violation replay recovery.
* **Evolution of Dynamic Execution Comparison (Table 16.7)**:
  * Expanded the comparative summary table to contrast Scoreboard (1964), Tomasulo (1967), Tomasulo + Circular ROB (1988), and Modern PRF + Fused OoO across 12 microarchitectural dimensions.

# 09/27/2026 07:45 Storage Systems Restructuring & Memory Hierarchy Optimization (Chapters 12 & 16)
* **Storage Systems Restructuring (Chapter 12 - `Latex/03_12_von_neumann_4-OS.tex`)**:
  * Refactored Section 12.4 from "Memory Management" to "Storage Systems: Primary and Secondary Memory". Framed memory within the fundamental 4-OS abstract model (a single, uniform address space for instructions and data), highlighting that early implementations (EDVAC, Princeton IAS) functioned without caches, TLBs, or virtual memory paging.
  * Preserved the conceptual memory hierarchy pyramid (Figure 12.6, `\label{memhier}`) and covered Primary Memory (SRAM vs DRAM, 1T-1C capacitive cells, dynamic refresh cycles, destructive read and precharge) and Secondary/Auxiliary Storage (HDDs with platters/seek/rotational mechanics, SSDs with NAND flash/FTL/wear-leveling/NVMe, Optical Discs, and Magnetic Tape).
  * Emphasized that caching and virtual memory are performance optimizations rather than fundamental computing structures, deferring their microarchitectural analysis to Section 16.7.
  * Streamlined Section 12.5 to focus on Input/Output (I/O) Architecture (Buses, DMA, and Peripheral/Functional I/O Devices) and retained the DMA SystemVerilog problem as the primary problem in Chapter 12.
* **Memory Hierarchy and Cache Optimization (Chapter 16 - `Latex/04_16_Performance_Optimized_Organization.tex`)**:
  * Added Section 16.7 ("Memory Hierarchy and Cache Optimization"):
    * *Section 16.7.1 (The Processor-Memory Performance Gap and the Memory Wall)*: Integrated an updated, vectorized TikZ performance gap chart (Figure 16.7) spanning 1980 through 2024+, citing Hennessy & Patterson, Wm. A. Wulf and Sally A. McKee (1995), Patterson et al. (1997), Karl Rupp (2020), and Amir Gholami et al. on the AI Memory Wall (2021/2024). Traced single-thread CPU performance ($52\%$/year to 2004, slowing to $3.5\%$/year post-Dennard, $\sim 3{,}500\times$), DRAM latency improvement ($7\%$/year, flattening at $40\text{--}50\,\text{ns}$ for DDR4/DDR5, $>600\times$ gap), DRAM bandwidth, and aggregate accelerator throughput ($>100{,}000\times$).
    * *Section 16.7.2 (The Locality Principle)*: Analyzed temporal and spatial locality in software execution (`\label{locprinc}`).
    * *Section 16.7.3 (Cache Memory Microarchitecture)*: Detailed cache address decomposition into Tag, Index, and Block Offset fields; placement policies (direct-mapped, set-associative, fully associative); replacement policies (LRU, Pseudo-LRU, Random); write policies (write-through with write buffers vs write-back with dirty bits; write-allocate vs no-write-allocate); Mark D. Hill's 3 Cs model of cache misses (Compulsory, Capacity, Conflict, and multiprocessor Coherence misses, 1987); and multi-level Average Memory Access Time (AMAT) formulations.
    * *Section 16.7.4 (Virtual Memory and Associative CAM-Based TLBs)*: Covered virtual page translation, page tables, and demand paging (`\label{lb:vmm}`, Figure 16.14). Contrasted flat RAM-based page translation ($O(n \times (\log m + \log n))$ area, $(m/n)\%$ utilization) with associative Content-Addressable Memory (CAM) Translation Lookaside Buffers ($O(m \times (\log m + \log n))$ area), proving an area reduction factor in $O(n/m)$. Provided a structural comparison table between hardware caches and virtual memory paging.
    * *Section 16.8 (Analytical Practice Problems)*: Relocated the CAM-based page translator design problem from Chapter 12 to Section 16.8 (Problem 16.8) with a complete mathematical solution demonstrating a $352\times$ memory savings, and added Problem 16.9 analyzing multi-level cache AMAT, tag/index/offset bit breakdowns, and pipeline CPI degradation under memory stalls.
* **Bibliographic Citations**:
  * Standardized all citations to use `\cite{...}.` parenthetical citations matching repository conventions.

# 09/27/2026 07:30 Bibliographic Citation Standardization (Chapters 15, 16, & 18)
* **Citation Standardization (`Latex/04_15_...`, `Latex/04_16_...`, `Latex/04_18_...`)**:
  * Standardized citations across Chapters 15, 16, and 18 to appear as parenthetical references at the end of sentences using `\cite{...}` (leveraging the document's `\let\cite\citep` macro definition), rendering consistent `(Author, Year)` parenthetical citations throughout the text.
  * Replaced markdown bold tags with LaTeX `\textbf{}` formatting in Chapter 18.

# 09/27/2026 07:15 Performance Optimized Architecture (Chapter 18) & Hardware Multithreading

* **Performance Optimized Architecture (Chapter 18 - `Latex/04_18_Performance_Optimized_Architecture.tex`)**:
  * Authored a brand new, in-depth chapter exploring architectural specializations beyond the ILP wall, framed around Domain-Specific Architectures (DSAs) and modular RISC-V extensions.
  * *Section 18.1 (The Post-ILP Era & the Rise of DSAs)*: Documented the triad of physical scaling limits (Dennard breakdown/Power Wall, ILP Wall, Memory Wall), dark silicon, and the prohibitive 80--90% von Neumann control overhead of out-of-order superscalar cores. Framed DSAs via Amdahl's Law and the modular RISC-V extension library (`F`, `D`, `V`, `P`, `Zfh`, `RV-M`).
  * *Section 18.2 (HPC and Floating-Point Architecture)*: Examined IEEE 754 arithmetic compliance, alignment/normalization shifters, dynamic rounding modes, accrued exception flags, the RISC-V floating-point register file (`f0`--`f31`) with full writable `f0` and NaN-boxing, `fcsr` status register, FMA units, and pipelined floating-point datapaths.
  * *Section 18.3 (Vector Processing & Data-Level Parallelism)*: Contrasted packed SIMD with Cray-style scalable vectors, detailed the RISC-V Vector (RVV) extension architecture (`VLEN`, `ELEN`, `SEW`, `LMUL`), dynamic configuration via `vsetvli`, vector stripmining assembly loops, multi-lane execution datapaths, and memory access modes (unit-stride, strided, indexed gather/scatter).
  * *Section 18.4 (GPUs and Massively Parallel SIMT)*: Analyzed Single Instruction, Multiple Threads (SIMT) vs SIMD, warp/wavefront execution, zero-overhead multithreaded warp schedulers for latency hiding, branch divergence management via active masks and divergence stacks, and open-source RISC-V SIMT GPGPU architectures (Vortex).
  * *Section 18.5 (Digital Signal Processors (DSPs) & Embedded Compute)*: Explored FIR/FFT computational kernels, fixed-point $Q$-format arithmetic with saturation and guard bits, dual-memory Harvard architectures (X/Y memories), dedicated Address Generation Units (AGUs) for circular modulo and bit-reversed addressing, and the RISC-V packed-SIMD `P` extension.
  * *Section 18.6 (Tensor & Matrix Processors for AI)*: Analyzed GEMM computational kernels, arithmetic intensity and the roofline model, 2D systolic array architectures with weight-stationary dataflows (Google TPU), sub-word low-precision numerics (INT8, FP8, BF16), and the emerging RISC-V Matrix (`RV-M`) extension.
  * *Section 18.7 (Comparative Synthesis of DSAs)*: Cross-cutting comparative synthesis (Table 18.3) contrasting CPUs, Vectors, GPUs, DSPs, and TPUs across instruction bandwidth, control overhead, memory organization, and peak energy efficiency.
* **Hardware Multithreading: Exploiting Thread-Level Parallelism (Chapter 16 - `Latex/04_16_Performance_Optimized_Organization.tex`)**:
  * Added Section 16.6 covering multithreading to exploit thread-level parallelism (TLP) and hide memory/execution latency.
  * Illustrated horizontal and vertical issue slot waste in superscalar cores vs multithreaded slot utilization (Figure 16.12).
  * Provided detailed taxonomy of fine-grained/interleaved (barrel) multithreading (citing Burton Smith on the Denelcor HEP, 1978), coarse-grained (blocked) multithreading, and Simultaneous Multithreading (SMT) (citing Dean Tullsen, Susan Eggers, and Henry Levy, ISCA 1995).
  * Analyzed microarchitectural resource sharing trade-offs between statically partitioned and dynamically shared hardware structures.
  * Detailed architectural touchpoints: hardware thread IDs (`mhartid`), atomic memory operations (`LR`/`SC`, AMOs in RISC-V `A` extension), memory fences (`FENCE`, `FENCE.I`), and memory consistency models (RVWMO vs TSO).
* **Textbook Chapter Structure & Downstream Renumbering**:
  * Renamed `Latex/04_18_1_RISCV.tex` to `Latex/04_19_1_RISCV.tex` (Chapter 19: The RISC-V Processor).
  * Renamed `Latex/04_18_2_RISCV_pipelined.tex` to `Latex/04_19_2_RISCV_pipelined.tex`.
  * Updated `Latex/00_main.tex` master inclusion sequence, aligning Chapter 20 naturally with `04_20_pRISC_heterogeneous.tex`.


# 09/27/2026 06:40 Performance Optimized Organization (Chapter 16) Enhancements & Refinements
* **Chapter Renaming (`Latex/04_16_Performance_Optimized_Organization.tex`)**: Renamed `04_16_ILP.tex` to `04_16_Performance_Optimized_Organization.tex` and updated `Latex/00_main.tex`.
* **Pedagogical Framing & Performance Equations**: Connected Chapter 15 gate-level logic optimization to Chapter 16 microarchitectural organization; formalized processor iron law ($\text{Execution Time} = \text{IC} \times \text{CPI} \times T_{clock}$), pipeline stalls, and speedup formulas.
* **Interleaved/Barrel Multithreading Analysis**: Foreshadowed $N = 4$ round-robin interleaved barrel multithreading matching the $k = 4$ stage toyRISC pipeline, structurally guaranteeing zero hazards without stalls or forwarding multiplexers.
* **Structural Hazards & Hardware Replication (Section 16.3.1)**: Added rigorous treatment of resource contention and hardware duplication, contrasting dedicated PC incrementers (+4) and Harvard memory ports in basic pipelines with multiple ALUs, AGUs, and multi-ported register files in superscalar cores, removing redundant numerical prefixes from subsubsection titles.
* **Figure Floating & Whitespace Elimination**: Allowed Figures 16.1, 16.2, and 16.3 to float with `[htbp]`, moved Figure 16.2 outside the 4-stage description list, and repositioned Figure 16.3 adjacent to textual analysis to eliminate page-break gaps and excess whitespace.
* **Forwarding Logic Table & Priority Equations**: Added Table 16.3 and Boolean equations for `ForwardA` and `ForwardB` multiplexer selection, formalizing the imperative priority rule when back-to-back instructions target the same register.
* **Origins of Branch Prediction & Early Pioneers (Section 16.3.3)**: Authored dedicated subsection analyzing the emergence of branch prediction when pipelining and lookahead decoupled fetch from execution. Documented the first hardware implementation in the IBM 7030 Stretch supercomputer (Buchholz 1962), crediting Richard T. Blosk's Instruction Unit (1960) and Richard S. Ballance, John Cocke, and Harwood G. Kolsky's Look-Ahead Unit (1962) for static "predict untaken" and speculative rollback. Documented the independent invention of dynamic 2-bit branch prediction by Tom McWilliams and Curt Widdoes on the S-1 supercomputer at LLNL (1977/1979) and James E. Smith at CDC and UW-Madison (1981, 1983).
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
