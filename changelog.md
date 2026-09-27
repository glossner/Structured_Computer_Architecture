# 09/27/2026 16:20 Single-Stream FIR/MAC Acceleration via Pipelined Multiplier Datapath, Intermediate Registers, and Forwarding in Chapter 19
* **2-Stage Pipelined Multiplier Architecture (`PipelinedMultiplier.scala`, Section 19.8)**:
  * Stage 1 in EX (`PipelinedMultiplierStage1`): 17-partial-product Radix-4 Booth recoding and 6-level CSA compressor tree reducing to two 64-bit redundant vectors ($Sum$ and $Carry$) in carry-free fashion with delay $\approx 0.61\,\text{ns}$, well within the EX stage timing budget.
  * Intermediate Pipeline Registers (`ex_mem`): Synchronously latches intermediate vectors (`mulSum`, `mulCarry`, `mulOp`, `isMul`) into the inter-stage register, completely decoupling tree reduction from final addition.
  * Stage 2 in MEM/WB (`PipelinedMultiplierStage2`): 64-bit vector-merging adder computing final sum and slicing $[31:0]$ for `MUL` or $[63:32]$ for `MULH*`, requiring $\approx 0.52\,\text{ns}$.
* **Zero-Stall Multiply-Accumulate (MAC) Forwarding**:
  * Integrated with the distance-1 forwarding network. In canonical FIR/MAC loops (`mul t0, t1, t0` followed by `add a5, t0, a5`), `add` enters EX while `mul` enters MEM/WB without hazard stalls (`isLoadUse` = 0).
  * Stage 2 produces `mulStage2Result` onto `exMemFwdData`, which is bypassed directly to ALU operand A in EX with **0 stall cycles**, sustaining an issue rate of **1 instruction entering the pipeline per clock cycle**!
* **ASAP7 7nm FinFET Physical Synthesis**:
  * Standalone 2-stage pipelined multiplier synthesized to 6,891 cells, 127 DFFs, and $554.07\,\mu\text{m}^2$ with a maximum stage delay of $0.65\,\text{ns}$ ($3.3\times$ peak operation throughput over combinational Booth).
  * Full core `RiscvPipelined` with pipelined multiplier synthesized to 19,079 cells, 1,563 DFFs, and **1,729.57 $\mu$m$^2$ silicon area**—a **22.6% area reduction** ($-505.98\,\mu\text{m}^2$, saving 3,630 standard cells) over the unpipelined core ($2,235.55\,\mu\text{m}^2$) due to standard cell drive-strength optimization.
  * The operating clock period was restored from $2.462\,\text{ns}$ ($406.2\,\text{MHz}$) to **1.363 ns (733.7 MHz)**, delivering an immediate **+80.6% frequency boost**.
* **Benchmark Verification and Speedup**:
  * Verified 100% pass across all 38 RV32I and 4 Zmmul official conformance tests.
  * On the DSP hardware multiply benchmark (804 instructions, 999 cycles), execution time dropped from $3,338.21\,\text{ns}$ (single-cycle) and $2,459.54\,\text{ns}$ (unpipelined core with bypassing) down to **1,361.64 ns (1.36 $\mu$s)**, achieving a **2.45x speedup** over single-cycle, **2.36x** over unforwarded pipeline, and **1.81x** over the unpipelined forwarded core.

# 09/27/2026 15:15 Hardware Data Bypassing, ASAP7 7nm FinFET Synthesis, and CoreMark/Dhrystone Rerun in Chapter 19
* **Hardware Implementation of Bypassing (`RTL/Chisel/src/main/scala/scabook/riscv/RiscvPipelined.scala`, Section 19.7)**:
  * Implemented distance-1 forwarding (MEM/WB to EX) for ALU inputs, branch comparison, `jalr` target calculation, and store data (`rs2Data`).
  * Implemented distance-2 write-through bypassing (WB to ID) directly at the register file read ports.
  * Revised Hazard Detection Unit: eliminated all RAW stalls for ALU operations (0 stalls); retained only the single irreducible Load-Use hazard (1 stall cycle).
* **Architectural Conformance Verification (`RiscvPipelinedConformanceSpec.scala`)**:
  * 100% PASS across all 38 RV32I tests (103,268 cycles, -38.8%) and 4 Zmmul tests (14,714 cycles, -55.3%) matching Spike golden reference signatures.
* **ASAP7 7nm FinFET Physical ASIC Synthesis (`Latex/04_19_2_RISCV_pipelined.tex`, Table 19.13)**:
  * Total cells: 11,974 (+390 cells, +3.4%).
  * Sequential DFFs: 1,434 (0 overhead).
  * Total Silicon Area: **$1,158.95\,\mu\text{m}^2$** vs. $1,128.16\,\mu\text{m}^2$ (**merely +2.7% area overhead!**).
  * Standalone Critical Path: $1.589\,\text{ns}$ ($629.2\,\text{MHz}$) vs. $1.362\,\text{ns}$ ($734.0\,\text{MHz}$).
* **Benchmark Rerun & Stall Elimination (Table 19.12)**:
  * **EEMBC CoreMark 1.0**: 1,160,199 cycles (CPI = 1.33 vs. 1.88), **482,861 stalls eliminated (-62.9%)**, execution time 1.58 ms ($1.42\times$ over unforwarded, **$1.69\times$ over single-cycle**), score = **632.4 CoreMarks** (+41.8%).
  * **Dhrystone 2.1**: 19,958 cycles (CPI = 1.44 vs. 1.76), **4,353 stalls eliminated (-41.5%)**, execution time 27.20 $\mu$s ($1.22\times$ over unforwarded, **$1.55\times$ over single-cycle**), score = **418.5 DMIPS** (+21.9%).
  * **DSP Kernel (Soft Mul)**: 8,774 cycles (CPI = 1.35), **3,801 stalls eliminated (-62.7%)**, speedup = **$1.43\times$** over unforwarded ($1.66\times$ over single-cycle).
  * **DSP Kernel (Hard Mul)**: 999 cycles (CPI = 1.24), **309 stalls eliminated (-61.3%)**, speedup = **$1.31\times$** over unforwarded ($1.36\times$ over single-cycle).

# 09/27/2026 14:45 Physical Synthesis Realities: EDA Baseline Multipliers vs. Explicit Architectures in Chapter 15
* **EDA Baseline Multiplier Mechanics (`Latex/04_15_Performance_Optimized_Digital_Logic.tex`, Section 15.3.6)**:
  * Documented the internal EDA transformation of HDL multiplication expressions (`*` / `$mul`) in Yosys and Berkeley ABC. Proved that Yosys does not synthesize naive shift-and-add logic; the `alumacc` pass transforms operations into multiply-accumulate macro-cells (`$macc`), and `maccmap` decomposes `$macc` into a bit-slice carry-save compressor tree with a Brent-Kung parallel prefix vector-merging adder (`$lcu_brent_kung`), followed by ABC AIG cut-based delay rewriting.
  * Revealed that supporting RISC-V `Zmmul` dynamic signedness caused Yosys to instantiate two complete 64-bit carry-save compressor trees behind an output multiplexer, explaining the 10,240 cell count and $1,083.36\,\mu\text{m}^2$ baseline area.
* **Why Array Multipliers Synthesized to Lowest Delay ($1.968\,\text{ns}$)**:
  * Demonstrated that single-gate 2-input AND partial product generation requires merely $\approx 15\,\text{ps}$ in ASAP7 7nm FinFET without recoding or multiplexing overhead, allowing ABC's AIG cut-based optimization to balance the flat multi-operand sum into a shallow reduction tree that reaches the vector-merging adder earlier than architectures with complex front-ends.
* **The Radix-4 Booth Trade-Off**:
  * Detailed the front-end recoding penalty (3-bit window decode, 5:1 multiplexing, negation logic = $\approx 150\text{--}200\,\text{ps}$) versus tree reduction savings (saving 2 carry-save levels = $\approx 80\,\text{ps}$). At 32 bits, the front-end penalty exceeds tree savings, making unpipelined Booth slightly slower ($2.160\,\text{ns}$ vs. $1.968\,\text{ns}$).
  * Proved that Booth achieves a massive **57.1% area reduction** ($464.45\,\mu\text{m}^2$, lowest of all architectures) and eliminates routing congestion. Demonstrated that for 64-bit and 128-bit datapaths, logarithmic tree savings easily dominate the constant front-end penalty.
* **Wallace Tree Limitations in Standard Cells**:
  * Analyzed why explicit structural netlists of full adders restrict ABC Boolean optimization and how irregular, non-planar wiring creates routing congestion and parasitic wire RC delays in deep-submicron FinFET nodes ($2.089\,\text{ns}$).
* **Redundant Binary Multipliers in Standard Cells vs. Custom DSP Silicon**:
  * Explained why automated standard-cell synthesis penalizes RBM ($1,072.65\,\mu\text{m}^2$, $2.246\,\text{ns}$) due to dual-rail encoding and scattered standard-cell placement, contrasting this with custom DSP datapaths (e.g., the multithreaded Sandblaster DSP) where pitch-matched bit-slice tiling and modular H-tree interconnects provide deterministic wire lengths and noise immunity.
  * Highlighted that DSP multiply-accumulate (MAC) loops accumulate directly in redundant binary format across cycles in constant $O(1)$ time, eliminating the final two's complement subtractor from the inner loop critical path.

# 09/27/2026 14:30 High-Performance Multipliers in Chapter 15 and ASAP7 7nm FinFET Synthesis for Zmmul Acceleration in Chapter 19
* **High-Performance Multipliers in Chapter 15 (`Latex/04_15_Performance_Optimized_Digital_Logic.tex`)**:
  * Added Section 15.3 on high-performance multipliers covering:
    * 3-phase hardware multiplication: partial product generation, partial product reduction, and final carry-propagate addition.
    * Array Multiplier: linear carry-save accumulation in planar 2D systolic grid ($O(N)$ delay, $O(N^2)$ area).
    * Radix-4 Modified Booth Recoding: 3-bit window recoding with digit set $\{-2, -1, 0, +1, +2\}$, halving partial product rows from 32 to 17.
    * Wallace and Dadda Carry-Save Reduction Trees: 3:2 and 2:2 counters reducing rows in $O(\log N)$ logic depth, contrasting eager vs. deferred compression and analyzing standard-cell interconnect routing congestion.
    * Redundant Binary Multiplier (RBM): Redundant Binary Signed-Digit (RBSD) representation $\{-1, 0, 1\}$ encoded as $(x^+, x^-)$, constant-time $O(1)$ carry-free Redundant Binary Adder (RBA) cells with adjacent condition testing ($p_{i-1} = (x_{i-1} + y_{i-1} > 0)$), balanced binary reduction tree ($2 \to 1$ at every node) of depth $\lceil \log_2 N \rceil = 5$, and final RB-to-two's-complement conversion. Highlighted its historical and practical prominence in high-performance digital signal processor (DSP) design (such as the Sandblaster DSP architecture), where strict layout modularity, predictable interconnect lengths, and uniform bit-slice pitch matching avoid the wiring chaos of Wallace trees.
* **Chisel Implementations and Verification (`RTL/Chisel/src/main/scala/scabook/multipliers/`)**:
  * Implemented `ArrayMultiplier`, `BoothMultiplier`, `WallaceTreeMultiplier`, and `RedundantBinaryMultiplier` supporting 32-bit signed and unsigned operations.
  * Created `MultiplierSuiteSpec.scala` testing zero, identity, edge cases, signed/unsigned modes, and randomized vectors with 100% pass rate.
  * Created `GenerateMultipliers.scala` and generated clean SystemVerilog netlists via CIRCT `firtool`.
* **ASAP7 7nm FinFET Physical ASIC Synthesis of Multipliers (`Latex/04_19_2_RISCV_pipelined.tex`)**:
  * Synthesized all 5 multiplier architectures to gate-level netlists using Yosys and ABC targeting the predictive ASAP7 7nm FinFET standard cell library (`asap7sc7p5t_28`, RVT, $0.70\,\text{V}$, $25^\circ\text{C}$, Typical TT corner, $C_{\text{load}} = 2.0\,\text{fF}$):
    * Behavioral Multiplier (Baseline): 10,240 cells, $1,083.36\,\mu\text{m}^2$, $2,148.00\,\text{ps}$ ($2.148\,\text{ns}$), $0.466\,\text{GHz}$ (baseline).
    * Array Multiplier: 5,405 cells, $527.04\,\mu\text{m}^2$, **1,967.86 ps (1.968 ns)**, **0.508 GHz** ($-51.4\%$ area, **1.09x speedup**).
    * Radix-4 Modified Booth Multiplier: 5,590 cells, **464.45 $\mu$m$^2$ (Lowest Area!)**, $2,159.88\,\text{ps}$ ($2.160\,\text{ns}$), $0.463\,\text{GHz}$ (**-57.1% area**, $0.99\times$).
    * Wallace Tree Multiplier: 7,293 cells, $627.77\,\mu\text{m}^2$, $2,088.55\,\text{ps}$ ($2.089\,\text{ns}$), $0.479\,\text{GHz}$ ($-42.0\%$ area, $1.03\times$).
    * Redundant Binary Multiplier (RBM): 14,920 cells, $1,072.65\,\mu\text{m}^2$, $2,245.91\,\text{ps}$ ($2.246\,\text{ns}$), $0.445\,\text{GHz}$ ($-1.0\%$ area, $0.96\times$).
* **Multiplier Selection, Chisel Integration, and Pipeline Balancing (`Latex/04_19_2_RISCV_pipelined.tex`)**:
  * Selected the Radix-4 Booth Multiplier as the primary area-optimized multiplier for standard-cell cores (while noting RBM for regular DSP datapaths). Provided concise Chisel code showing only the multiplier invocation.
  * Demonstrated how internally pipelining the multiplier into 4 balanced stages of $\approx 0.50\text{--}0.54\,\text{ns}$ resolves the Zmmul frequency collapse, enabling the processor to operate at the full **1.67 GHz** clock frequency ($2.27\times$ over the unoptimized baseline) while delivering single-cycle multiplier throughput.

# 09/27/2026 13:55 Balancing the RV32I Pipeline: ASAP7 7nm FinFET Synthesis of High-Performance Adders and Selection
* **Diagnosis of Ripple-Carry Adder Bottleneck (`Latex/04_19_2_RISCV_pipelined.tex`)**:
  * Highlighted that the baseline ALU's unoptimized 32-bit ripple-carry adder (RCA, $1.045\,\text{ns}$) has approximately $2\times$ the delay of any other pipeline stage in the core ($T_{\text{fetch}} = 0.550\,\text{ns}$, $T_{\text{decode}} = 0.540\,\text{ns}$, $T_{\text{sram\_wb}} = 0.600\,\text{ns}$).
  * Showed that this slow adder acts as the primary bottleneck limiting pipelined clock frequency to $733.7\,\text{MHz}$ despite the other stages supporting $>1.6\,\text{GHz}$.
* **ASAP7 7nm FinFET Synthesis of Chapter 15 Adders (`RTL/Chisel/src/main/scala/scabook/adders/`, `Latex/04_19_2_RISCV_pipelined.tex`)**:
  * Implemented, unit-tested (100% pass rate in `AdderSuiteSpec.scala`), and synthesized five 32-bit adder architectures from Chapter 15 to physical gate-level netlists using the ASAP7 7nm FinFET predictive PDK:
    * Ripple-Carry Adder (RCA): 106 cells, $13.91\,\mu\text{m}^2$, $1,045.43\,\text{ps}$ ($1.045\,\text{ns}$), $0.96\,\text{GHz}$ (baseline).
    * Carry Lookahead Adder (CLA): 122 cells, $12.98\,\mu\text{m}^2$, $987.99\,\text{ps}$ ($0.988\,\text{ns}$), $1.01\,\text{GHz}$ ($1.06\times$ speedup).
    * Brent-Kung Parallel Prefix Adder (BKA): 144 cells, $14.83\,\mu\text{m}^2$, $836.25\,\text{ps}$ ($0.836\,\text{ns}$), $1.20\,\text{GHz}$ ($1.25\times$ speedup).
    * Kogge-Stone Parallel Prefix Adder (KSA): 190 cells, $17.48\,\mu\text{m}^2$, $712.57\,\text{ps}$ ($0.713\,\text{ns}$), $1.40\,\text{GHz}$ ($1.47\times$ speedup).
    * Carry Select Adder (CSA): 190 cells, $18.01\,\mu\text{m}^2$, **519.83 ps (0.520 ns)**, **1.92 GHz** (**2.01x speedup**).
* **Adder Selection, Chisel Integration, and Pipeline Balancing (`Latex/04_19_2_RISCV_pipelined.tex`)**:
  * Selected the Carry Select Adder as the optimal high-speed adder for 32-bit standard cells, reducing adder latency by $50.3\%$.
  * Inserted Section 19.4 directly after the RV32I pipelined processor, presenting comparative synthesis results, selecting the optimal adder, and providing a clean Chisel code listing illustrating only the adder invocation.
  * Demonstrated that replacing the RCA balances all four pipeline stages ($T_{\text{fetch}} = 0.550\,\text{ns}$, $T_{\text{decode}} = 0.540\,\text{ns}$, $T_{\text{execute}} \approx 0.600\,\text{ns}$, $T_{\text{sram\_wb}} = 0.600\,\text{ns}$), dropping the worst-case clock period to $0.600\,\text{ns}$ and unlocking an operating frequency of **1.67 GHz** ($+127.2\%$ frequency boost, $2.27\times$ over the unoptimized baseline).

# 09/27/2026 13:30 4-Stage Pipelined RV32I: 4-Step Pipeline Delay Model (SRAM Fetch, Decode, Execute, SRAM Write-Back) in ASAP7 7nm FinFET, No Forwarding, and Benchmark Evaluation
* **ASAP7 7nm FinFET Physical ASIC Synthesis & Microarchitectural Multiplier Case Study (`Latex/04_19_2_RISCV_pipelined.tex`)**:
  * Migrated technology process from SkyWater 130nm to the predictive ASAP7 7nm FinFET PDK (`asap7sc7p5t_28`, 7.5-track standard cells, RVT, $V_{dd} = 0.70\,\text{V}$, $25^\circ\text{C}$, Typical TT corner, $C_{\text{load}} = 2.0\,\text{fF}$) using Yosys and ABC.
  * Synthesized single-cycle `RV32I` core (9,730 cells, 1,023 DFFs, $904.24\,\mu\text{m}^2$) and 4-stage pipelined `RV32I` core (11,584 cells, 1,434 DFFs, $1,128.16\,\mu\text{m}^2$, +24.8% area).
  * Synthesized standalone ALUs and full cores with `Zmmul` hardware multiplier: unpipelined $32 \times 32$ multiplier logic array caused an 11.5$\times$ ALU area explosion ($103.31 \to 1,186.67\,\mu\text{m}^2$) and +105.3% delay ($1.047 \to 2.148\,\text{ns}$). Monolithic integration in EX stage collapsed pipelined core frequency to $406.2\,\text{MHz}$ (+68.7% gain over single-cycle vs. +124.0% for pure RV32I), demonstrating Amdahl's Law for clock periods.
* **The 4-Step Pipeline Delay Model (`Latex/04_19_2_RISCV_pipelined.tex`, `RTL/Chisel/src/test/scala/scabook/riscv/RiscvBenchmarkSpec.scala`)**:
  * Decomposed execution into four physical steps: Step 1 SRAM Fetch ($T_{\text{fetch}} = 0.550\,\text{ns}$), Step 2 Decode and Register Read ($T_{\text{decode}} = 0.540\,\text{ns}$), Step 3 Execute ($T_{\text{execute}} = 1.363\,\text{ns}$ pure RV32I, $2.462\,\text{ns}$ Zmmul), and Step 4 SRAM Data Memory and Write-Back ($T_{\text{sram\_wb}} = 0.600\,\text{ns}$).
  * In the single-cycle Fetch-Execute (FE) core, the clock period is the sum of all 4 steps: $T_{\text{clk, FE}} = 0.550 + 0.540 + 1.363 + 0.600 = \mathbf{3.053\,\text{ns}}$ ($327.5\,\text{MHz}$).
  * In the 4-stage pipelined core, the clock period is the max of the 4 steps: $T_{\text{clk, pipe}} = \max(0.550, 0.540, 1.363, 0.600) = \mathbf{1.363\,\text{ns}}$ ($733.7\,\text{MHz}$).
  * Clock period delta $\Delta T = T_{\text{clk, FE}} - T_{\text{clk, pipe}} = \mathbf{1.690\,\text{ns}}$ ($-55.4\%$), providing a **2.240$\times$ frequency multiplier** (+124.0%).
* **Baseline No-Forwarding Microarchitecture & Conformance (`RTL/Chisel/src/main/scala/scabook/riscv/RiscvPipelined.scala`)**:
  * Removed write-through bypass from MEM/WB to ID to establish pure unbypassed baseline: distance-1 RAW hazards stall 2 cycles, distance-2 RAW hazards stall 1 cycle.
  * Achieved 100% pass rate across all 38 official RISC-V `RV32I` architectural test suites (168,638 cycles) and all 4 official `Zmmul` test suites (32,892 cycles) with exact golden signature matches against Spike.
* **Empirical Workload Benchmarking & Pipeline Balancing Analysis (`Latex/04_19_2_RISCV_pipelined.tex`, `RTL/Chisel/src/test/scala/scabook/riscv/RiscvBenchmarkSpec.scala`)**:
  * Executed DSP kernel (soft mul: 6,511 insts, Single = 19,878 ns, Pipe = 17,140 ns, CPI 1.93, speedup = **1.16$\times$**; hard mul: 804 insts, Single = 3,338 ns, Pipe = 3,220 ns, CPI 1.63, speedup = **1.04$\times$**), Dhrystone 2.1 (20 runs, Single = 42,211 ns, Pipe = 33,136 ns, CPI 1.76, Single: 0.824 DMIPS/MHz, 269.9 DMIPS; Pipe: 0.468 DMIPS/MHz, 343.4 DMIPS, speedup = **1.27$\times$**), and EEMBC CoreMark 1.0 (1 iter, 874,891 insts, Single = 2,671,042 ns, Pipe = 2,239,491 ns, CPI 1.88, Single: 1.143 CoreMark/MHz, 374.3 CoreMarks; Pipe: 0.608 CoreMark/MHz, 446.1 CoreMarks, speedup = **1.19$\times$**).
  * Analyzed why the pipelined `RV32I` runs only a tiny bit faster ($1.04\times\text{--}1.27\times$): stage delay quantization limits frequency scaling to $2.24\times$, sequential register overhead adds $\approx 55\,\text{ps}$ per stage boundary, and without forwarding, RAW hazard stalls expand CPI to $1.63\text{--}1.93$, devouring most of the $2.24\times$ frequency multiplier.

# 09/27/2026 12:45 Overleaf Compatibility: Remove Symbolic Links
* **Overleaf Compatibility (`Latex/latexcad/latexcad.sty`)**:
  * Removed git symbolic link files from repository to satisfy Overleaf's git import engine.
  * Converted `Latex/latexcad/latexcad.sty` to a standard regular file (mode 100644).

# 09/27/2026 12:35 RV32I Workload Evaluation: Dhrystone 2.1, EEMBC CoreMark 1.0, Physical Pipeline Balancing Barriers, and Benchmark Taxonomy
* **RV32I Workload Evaluation (`Latex/04_19_2_RISCV_pipelined.tex`, `RTL/Chisel/src/test/scala/scabook/riscv/RiscvBenchmarkSpec.scala`, `Latex/bibliography.bib`)**:
  * Ported and executed Dhrystone 2.1 (20 iterations) and EEMBC CoreMark 1.0 (1 iteration, 2 KB data footprint, 874,891 dynamic instructions) bare-metal on both single-cycle (`RiscvFetchExecute`) and 4-stage pipelined (`RiscvPipelined`) pure `RV32I` cores. Both benchmarks achieved 100% data validation matching golden signatures (`tohost = 1` with full CRC pass).
  * Measured dynamic execution cycles and effective CPI under active RAW hazard stalling and EX branch flushes: Dhrystone 2.1 executed in 13,826 single-cycle cycles (CPI = 1.00) vs. 20,460 pipelined cycles ($\text{CPI} = 1.48$, +48.0% stall penalty); CoreMark executed in 874,891 single-cycle cycles (CPI = 1.00) vs. 1,340,964 pipelined cycles ($\text{CPI} = 1.53$, +53.3% stall penalty). The 4-stage pipeline achieved $0.556\,\text{DMIPS/MHz}$ ($77.55\,\text{DMIPS}$ at system frequency) and $0.746\,\text{CoreMark/MHz}$ ($104.04\,\text{CoreMarks}$ at system frequency).
  * Analyzed why the naive theoretical $N = 4.0\times$ speedup is fundamentally inaccessible in physical silicon:
    1. **Stage Delay Imbalance / Quantization**: Logic is indivisible; the 7.17 ns EX stage bounds operating frequency, limiting frequency scaling to $10.57 / 7.17 = 1.474\times$ and eliminating 63.1% of theoretical speedup before any code executes.
    2. **Sequential Register Overhead**: Flip-flop setup, clock-to-Q, and clock skew impose $0.80\,\text{ns}$ of dead delay per stage ($3.20\,\text{ns}$ across 4 stages).
    3. **Hazard-Induced CPI Expansion**: RAW stalls and branch flush bubbles degrade CPI to $1.48\text{--}1.53$, resulting in core-only speedups of $0.96\times\text{--}0.996\times$ (pipelined core is slightly slower than single-cycle in isolation).
  * Demonstrated that the true system-level advantage of pipelining arises from decoupling and overlapping instruction fetch and data memory accesses across separate clock cycles. With realistic 5 ns SRAM, system clock frequency increases by $2.869\times$ ($48.61\,\text{MHz} \to 139.47\,\text{MHz}$), converting the core-only deficit into a robust **1.87$\times$ (CoreMark) and 1.94$\times$ (Dhrystone) actual system speedup**.
  * Established a comprehensive benchmark taxonomy (SPEC CPU2017, CoreMark, Dhrystone, Embench-IoT, MLPerf Tiny). Formulated the architectural prerequisites of SPEC CPU (MMU with virtual memory paging, POSIX OS / Linux, multi-GB RAM, glibc), proving why SPEC CPU cannot run on bare-metal microcontroller cores and highlighting modern open-source alternatives (Embench-IoT).
  * Formulated analytical Problem 19.6 with complete step-by-step mathematical solutions calculating core vs. system speedups on CoreMark, memory overlapping frequency multiplier, and benchmark architectural requirements.

# 09/27/2026 12:00 4-Stage Pipelined RV32I Baseline Implementation, Architectural Conformance, SkyWater 130nm ASIC Synthesis, and Zmmul Multiplier Case Study
* **4-Stage Pipelined RV32I Baseline and Zmmul Multiplier Case Study (`RTL/Chisel/src/main/scala/scabook/riscv/RiscvPipelined.scala`, `Latex/04_19_2_RISCV_pipelined.tex`)**:
  * Designed and implemented a 4-stage pipelined processor core (`RiscvPipelined.scala`, Section 19.5) for the base `RV32I` architecture in Chisel 7.15.0, partitioning execution into IF, ID, EX, and MEM/WB stages without cache hierarchies (direct memory access).
  * Added configurable multiplier parameterization (`enableZmmul: Boolean = true`) across `RiscvALU`, `RALU`, `RiscvFetchExecute`, and `RiscvPipelined`.
  * Implemented an active Hazard Detection Unit that stalls the pipeline on RAW data dependencies against uncommitted instructions in the EX stage (freezes `pcReg` and `if_id`, injects bubble into `id_ex`).
  * Added internal register file write-through forwarding bypass from MEM/WB to ID to resolve distance-2 dependencies without stalls (0 cycles) and distance-1 dependencies with a single stall cycle (1 cycle).
  * Evaluated branch and jump target resolution in the EX stage with pipeline flushing (2-cycle penalty on taken branches/jumps, 0-cycle penalty on fall-through).
  * Verified 100% pass rate across all 38 official RISC-V `RV32I` architectural test suites with exact word-for-word golden signature matching (`RiscvPipelinedConformanceSpec.scala`, Section 19.6).
  * Formulated and executed a DSP workload kernel combining a 16-point 4-tap FIR filter and a $4 \times 4$ matrix-vector multiplication compiled for pure `RV32I` using software shift-and-add multiplication subroutines (`__mulsi3`, 6,511 executed instructions), validating identical computational results (`tohost = 0x00000711`) across single-cycle (6,511 cycles, $\text{CPI} = 1.00$) and 4-stage pipelined cores (10,645 cycles, $\text{CPI} = 1.63$).
  * Synthesized both pure `RV32I` processors to open-source SkyWater 130nm high-density standard cells (`sky130_fd_sc_hd`) using Yosys and ABC. The 4-stage pipelined core (8,438 cells, 1,434 DFFs, $68,710\,\mu\text{m}^2$) reduced critical path delay from $10.57\,\text{ns}$ to $7.17\,\text{ns}$, delivering a **+47.4% core clock frequency boost** ($94.60\,\text{MHz} \to 139.47\,\text{MHz}$). With realistic 5\,ns memory latency, pipelining overlaps memory access, yielding a **+186.9% system clock frequency boost** ($48.61\,\text{MHz} \to 139.47\,\text{MHz}$) and an **actual application speedup of 1.75$\times$** ($76,325\,\text{ns}$ vs. $133,931\,\text{ns}$).
  * Conducted a dedicated microarchitectural case study on hardware multiplication (`Zmmul`, Section 19.7): standalone ALU synthesis showed that the unpipelined $32 \times 32$ multiplier logic array caused a **14.0$\times$ ALU area explosion** ($6,341\,\mu\text{m}^2 \to 88,870\,\mu\text{m}^2$) and an $11.09\,\text{ns}$ intrinsic delay, making the multiplier alone larger than the entire pure `RV32I` pipelined core. When integrated monolithically into the EX stage, the EX stage delay expanded to $13.25\,\text{ns}$, causing a **frequency collapse** (pipelined core frequency dropped from $139.47\,\text{MHz}$ to $75.47\,\text{MHz}$, shrinking pipelining frequency gain from +47.4% to +2.95%). While hardware multiplication achieved an 8.1$\times$ dynamic instruction reduction on the DSP benchmark (804 vs. 6,511 instructions), the case study demonstrated Amdahl's Law for clock frequencies and motivated multi-cycle or internally pipelined multiplier units.
  * Consolidated Chapter 19 into clean modular sections with complete multi-tier comparison tables, expanded exercises, and configured `.gitignore` to isolate physical design intermediate files.

# 09/27/2026 10:15 Chapter 16 Organization Refinements: Structural Hazards, Branch Prediction, Out-of-Order Frontiers, and Table Legibility
* **Chapter 16 Organization Refinements (`Latex/04_16_Performance_Optimized_Organization.tex`, `Latex/bibliography.bib`)**:
  * Dynamically linked the opening introductory reference in Section 16 to Chapter 15 (`\label{chap:perf_opt_logic}`) instead of Chapter 11.
  * Removed Section 16.2 "Architectural Insight" on barrel multithreading hazard elimination to prevent redundancy with Section 16.6 (`\label{sec:barrel_multithreading}`).
  * Relocated "Superscalar Structural Hazards and Execution Unit Replication" (formerly Section 16.3.1.2) to become the first subsection under Section 16.5 (`\label{sec:superscalar_structural_hazards}`), directly motivating execution unit replication and multi-issue resource contention.
  * Renamed Section 16.3.3.3 to "Branch Prediction", and streamlined subsection headings by removing "The Genesis of" before "Dynamic Branch Prediction (Late 1970s)" and "The Evolution to" before "Modern Adaptive Predictors (1990s)".
  * Added authoritative peer-reviewed *IEEE Micro*, *MICRO*, and *ISSCC* citations for out-of-order and superscalar processors referenced in Section 16.5: IBM POWER4 / PowerPC 970 (`tendler2002power4`), Intel P6 / Pentium Pro (`papworth1996tuning`), Intel Sandy Bridge (`rotem2012power`), AMD Zen (`singh2017zen`, `suggs2020zen2`), Berkeley BOOM (`celio2018boom`), and XiangShan (`xiangshan2022micro`).
  * Redesigned Table 16.7 without `\resizebox` scaling, using `tabularx` with `\footnotesize` wrapped columns, balanced margins, and a dedicated citation row for crisp, high-legibility comparison across four generations of dynamic execution architectures.

# 09/27/2026 09:45 Chapter Sequence Alignment: Performance Optimized Architecture (Chapter 17) & ToyRISC Design (Chapter 18)
* **Chapter Sequence Alignment (`Latex/04_17_Performance_Optimized_Architecture.tex`, `Latex/04_18_0_ToyRISC.tex`)**:
  * Reordered Performance Optimized Architecture to Chapter 17 (`Latex/04_17_Performance_Optimized_Architecture.tex`), positioning the complete three-tier optimization sequence (Digital Logic in Chapter 15 $\rightarrow$ Organization in Chapter 16 $\rightarrow$ Architecture in Chapter 17) directly ahead of practical processor design implementations.
  * Renamed and reordered the ToyRISC modular processor files to Chapter 18 (`Latex/04_18_0_ToyRISC.tex`, `Latex/04_18_1_ToyRISC_organization.tex`, `Latex/04_18_2_toyRISC_implementation.tex`, `Latex/04_18_3_ToyRISC_pipelined.tex`, `Latex/04_18_4_ToyRISC_forwarding.tex`, and `Latex/04_18_5_ToyRISC_verilog.tex`).
  * Verified that dynamic cross-references across Chapters 13, 16, 17, and 18 resolve cleanly to Chapter 17 (Performance Optimized Architecture) and Chapter 18 / Section 18.1 / Listing 18.1 / Table 18.1 (ToyRISC Design).

# 09/27/2026 09:15 Front Matter: First Edition Preface Restoration & Second Edition Placeholder
* **First Edition Preface Restoration (`Latex/00_main.tex`, `README.md`)**:
  * Restored the original First Edition signed and dated entry (June 3rd, 2026) in both `Latex/00_main.tex` and `README.md`, preserving the historical toolchain statement indicating testing with Chisel 6.6.0.
* **Second Edition Preface Placeholder (`Latex/00_main.tex`, `README.md`)**:
  * Created a dedicated placeholder for the Preface to the Second Edition positioned before the First Edition preface in both `Latex/00_main.tex` and `README.md`.
  * Detailed the hardware construction environment upgrade to Chisel 7.15.0 and Scala 2.13.18 (supported by CIRCT firtool 1.158.0 and `EphemeralSimulator`).
  * Added explicit references to the BreadBoard Computer Zoo repository (`https://github.com/glossner/BBzoo`) and the KryptoNyte RISC-V processor family repository (`https://github.com/Ypologist/KryptoNyte`).

# 09/27/2026 09:10 ToyRISC Architecture and ISA Migration to Chapter 17 (Chapters 13 & 17)
* **ToyRISC Architecture, Organization, and ISA Migration (`Latex/04_17_1_ToyRISC_organization.tex`, `Latex/03_13_harvard_5-OS.tex`)**:
  * Migrated non-redundant ToyRISC architecture, organization, loop-connected automata analysis, ISA specifications, opcode macro definitions (`A11_DEFINES.vh`), 28-instruction opcode Table 17.1, program execution trace Example 17.1, and single-cycle critical-path timing formulations from Section 13.5 into Chapter 17 (Section 17.1, `Latex/04_17_1_ToyRISC_organization.tex`).
  * Preserved Section 13.5 in `Latex/03_13_harvard_5-OS.tex` strictly commented out in source code, eliminating redundancy while retaining introductory reference paragraphs pointing to Chapters 16 and 17.
  * Resolved all internal cross-references across Chapters 12, 13, 16, and 17 (`sec:toyrisc_arch`, `tab:riscISA`, `lst:toyRISCdefines`, `exADD`, and `interrupt`) with zero undefined warnings and clean typography.

# 09/27/2026 08:50 Early Stored-Program Citations, Cache Consolidation, and Range Typography (Chapters 12, 13, & 16)
* **Early Physical 4-OS Landmark Citations (`Latex/03_12_von_neumann_4-OS.tex`)**:
  * Added authoritative historical citations in Section 12.4 for early physical single-level stored-program implementations: Maurice V. Wilkes and William Renwick's Cambridge EDSAC (`wilkes1949edsac`, the first operational stored-program computer in regular service, utilizing mercury delay lines), the Moore School EDVAC (`vonNeumann1945`, `gluck1953edvac`), and the Princeton IAS machine designed by Arthur W. Burks, Herman H. Goldstine, and John von Neumann (`burks1946preliminary`, utilizing Williams-Kilburn cathode-ray tubes).
  * Added explicit cross-references pointing to downstream microarchitectural optimizations: cache memory (Section 16.7.3) and virtual memory paging/TLBs (Section 16.7.4).
  * Removed redundant introductory paragraph (`\paragraph{Microarchitectural Optimization vs. Fundamental Architecture}`).
* **Mathematical Range Typography Standardization (`Latex/03_12_von_neumann_4-OS.tex`, `Latex/04_16_Performance_Optimized_Organization.tex`)**:
  * Replaced 22 non-standard occurrences across Chapters 12 and 16 where `\div` was erroneously rendered for intervals/ranges with standard LaTeX en-dashes (`\text{--}`), while strictly preserving arithmetic division operations in Chapter 1.
* **Cache Architecture Consolidation (`Latex/03_13_harvard_5-OS.tex`, `Latex/04_16_Performance_Optimized_Organization.tex`)**:
  * Removed redundant Section 13.3 ("Cache Memories") from Chapter 13, consolidating comprehensive cache microarchitecture into Section 16.7.3 while citing Maurice Wilkes' 1965 slave memories (`wilkes1965slave`) and the IBM System/360 Model 85 (`liptay1968cache`).
* **ToyRISC Modular Organization (`Latex/03_13_harvard_5-OS.tex`)**:
  * Commented out Section 13.5 ("ToyRISC Processor") to eliminate redundancy with Chapter 17 (ToyRISC Design), preserving all original text and figures in source comments.
  * Added an introductory reference paragraph under "How an Instruction Set Architecture is Designed" directing readers to Chapter 17 (`\label{toyRiscDesign}`) and Chapter 16 (`\label{lect5}`).

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
