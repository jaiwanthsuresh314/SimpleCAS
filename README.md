# SimpleCAS Project Context

## Goal

Build a small Computer Algebra System (CAS) in Java, incrementally, while learning compiler design concepts.

## Current architecture

- `Main.java` reads one expression from standard input, coordinates the pipeline, and contains inorder and level-order AST traversal helpers.
- `SimpleCASTest.java` is a dependency-free regression test program for values, errors, and selected AST shapes.
- `Lexer.java` tokenizes integer arithmetic expressions.
- `Parser.java` uses recursive descent to construct an AST.
- `ASTNode.java` represents expression-tree nodes.
- `Evaluator.java` recursively evaluates the AST.
- `Semantic.java` is currently an empty placeholder and is not used.

The pipeline is:

```text
input text -> Lexer -> tokens -> Parser -> AST -> Evaluator -> int result
```

## Current supported grammar

The implementation currently supports integer literals, parentheses, addition, subtraction, multiplication, division, unary minus, and exponentiation:

```text
E       -> T ((ADD | SUB) T)*
T       -> F ((MUL | DIV) F)*
F       -> SUB F | P
P       -> Primary (POW F)?
Primary -> ( E ) | N
N       -> one or more decimal digits
```

This structure gives exponentiation higher precedence than multiplication and division, which are higher precedence than addition and subtraction. The loops in `E()` and `T()` make the binary operator groups left-associative. The recursive `P()`/`F()` relationship makes exponentiation right-associative, so `2^3^2` means `2^(3^2)`. Unary minus is handled in `F()`.

Examples:

```text
8-3-2    -> (8-3)-2 -> 3
20/5/2   -> (20/5)/2 -> 2
10-2*3   -> 10-(2*3) -> 4
-2*3     -> (-2)*3 -> -6
2^3^2    -> 2^(3^2) -> 512
-2^2     -> -(2^2) -> -4
```

The lexer emits multi-digit integers as single tokens and uses these operator and delimiter tokens:

```text
ADD, SUB, MUL, DIV, POW, LPAREN, RPAREN
```

## Current implementation

- `Lexer.tokenize()` groups consecutive digits, recognizes whitespace, operators, and parentheses, and rejects unknown characters with `Invalid character: ...`.
- `Parser.E()` parses addition and subtraction.
- `Parser.T()` parses multiplication and division.
- `Parser.F()` parses unary minus and delegates ordinary factors to `P()`.
- `Parser.P()` parses exponentiation and constructs `^` nodes. Its recursive right operand gives exponentiation right associativity.
- `Parser.Primary()` parses parenthesized expressions and integer literals.
- `Parser.parse()` rejects leftover tokens after the expression and returns the AST root on success.
- `ASTNode` stores a `value`, `left`, and `right`. Binary operator nodes store `+`, `-`, `*`, or `/`; unary-minus nodes store `NEG` and use their left child.
- `Evaluator.evaluate()` returns an `int` and handles all five binary operators plus `NEG`.
- Exponentiation uses repeated integer multiplication. Negative exponents are rejected because the evaluator currently represents only integer results.
- Division is integer division because the evaluator returns `int`. For example, `10/3` evaluates to `3`.
- Division by zero is explicitly rejected with `ArithmeticException("Division by zero")`.
- AST traversal helpers remain in `Main.java`.

## Compilation and execution

From the project root in PowerShell:

```powershell
New-Item -ItemType Directory -Force out
javac -d out src\*.java
java -cp out Main
```

To run the regression tests:

```powershell
javac -d out src\*.java
java -cp out SimpleCASTest
```

The test command should finish with `Tests failed: 0`.

`Main.java` prompts for an expression and reads one line from standard input. For example:

```powershell
"-(2+3)" | java -cp out Main
```

## Verified expressions

The following expressions were compiled and executed successfully using the current source:

| Expression | Result |
| --- | ---: |
| `12+3*4` | `24` |
| `(12+3)*4` | `60` |
| `8-3-2` | `3` |
| `10-2*3` | `4` |
| `2+3-4` | `1` |
| `20/5*2` | `8` |
| `20/5/2` | `2` |
| `-5` | `-5` |
| `2*-3` | `-6` |
| `-(2+3)` | `-5` |
| `-2*3` | `-6` |
| `2--3` | `5` |
| `2^3` | `8` |
| `2^3^2` | `512` |
| `2^3*4` | `32` |
| `-2^2` | `-4` |
| `(-2)^2` | `4` |

The following malformed or unsafe expressions were rejected as expected:

| Expression | Actual message |
| --- | --- |
| `10/0` | `Division by zero` |
| `10/(3-3)` | `Division by zero` |
| `2+` | `Unexpected Token: EOF` |
| `()` | `Unexpected Token: RPAREN` |
| `2--` | `Unexpected Token: EOF` |
| `12a+3` | `Invalid character: a` |
| `2^-3` | `Negative exponents are not supported for integer evaluation` |

## Known limitations

- `Main.java` reads one input line, reports lexer/parser/evaluator errors as `Error: ...`, returns exit code `1` on failure, and does not provide a loop for evaluating multiple expressions.
- `SimpleCASTest` can be run with `java -cp out SimpleCASTest`; it exits with code `1` if any check fails.
- There is no external test framework or build configuration; `SimpleCASTest` is a dependency-free regression harness.
- Division uses Java integer-division semantics; decimal values are not supported.
- Negative exponents, variables, symbolic simplification, and symbolic differentiation are not implemented.
- The parser and evaluator use unchecked exceptions for invalid input and evaluation errors; Main currently catches and formats them for the user.
- `ASTNode` fields have package-private visibility and no accessor methods.

## Recommended next direction

The current test harness verifies precedence, associativity, parentheses, unary minus, exponentiation, malformed input, division by zero, and selected AST shapes. The next focus should be extending those tests whenever the grammar changes, then deciding whether integer overflow should be detected during arithmetic.

## Design decisions

- Keep the code beginner-friendly and explain changes step by step.
- Keep Lexer, Parser, ASTNode, and Evaluator as separate components.
- Continue using recursive descent while the grammar remains small.
- Use `.equals()` for Java string comparisons.
- Reject leftover tokens after parsing.
- Do not silently skip invalid characters in the lexer.
- `Integer.parseInt()` is acceptable for converting integer literal tokens.
- Preserve the existing architecture and make small, deliberate changes.

## How to collaborate

Explain the reasoning behind each change. Avoid rewriting the project unnecessarily. Preserve the existing architecture where possible, point out bugs, and suggest tests for valid and invalid expressions. Do not add features without explaining their purpose.

## Git workflow

From the project root:

```powershell
git status
git add .
git commit -m "Build integer expression evaluator"
git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPOSITORY.git
git branch -M main
git push -u origin main
```

Review `git status` and `git diff --cached` before committing. Do not commit compiled output, IDE metadata, or generated render directories; they are excluded by `.gitignore`.
