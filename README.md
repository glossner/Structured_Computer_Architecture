> **Question:** *Can we have a procedure that, receiving a program and the data it processes, decides if the program will stop executing in a finite time?*  

This (halting) problem was formulated in computational terms in 1936 by Alonzo Church, Stephen Kleene, Emil Post, and Alan Turing as a reaction to the incompleteness theorem published by Kurt Gödel in 1931.

> **Answer**: *No!*

And thus, the most important negative result in the history of mathematics underpins the science of computation.


---
# Preface to Preliminary Release v0.1

This textbook arose from an introductory course on Computer Architecture taught at Rivier University in the Fall of 2024. It draws on Gheorghe M. Ștefan's years of teaching Computer Architecture at the University of Bucharest.

Unlike other Computer Architecture textbooks, this book approaches the subject from a Computer Science theoretical perspective. Using an ordered approach, we start with the most basic 0-order computing system—one with stateless operations. We then describe computing systems in terms of their feedback loop orders progressing order-by-order. Table 1 below shows the progression of computing systems' feedback loops.

## Orders of Computing Systems

| **Order**    | **Definition**                                                | **Components**                                      | **Example**                                   |
|--------------|----------------------------------------------------------------|----------------------------------------------------|-----------------------------------------------|
| **0-Order**  | No loops. Stateless systems where output is determined by current input only. | Logic gates (AND, OR, NOT).                        | Simple combinational circuits, e.g., Multiplexer, adders, ALU. |
| **1st-Order**| One loop. Memory Circuits                                      | Flip-flops, latches, registers, RAM, SRAM, ROM.    | D flip-flop.                                  |
| **2nd-Order**| Two loops. Automata.                                           | Mealy, Moore automaton                             | Counters, FSMs, ALUs with registers.          |
| **3rd-Order**| Three loops. Processing units executing instructions.          | ALU, control units, registers.                    | Simple microprocessor, e.g., CPU.             |
| **4th-Order**| Four loops. Processor plus one Memory                          | Program and Data in single memory.                | von Neumann abstract machine.                 |
| **5th-Order**| Five loops. Processor with two memories.                       | Separate Program and Data memories.               | Harvard abstract machine.                     |
| **nth-Order**| N loops. Multi-processor systems.                              | SCAN and REDUCE                                    | TBD                                           |

*Table 1: Orders of Computing Systems*

Part I of this book provides a mathematical foundation for the number systems used in computing systems. Part II covers orders 0 through 3—basic combinational logic through a processor. Part III covers the theory and implementation of computing, including parallel heterogeneous systems.

In our opinion, an advantage of the order-based approach is that it roughly follows the historical development of computing systems. We also ground these developments in the theoretical models that provided for their development.

While primarily written for our students, we both have industry experience and have shipped multiple machines in volume. The code we provide bridges the gap between theoretical models and commercial implementations. The code starts very simple using behavioral constructs but progresses towards code that can be used in commercial machines. Over time, it is our hope that these examples continue to expand.

The descriptions of the structures and the simulation of their behavior are written in both SystemVerilog and Chisel HDL. The Chisel code has unit tests and has been tested using Chisel 6.6.0. Some of the SystemVerilog code has unit tests. All SystemVerilog code has been validated using the AMD/Xilinx Vivado Design Suite ([Download Vivado Design Suite](https://www.xilinx.com/content/xilinx/en/support/download.html/)). It should also execute using online tools such as [EDA Playground](https://www.edaplayground.com).

Because this book's code is executable, it should not contain any syntax errors. The same files that are used for design descriptions are directly imported into the book.

This preliminary release we know is quite rough. However, it will become polished with time. The best way to help this effort is to send pull requests to [https://github.com/glossner/Structured_Computer_Architecture](https://github.com/glossner/Structured_Computer_Architecture).


John Glossner and Gheorghe M. Ștefan  
January 1st, 2025



---
# Copyright

Part I: Number Systems and Representations is copyright © 2025 by John Glossner and Gheorghe M. Ștefan and licensed under a Creative Commons Attribution 4.0 International License (CC BY).  

Parts II through IV and all other content is copyright © 2025 by John Glossner and Gheorghe M. Ștefan and licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License (CC BY-NC-ND 4.0).

Some portions of this document contain edited text originally generated by ChatGPT, primarily Part I and the code appendices.



---

## Code License

### Chisel (Scala) Code License - BSD 3-Clause
Chisel (Scala) code is licensed under the [BSD 3-Clause License](https://opensource.org/licenses/BSD-3-Clause). See the link for details.

### Hand Written Verilog Code License - CERN-OHL-S v2
Hand-written SystemVerilog and Verilog code is licensed under [CERN-OHL-S v2](https://cern.ch/cern-ohl). See the link for details.



