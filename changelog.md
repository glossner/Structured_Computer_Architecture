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
