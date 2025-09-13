# LaTeX Beamer Slides for Structured Computer Architecture

This directory contains LaTeX Beamer presentations for each chapter of the "Structured Computer Architecture: An Order-Based Approach" textbook.

## Overview

The slides are designed to be:
- **Print-friendly**: Simple color scheme with high contrast
- **Educational**: Clear structure with learning objectives and summaries
- **Comprehensive**: Detailed theoretical content based on the textbook
- **Professional**: Suitable for academic instruction

## Current Status

### Completed Slides
- **Chapter 1**: `chapter1_slides.tex` - Number Systems

### Planned Slides
- Chapter 2: Formal Notation
- Chapter 3: Unsigned Integers
- Chapter 4: Signed Integers
- Chapter 5: Floating Point
- Chapter 6: Character Encodings
- Chapter 7: Part 1 Summary
- Chapters 8-17: Computer Architecture Topics

## Usage Instructions

### Compilation
```bash
pdflatex chapter1_slides.tex
```

### For Overleaf
1. Upload the `.tex` file to Overleaf
2. Set the compiler to `pdfLaTeX`
3. Compile to generate PDF slides

### Presentation Mode
- Use PDF viewer in full-screen mode
- Navigate with arrow keys or page up/down
- Print slides for handouts (optimized for black & white)

## Design Philosophy

### Simple Template
- Minimal colors for printing compatibility
- Clean typography using standard fonts
- No complex animations or transitions
- Focus on content over visual effects

### Educational Structure
Each chapter follows this structure:
1. **Title Slide**: Chapter name and authors
2. **Outline**: Table of contents
3. **Learning Objectives**: Clear goals for students
4. **Content Sections**: Detailed theoretical concepts
5. **Summary**: Key takeaways
6. **Questions**: Discussion prompts

### Theoretical Focus
- Emphasis on concepts over implementation
- Mathematical foundations clearly explained
- Historical context provided where relevant
- Minimal code examples (only when essential)

## Chapter 1: Number Systems

### Content Coverage
- Historical evolution of number systems
- Mathematical set development (ℕ, ℤ, ℚ, ℝ, ℂ)
- Pre-computer number representations
- Decimal positional systems
- Complement arithmetic (10's complement)
- Logarithmic representations
- Binary number systems
- Two's complement representation
- Excess-k (biased) systems
- Fixed-point arithmetic
- Floating-point systems (IEEE 754)
- Binary Coded Decimal (BCD)

### Key Features
- 30+ slides with comprehensive coverage
- Mathematical examples and formulas
- Historical context and development
- Clear explanations of theoretical concepts
- Suitable for 50-minute lecture

## Contributing

When adding new chapter slides:
1. Follow the same template structure
2. Use consistent naming: `chapterN_slides.tex`
3. Include learning objectives and summary
4. Focus on theoretical concepts
5. Keep design simple and print-friendly
6. Test compilation before submitting

## Technical Requirements

### LaTeX Packages Required
- `beamer` (presentation class)
- `amsmath`, `amsfonts`, `amssymb` (mathematics)
- `graphicx` (images)
- `tikz` (diagrams)
- `booktabs` (tables)
- `listings` (code, if needed)

### Recommended Setup
- TeX Live 2023 or later
- Overleaf (online compilation)
- Any PDF viewer for presentation

## License

These slides are based on the textbook "Structured Computer Architecture: An Order-Based Approach" by John Glossner and Gheorghe M. Ştefan and follow the same licensing terms as the main textbook.

