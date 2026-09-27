# Changelog

All notable changes to the Structured Computer Architecture project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased] - 2026-09-27

### Changed
- **Chapter Sequence Alignment: Performance Optimized Architecture (Chapter 17) & ToyRISC Design (Chapter 18)**:
  - Reordered Performance Optimized Architecture to Chapter 17 (`Latex/04_17_Performance_Optimized_Architecture.tex`), positioning the complete three-tier optimization sequence (Digital Logic in Chapter 15 $\rightarrow$ Organization in Chapter 16 $\rightarrow$ Architecture in Chapter 17) directly ahead of practical processor design implementations.
  - Renamed and reordered the ToyRISC modular processor files to Chapter 18 (`Latex/04_18_0_ToyRISC.tex`, `Latex/04_18_1_ToyRISC_organization.tex`, `Latex/04_18_2_toyRISC_implementation.tex`, `Latex/04_18_3_ToyRISC_pipelined.tex`, `Latex/04_18_4_ToyRISC_forwarding.tex`, and `Latex/04_18_5_ToyRISC_verilog.tex`).
  - Verified that dynamic cross-references across Chapters 13, 16, 17, and 18 resolve cleanly to Chapter 17 (Performance Optimized Architecture) and Chapter 18 / Section 18.1 / Listing 18.1 / Table 18.1 (ToyRISC Design).
- **Front Matter: First Edition Preface Restoration and Second Edition Placeholder**:
  - Restored the original signed and dated First Edition preface (June 3rd, 2026) in both `Latex/00_main.tex` and `README.md`, preserving the historical toolchain note referencing Chisel 6.6.0 testing.
  - Added a placeholder for the Preface to the Second Edition in both `Latex/00_main.tex` and `README.md`, documenting the upgrade to Chisel 7.15.0 and Scala 2.13.18 (supported by CIRCT firtool 1.158.0 and `EphemeralSimulator`) along with explicit references to the BreadBoard Computer Zoo repository ([https://github.com/glossner/BBzoo](https://github.com/glossner/BBzoo)) and the KryptoNyte RISC-V processor family repository ([https://github.com/Ypologist/KryptoNyte](https://github.com/Ypologist/KryptoNyte)).
- **ToyRISC Architecture, Organization, and ISA Migration (Chapters 13 & 18)**:
  - Migrated non-redundant ToyRISC architecture, organization, loop-connected automata analysis, ISA specifications, opcode macro definitions (`A11_DEFINES.vh`), 28-instruction opcode Table 18.1, program execution trace Example 18.1, and single-cycle critical-path timing formulations from Section 13.5 into Chapter 18 (Section 18.1, `Latex/04_18_1_ToyRISC_organization.tex`).
  - Maintained Section 13.5 in `Latex/03_13_harvard_5-OS.tex` fully commented out in source, eliminating redundancy while preserving historical text.
  - Resolved all internal cross-references across Chapters 12, 13, 16, 17, and 18 (`sec:toyrisc_arch`, `tab:riscISA`, `lst:toyRISCdefines`, `exADD`, and `interrupt`) with zero undefined references.

### Fixed
- **Mathematical Range Typography Standardization (Chapters 12 & 16)**:
  - Replaced 22 non-standard instances across Chapters 12 and 16 where the division symbol (`\div`) was erroneously used for numeric ranges (e.g., $1\,\text{TB} \div 30\,\text{TB}$, ROB sizes, PRF sizes, L2/L3 capacities) with proper LaTeX en-dashes (`\text{--}`), while strictly preserving genuine arithmetic division operators in Chapter 1.
- **Section 12.4 Cleanup**:
  - Removed redundant introductory paragraph (`\paragraph{Microarchitectural Optimization vs. Fundamental Architecture}`).

### Added
- **Early Stored-Program Computing Landmark Citations (Section 12.4)**:
  - Added authoritative historical citations in Section 12.4 (`Latex/03_12_von_neumann_4-OS.tex`) for early physical 4-OS implementations: Maurice V. Wilkes and William Renwick's Cambridge EDSAC (`wilkes1949edsac`, the first operational stored-program computer in regular service, utilizing mercury delay lines), the Moore School EDVAC (`vonNeumann1945`, `gluck1953edvac`), and the Princeton IAS machine designed by Arthur W. Burks, Herman H. Goldstine, and John von Neumann (`burks1946preliminary`, utilizing Williams-Kilburn cathode-ray tubes).
  - Explicitly cross-referenced downstream microarchitectural optimizations: cache memory (Section 16.7.3) and virtual memory paging/TLBs (Section 16.7.4).
- **Cache Architecture Consolidation (Chapters 13 & 16)**:
  - Removed redundant Section 13.3 ("Cache Memories") from Chapter 13 (`Latex/03_13_harvard_5-OS.tex`), consolidating comprehensive cache microarchitecture into Section 16.7.3 while citing Maurice Wilkes' 1965 slave memories (`wilkes1965slave`) and the IBM System/360 Model 85 (`liptay1968cache`).
- **ToyRISC Modular Organization (Chapters 13 & 17)**:
  - Commented out Section 13.5 ("ToyRISC Processor") in Chapter 13 to eliminate duplication with Chapter 17 (ToyRISC Design), preserving all original text and figures in source comments.
  - Added an introductory reference paragraph under "How an Instruction Set Architecture is Designed" directing readers to Chapter 17 (`\label{toyRiscDesign}`) and Chapter 16 (`\label{lect5}`).

## [Unreleased] - 2026-09-26

### Fixed
- **Terminology and Historical Attribution Alignment (Chapters 12 & 13)**:
  - Corrected "four-organ system" to "fourth-order system" (4-OS) and "Memory Organ" to "Memory" in Section 12.4 (`Latex/03_12_von_neumann_4-OS.tex`).
  - Verified and eliminated all obsolete "organ" terminology across the entire textbook codebase.
  - Harmonized the Section 12.4 stored-program writeup with co-author Stefan Gheorghe's historical attribution in Section 13.4.2 (`Latex/03_13_harvard_5-OS.tex`), explicitly recognizing the collaborative EDVAC work of the Moore School team (J. Presper Eckert, John Mauchly, Arthur Burks, Herman Goldstine, and John von Neumann) based on Alan Turing's mathematical model.
- **Bibliographic Citation Standardization (Chapters 15, 16, & 18)**:
  - Standardized citations across Chapters 15, 16, and 18 to appear as parenthetical references at the end of sentences using `\cite{...}` (leveraging the document's `\let\cite\citep` macro definition), rendering consistent `(Author, Year)` parenthetical citations throughout the text.
- **Chisel Listing Splitting & Layout (Chapters 9 & 10)**:
  - Eliminated listing splitting across page turns for all Chisel listings in Chapters 9 and 10 (`SRAM.scala`, `PipelinedSRAM.scala`, `RegFile2R1WVec.scala`, `RegFile2R1WSRAM.scala`, `RiscvRegFile.scala`, `LanguageRecognizer.scala`, `GCD.scala`, `ProgramCounter.scala`, and `RALU.scala`), ensuring unbroken, contiguous single-page presentation.
  - Corrected Listing 9.6 to properly include `PipelinedSRAM.scala` (with `RegNext`) instead of replicating `SRAM.scala`.
  - Fixed `ProgramCounter.scala` truncation in `02_10_automata_2-OS.tex`, restoring full display from line 1.
  - Fixed all-red syntax highlighting on `LanguageRecognizer.scala` caused by quote parsing in LaTeX listings.
  - Refactored `RALU.scala` ALU multiplexing from `switch` to `MuxCase`, conforming with textbook architectural guidelines.

### Added
- **Instruction Compounding (SCISM) and Modern Superscalar Dispatch Frontiers (Chapter 16)**:
  - **SCISM (Section 16.5.6)**: Documented Stamatis Vassiliadis, Bart Blaner, and Richard J. Eickemeyer's Scalable Compound Instruction Set Machine (SCISM, IBM 1994) and Compound ALU (CALU, 1993), addressing the quadratic $O(W^2)$ comparator bottleneck of wide superscalar issue by pre-grouping independent instructions into compound instructions without runtime interlocks, and folding branches out of the instruction queue.
  - **Macro-Op and Micro-Op Fusion (Section 16.5.7)**: Detailed the commercial evolution of compounding across Intel Core (Gochman et al., 2006), AMD Zen, Apple Silicon, ARM, and RISC-V (`lui`+`addi`, `auipc`+`jalr`, and indexed load/stores), merging adjacent instructions in decode to save ROB entries and issue bandwidth.
  - **Zero-Cycle Move Elimination (Section 16.5.7)**: Analyzed register renaming optimizations (Jourdan et al., 1999) that handle register-to-register moves entirely within the Register Alias Table (RAT) without allocating ALU execution cycles or reservation stations (0 issue slots, 0 ALU latency).
  - **Unified Physical Register File (PRF) Architecture vs. Data-Carrying ROB (Section 16.5.7)**: Contrasted classic P6 data-carrying ROBs (limited to 32--40 entries by data wiring and multiplexer overhead) with decoupled Unified PRF architectures (MIPS R10000, DEC Alpha 21264, Intel Sandy Bridge/Golden Cove, AMD Zen, Apple M-Series) where the ROB holds only tags and status while data lives in a 128--640+ entry centralized PRF.
  - **Speculative Memory Disambiguation & Store Sets (Section 16.5.7)**: Covered Load/Store Queues (LSQ), store-to-load forwarding, and speculative memory dependence prediction (Moshovos et al., 1997; Chrysos & Emer, 1998) with order violation replay recovery.
  - **Evolution of Dynamic Execution Comparison (Table 16.7)**: Expanded the comparative summary table to contrast Scoreboard (1964), Tomasulo (1967), Tomasulo + Circular ROB (1988), and Modern PRF + Fused OoO across 12 microarchitectural dimensions.
- **Storage Systems Restructuring and Memory Hierarchy Optimization (Chapters 12 & 16)**:
  - **Chapter 12 (`Latex/03_12_von_neumann_4-OS.tex`)**: Refactored Section 12.4 from "Memory Management" to "Storage Systems: Primary and Secondary Memory". Framed memory in the context of von Neumann's 4-OS abstract model (a single, uniform address space for instructions and data), noting that early implementations (EDVAC, Princeton IAS) had no caches, TLBs, or paging. Retained the conceptual memory hierarchy pyramid (Figure 12.6, `\label{memhier}`) and covered Primary Memory (SRAM vs DRAM, 1T-1C capacitive storage, dynamic refreshing, destructive reads and precharge) and Secondary/Auxiliary Storage (HDDs with platters/seek/rotational mechanics, SSDs with NAND flash/FTL/wear-leveling/NVMe, Optical Discs, and Magnetic Tape). Emphasized that caching and virtual memory are performance optimizations, deferring their microarchitectural analysis to Section 16.7. Streamlined Section 12.5 to focus on Input/Output (I/O) Architecture (Buses, DMA, and Peripheral/Functional I/O Devices) and retained the DMA SystemVerilog problem as the primary problem in Chapter 12.
  - **Chapter 16 (`Latex/04_16_Performance_Optimized_Organization.tex`)**: Added Section 16.7 ("Memory Hierarchy and Cache Optimization"):
    - Section 16.7.1 (*The Processor-Memory Performance Gap and the Memory Wall*): Integrated an updated, vectorized TikZ performance gap chart (Figure 16.7) spanning 1980 through 2024+, citing Hennessy & Patterson, Wm. A. Wulf and Sally A. McKee, Patterson et al., Karl Rupp, and Amir Gholami et al. on the AI Memory Wall. Traced single-thread CPU performance ($52\%$/year to 2004, slowing to $3.5\%$/year post-Dennard, $\sim 3{,}500\times$), DRAM latency improvement ($7\%$/year, flattening at $40\text{--}50\,\text{ns}$ for DDR4/DDR5, $>600\times$ gap), DRAM bandwidth, and aggregate accelerator throughput ($>100{,}000\times$).
    - Section 16.7.2 (*The Locality Principle*): Analyzed temporal and spatial locality in software execution (`\label{locprinc}`).
    - Section 16.7.3 (*Cache Memory Microarchitecture*): Detailed cache address decomposition into Tag, Index, and Block Offset fields; placement policies (direct-mapped, set-associative, fully associative); replacement policies (LRU, Pseudo-LRU, Random); write policies (write-through with write buffers vs write-back with dirty bits; write-allocate vs no-write-allocate); Mark D. Hill's 3 Cs model of cache misses (Compulsory, Capacity, Conflict, and multiprocessor Coherence misses); and multi-level Average Memory Access Time (AMAT) formulations.
    - Section 16.7.4 (*Virtual Memory and Associative CAM-Based TLBs*): Covered virtual page translation, page tables, and demand paging (`\label{lb:vmm}`, Figure 16.14). Contrasted flat RAM-based page translation ($O(n \times (\log m + \log n))$ area, $(m/n)\%$ utilization) with associative Content-Addressable Memory (CAM) Translation Lookaside Buffers ($O(m \times (\log m + \log n))$ area), proving an area reduction factor in $O(n/m)$. Provided a structural comparison table between hardware caches and virtual memory paging.
    - Section 16.8 (*Analytical Practice Problems*): Relocated the CAM-based page translator design problem from Chapter 12 to Section 16.8 (Problem 16.8) with a complete mathematical solution demonstrating a $352\times$ memory savings, and added Problem 16.9 analyzing multi-level cache AMAT, tag/index/offset bit breakdowns, and pipeline CPI degradation under memory stalls.
- **Performance Optimized Architecture: Domain-Specific Architectures and Accelerators (Chapter 18)**:
  - Authored a comprehensive new chapter (`Latex/04_18_Performance_Optimized_Architecture.tex`) exploring architectural paradigm shifts beyond the ILP wall, structured around Domain-Specific Architectures (DSAs) and modular RISC-V extensions.
  - Section 18.1 (*The Post-ILP Era and the Rise of DSAs*): Analyzed physical limits (breakdown of Dennard scaling, Power Wall, ILP Wall, and Memory Wall), dark silicon, the 80--90% von Neumann control overhead of out-of-order superscalar cores, Amdahl's Law for accelerators, and the modular RISC-V extension framework (`F`, `D`, `V`, `P`, `Zfh`, and `RV-M`).
  - Section 18.2 (*High-Performance Computing and Floating-Point Architecture*): Examined IEEE 754 arithmetic compliance, dynamic rounding modes, accrued exception flags, the RISC-V floating-point register file (`f0`--`f31`) with full writable `f0` and NaN-boxing, `fcsr` status register, FMA units, and pipelined floating-point datapaths.
  - Section 18.3 (*Vector Processing and Data-Level Parallelism*): Contrasted packed SIMD with Cray-style scalable vectors, detailed the RISC-V Vector (RVV) extension architecture (`VLEN`, `ELEN`, `SEW`, `LMUL`), dynamic configuration via `vsetvli`, vector stripmining assembly loops, multi-lane execution datapaths, and memory access modes (unit-stride, strided, indexed gather/scatter).
  - Section 18.4 (*Graphics Processing Units (GPUs) and Massively Parallel SIMT*): Analyzed Single Instruction, Multiple Threads (SIMT) vs SIMD, warp/wavefront execution, zero-overhead multithreaded warp schedulers for latency hiding, branch divergence management via active masks and divergence stacks, and open-source RISC-V SIMT GPGPU architectures (Vortex).
  - Section 18.5 (*Digital Signal Processors (DSPs) and Embedded Compute*): Explored FIR/FFT computational kernels, fixed-point $Q$-format arithmetic with saturation and guard bits, dual-memory Harvard architectures (X/Y memories), dedicated Address Generation Units (AGUs) for circular modulo and bit-reversed addressing, and the RISC-V packed-SIMD `P` extension.
  - Section 18.6 (*Tensor and Matrix Processors for Artificial Intelligence*): Analyzed GEMM computational kernels, arithmetic intensity and the roofline model, 2D systolic array architectures with weight-stationary dataflows (Google TPU), sub-word low-precision numerics (INT8, FP8, BF16), and the emerging RISC-V Matrix (`RV-M`) extension.
  - Section 18.7 (*Comparative Synthesis of DSAs*): Cross-cutting comparative synthesis (Table 18.3) contrasting CPUs, Vectors, GPUs, DSPs, and TPUs across instruction bandwidth, control overhead, memory organization, and peak energy efficiency.
- **Hardware Multithreading: Exploiting Thread-Level Parallelism (Chapter 16)**:
  - Expanded Chapter 16 (`Latex/04_16_Performance_Optimized_Organization.tex`) with Section 16.6 on hardware multithreading.
  - Contrasted single-threaded horizontal and vertical issue slot waste with multithreaded slot utilization (Figure 16.12).
  - Detailed taxonomy of fine-grained/interleaved (barrel) multithreading (citing Burton Smith on the Denelcor HEP, 1978), coarse-grained (blocked) multithreading, and Simultaneous Multithreading (SMT) (citing Dean Tullsen, Susan Eggers, and Henry Levy, ISCA 1995).
  - Analyzed microarchitectural resource sharing trade-offs between statically partitioned and dynamically shared hardware structures.
  - Examined architectural touchpoints: hardware thread IDs (`mhartid`), atomic memory operations (`LR`/`SC`, AMOs in RISC-V `A` extension), memory fences (`FENCE`, `FENCE.I`), and memory consistency models (RVWMO vs TSO).
- **Textbook Chapter Structure & Downstream Renumbering**:
  - Renamed `Latex/04_18_1_RISCV.tex` to `Latex/04_19_1_RISCV.tex` (Chapter 19: The RISC-V Processor).
  - Renamed `Latex/04_18_2_RISCV_pipelined.tex` to `Latex/04_19_2_RISCV_pipelined.tex`.
  - Updated `Latex/00_main.tex` inclusion order, aligning Chapter 20 naturally with `04_20_pRISC_heterogeneous.tex`.
- **Performance Optimized Organization: Pipelining and ILP (Chapter 16)**:
  - Renamed `Latex/04_16_ILP.tex` to `Latex/04_16_Performance_Optimized_Organization.tex` and updated master inclusion in `Latex/00_main.tex`.
  - Added introductory pedagogical bridge connecting gate-level logic optimizations (Chapter 15) to microarchitectural system organization (Chapter 16).
  - Formalized processor performance equations ($\text{Execution Time} = \text{IC} \times \text{CPI} \times T_{clock}$), pipeline CPI degradation under stalls, and speedup formulas.
  - Detailed barrel multithreading microarchitectural principles, explaining how $N = 4$ round-robin interleaved threads across the $k = 4$ stage toyRISC pipeline structurally guarantee zero hazards without stalls or forwarding multiplexers.
  - Authored Section 16.3.1 analyzing structural hazards and mandatory hardware replication (dedicated PC incrementer in IF vs ALU, Harvard memory ports, multi-ported register files, superscalar ALU/AGU replication), with clean subsubsection header formatting.
  - Allowed Figures 16.1, 16.2, and 16.3 to float with `[htbp]`, relocated Figure 16.2 outside the 4-stage description list, and repositioned Figure 16.3 adjacent to textual analysis to eliminate page gaps and excess whitespace.
  - Integrated Table 16.3 and Boolean equations for ALU operand forwarding controls, establishing the essential priority rule for back-to-back hazards.
  - Authored Subsection 16.3.3 framing the emergence of branch prediction when instruction prefetching, pipelining, and lookahead decoupled fetch from execution, documenting the first hardware implementation in the IBM 7030 (Stretch) supercomputer (Buchholz 1962), crediting Richard T. Blosk's Instruction Unit (1960) and Richard S. Ballance, John Cocke, and Harwood G. Kolsky's Look-Ahead Unit (1962) for static "predict untaken" and speculative rollback, and detailing the independent invention of dynamic 2-bit branch prediction by Tom McWilliams and Curt Widdoes on the S-1 supercomputer at LLNL (1977/1979) and James E. Smith at CDC and UW-Madison (1981, 1983).
  - Corrected 2-bit counter description to a 4-state automaton and added step-by-step trace Table 16.5 comparing 1-bit vs 2-bit dynamic branch predictors on loops, illustrating the elimination of the loop re-entry misprediction penalty ($(N-1)/N$ vs $(N-2)/N$).
  - Authored coverage of dynamic branch prediction foundations citing J. E. Smith (1981) and Lee & A. J. Smith (1984), and added Subsection 16.3.4 crediting Tse-Yu Yeh and Yale N. Patt (1991, 1992) for two-level adaptive branch prediction, Yale Patt's HPS speculative architecture (1985), GHR/BHR and PHT taxonomy, and Scott McFarling's gshare and tournament combining predictors (1993).
  - Authored Section 16.5 expansions on dynamic scheduling with full bibliographic citations: Seymour Cray and James E. Thornton's CDC 6600 Scoreboard (4 stages, stalls on WAR/WAW), Robert M. Tomasulo's IBM 360/91 algorithm (3 stages, distributed reservation stations, dynamic register renaming, CDB broadcast), and James E. Smith and Andrew R. Pleszkun's Reorder Buffer (ROB) supporting precise interrupts and in-order retirement with speculative branch recovery, along with 3-generation comparison Table 16.6.
  - Added analytical Problems 16.5 (Pipeline Speedup and Imbalance Analysis), 16.6 (Forwarding Unit Hazard Equations and Priority), and 16.7 (Branch Predictor Performance and CPI Penalty) complete with step-by-step worked mathematical solutions.
- **The RISC-V Processor: Architecture, Implementation, and Verification (Chapter 18)**:
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
