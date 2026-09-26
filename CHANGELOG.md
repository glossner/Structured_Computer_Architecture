# Changelog

All notable changes to the Structured Computer Architecture project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased] - 2026-09-26

### Added
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
