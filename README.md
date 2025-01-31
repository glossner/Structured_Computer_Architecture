> **Question:** *Can we have a procedure that, receiving a program and the data it processes, decides if the program will stop executing in a finite time?*  

This (halting) problem was formulated in computational terms in 1936 by Alonzo Church, Stephen Kleene, Emil Post, and Alan Turing as a reaction to the incompleteness theorem published by Kurt Gödel in 1931.

> **Answer**: *No!*

And thus, the most important negative result in the history of mathematics underpins the science of computation.


---
# Preface to Preliminary Release v0.1

This textbook arose from an introductory course on Computer Architecture taught at Rivier University in the Fall of 2024. 

Unlike other Computer Architecture textbooks, this book approaches the subject from a Computer Science theoretical perspective. Using an ordered approach, we start with the most basic 0-order computing system - one with stateless operations. We then describe computing systems in terms of their feedback loop orders progressing order-by-order (Stefan, 2024). Table 1 shows the progression of computing systems feedback loops.

## Orders of Computing Systems

| **Order**               | **Definition**                            | **Components**                                     | **Example**                                    |
|--------------------------|-------------------------------------------|---------------------------------------------------|-----------------------------------------------|
| 0-Order                 | No-loop combinational circuits.           | Logic gates (AND, OR, NOT, ROM).                  | Multiplexer, adder, ALU.                      |
| 1st-Order               | One-loop memory circuits                  | Latches, FFs, registers, RAM, SRAM.              | D flip-flop, Register file                    |
| 2nd-Order               | Two-loop automata circuits.               | Mealy, Moore automaton                           | FSMs, Counters, ALUs with register files.     |
| 3rd-Order               | Three-loops processing units.             | ALU, control units, registers.                   | Simple microprocessor, e.g., CPU.             |
| 4th-Order               | Four-loop computer with one memory        | Program and Data in single memory.               | von Neumann abstract model computer.          |
| 5th-Order               | Five-loop computer with two memories.     | Separate Program and Data memories.              | Harvard abstract model computer.             |
| nth-Order High-Order 0  | N-loop, no global loop many-cell systems. | Controller, Map Array                            | Map Parallel Accelerator                      |
| nth-Order 1st-High Order| N-loop & 1st global loop many-cell systems.| Controller, Map Array, Reduce loop               | MapReduce Parallel Accelerator                |
| nth-Order 2nd-High Order| N-loop & 2nd global loop many-cell systems.| Controller, Map Array, Reduce loop, Scan loop    | MapScanReduce Parallel Accelerator            |


*Table 1: Orders of Computing Systems*

Part I of this book provides a mathematical foundation for the number systems used in computing systems. Part II covers orders 0 through 3 - basic combinational logic through a processor. Part III covers the theory and implementation of computing including parallel heterogeneous systems. Part IV refers to practical aspects related to the design of frequently used combinational circuits, the design of processors, and the use of heterogeneous computing systems. 

This book also differs from one that deals with digital systems in that it pays attention only to those digital systems that contribute directly to the simplest structures that allow the enunciation of general principles related to the architecture of computing systems. A minimal path has been identified, through the exceptionally large set of digital structures, that leads to the generic computing structures that allowed us to achieve the proposed target.

In our opinion, an advantage of the order-based approach is that it roughly follows the historical and structural development of computing systems. We also ground these developments in the theoretical models that provided for their development.

While primarily written for our students, we both have industry experience and have shipped multiple machines in volume. The code we provide bridges the gap between theoretical models and commercial implementations. The code starts very simple using behavioral constructs but progresses towards code that can be used in commercial machines. Over time, it is our hope that these examples continue to expand.

The descriptions of the structures and the simulation of their behavior are written in both SystemVerilog and Chisel HDL. The Chisel code has unit tests and has been tested using Chisel 6.6.0. Some of the SystemVerilog code has unit tests. All SystemVerilog code has been validated using the AMD/Xilinx Vivado Design Suite \footnote{\url{https://www.xilinx.com/content/xilinx/en/support/download.html/}}. It should also execute using online tools such as EDA Playground \footnote{\url{https://www.edaplayground.com}}.

Because this book's code is executable, it should not contain any syntax errors. The same files that are used for design descriptions are directly imported into the book.

This preliminary release we know is quite rough. However, it will become polished with time. The best way to help this effort is to send pull requests to \url{https://github.com/glossner/Structured_Computer_Architecture}. 

John Glossner and Gheorghe M. Ștefan  
January 1st, 2025



---
# Copyright

All text content is copyright © 2025 CC-BY-NC-ND 4.0 by John Glossner and Gheorghe M. Ştefan

Upon our deaths, the text copyright shall convert to © 2025 by John Glossner and Gheorghe M. Ştefan
licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License
(CC BY-NC-SA 4.0)[https://creativecommons.org/licenses/by-nc-sa/4.0/deed.en].

Some portions of this document contain edited text originally generated by ChatGPT.

---

## Code License

### Chisel (Scala) Code License - BSD 3-Clause
Chisel (Scala) code is licensed under the [BSD 3-Clause License](https://opensource.org/licenses/BSD-3-Clause). See the link for details.

### Hand Written Verilog Code License - CERN-OHL-S v2
Hand-written SystemVerilog and Verilog code is licensed under [CERN-OHL-S v2](https://cern.ch/cern-ohl). See the link for details.



