# WOOL_Compiler

Compiler for the custom WOOL programming language. The exact details of the WOOL language can be found in "wool-manual.pdf".

## Approach

## Lexical and Syntactic Analysis

Using ANTLR a .g4 file was created that first separates text out into valid tokens for the lexical analysis. Next, ANTLR uses a second set of rules to create a parse tree of syntactically valid programs for WOOL.

## Semantic Analysis

Custom ANTLR visitors were used to walk the parse trees created in syntactic analysis. The visitors were use to:
1. Create a symbol table for the program
2. Check symbol references against the symbol table 
3. Perform type checking on the WOOL operations

## Code Generation

Here another custom ANTLR visitor was used to walk through the parse tree of the now valid WOOL program and ASM was used to generate the Java bytecode intended to perform each WOOL operation.

## Usage
### Creating Woolc.jar

In the `wool/compile` package, use Eclipse's export feature to export Woolc.java as a runnable jar file.

### Using Woolc

`woolc <fileName>.wl`: Compiles the WOOL code into .class files and places them into a /wool directory.

### Running the Programs

`java <fileName>`: Run the program the same as any other Java program post-compile.

### Dependencies and Other Software

- Junit 5
- Java 15.02
- ANTLR 4.9
- ASM 9.1
- WOOL standard library ("Wool.jar")
- Eclipse IDE
