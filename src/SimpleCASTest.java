import java.util.HashMap;
import java.util.Map;

public class SimpleCASTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testValue("12+3*4", 24);
        testValue("(12+3)*4", 60);
        testValue("8-3-2", 3);
        testValue("20/5/2", 2);
        testValue("-2*3", -6);
        testValue("2*-3", -6);
        testValue("-(2+3)", -5);
        testValue("2--3", 5);
        testValue("2^3", 8);
        testValue("2^3^2", 512);
        testValue("2^3*4", 32);
        testValue("-2^2", -4);
        testValue("(-2)^2", 4);

        Map<String, Integer> variables = new HashMap<>();
        variables.put("x", 5);
        variables.put("y", 2);
        testVariableValue("x+3", variables, 8);
        testVariableValue("2*x", variables, 10);
        testVariableValue("x^2+y", variables, 27);
        testVariableValue("x-y*3", variables, -1);
        testAssignment("x=5", variables, 5);
        testAssignment("y=x^2+1", variables, 26);
        testVariableValue("y", variables, 26);

        Map<String, Integer> pipelineVariables = new HashMap<>();
        testPipeline("x=5", pipelineVariables, 5);
        testPipeline("x+3", pipelineVariables, 8);
        testPipeline("y=x^2+1", pipelineVariables, 26);
        testPipeline("y", pipelineVariables, 26);
        testSemanticAccepted("x+1", pipelineVariables);
        testSemanticRejected("z+1", pipelineVariables,
                "Undefined variable: z");
        testPipelineRejected("z=missing+1", pipelineVariables,
                "Undefined variable: missing");
        check(!pipelineVariables.containsKey("z"),
                "failed assignment does not update the symbol table");

        testRejected("10/0", "Division by zero");
        testRejected("2+", "Unexpected Token: EOF");
        testRejected("()", "Expected RPAREN, found EOF");
        testRejected("2--", "Unexpected Token: EOF");
        testRejected("12a+3", "Unexpected Token: a");
        testRejected("2^-3",
                "Negative exponents are not supported for integer evaluation");
        testUndefinedVariable("z+1", variables);

        testAst("8-3-2", "-", "-", "8", "3", "2");
        testUnaryAst("2*-3");
        testPowerAst("2^3^2");

        System.out.println("Tests passed: " + passed);
        System.out.println("Tests failed: " + failed);

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testValue(String expression, int expected) {
        try {
            ASTNode root = parse(expression);
            int actual = Evaluator.evaluate(root);
            check(actual == expected,
                    expression + " expected " + expected + ", got " + actual);
        } catch (RuntimeException e) {
            fail(expression + " unexpectedly failed: " + e.getMessage());
        }
    }

    private static void testRejected(String expression, String expectedMessage) {
        try {
            Evaluator.evaluate(parse(expression));
            fail(expression + " should have been rejected");
        } catch (RuntimeException e) {
            check(e.getMessage().equals(expectedMessage),
                    expression + " expected error '" + expectedMessage
                            + "', got '" + e.getMessage() + "'");
        }
    }

    private static void testVariableValue(String expression,
                                          Map<String, Integer> variables,
                                          int expected) {
        try {
            ASTNode root = parse(expression);
            int actual = Evaluator.evaluate(root, variables);
            check(actual == expected,
                    expression + " expected " + expected + ", got " + actual);
        } catch (RuntimeException e) {
            fail(expression + " unexpectedly failed: " + e.getMessage());
        }
    }

    private static void testUndefinedVariable(String expression,
                                               Map<String, Integer> variables) {
        try {
            Evaluator.evaluate(parse(expression), variables);
            fail(expression + " should have been rejected");
        } catch (RuntimeException e) {
            check(e.getMessage().equals("Undefined variable: z"),
                    expression + " reports an undefined variable");
        }
    }

    private static void testAssignment(String expression,
                                       Map<String, Integer> variables,
                                       int expected) {
        try {
            ASTNode root = new Parser(new Lexer(expression).tokenize()).parseStatement();
            int actual = Evaluator.evaluate(root, variables);
            check(actual == expected,
                    expression + " expected " + expected + ", got " + actual);
        } catch (RuntimeException e) {
            fail(expression + " unexpectedly failed: " + e.getMessage());
        }
    }

    private static void testPipeline(String expression,
                                     Map<String, Integer> variables,
                                     int expected) {
        try {
            int actual = Main.evaluateLine(expression, variables);
            check(actual == expected,
                    expression + " full pipeline expected " + expected
                            + ", got " + actual);
        } catch (RuntimeException e) {
            fail(expression + " unexpectedly failed in full pipeline: "
                    + e.getMessage());
        }
    }

    private static void testPipelineRejected(String expression,
                                             Map<String, Integer> variables,
                                             String expectedMessage) {
        try {
            Main.evaluateLine(expression, variables);
            fail(expression + " should have been rejected by the full pipeline");
        } catch (RuntimeException e) {
            check(e.getMessage().equals(expectedMessage),
                    expression + " produced the expected pipeline error");
        }
    }

    private static void testSemanticAccepted(String expression,
                                             Map<String, Integer> variables) {
        try {
            Semantic.analyze(parseStatement(expression), variables);
            check(true, expression + " passed semantic analysis");
        } catch (RuntimeException e) {
            fail(expression + " unexpectedly failed semantic analysis: "
                    + e.getMessage());
        }
    }

    private static void testSemanticRejected(String expression,
                                             Map<String, Integer> variables,
                                             String expectedMessage) {
        try {
            Semantic.analyze(parseStatement(expression), variables);
            fail(expression + " should have failed semantic analysis");
        } catch (RuntimeException e) {
            check(e.getMessage().equals(expectedMessage),
                    expression + " produced the expected semantic error");
        }
    }

    private static void testAst(String expression, String rootValue,
                                String leftValue, String leftLeftValue,
                                String leftRightValue, String rightValue) {
        try {
            ASTNode root = parse(expression);
            check(root.value.equals(rootValue)
                            && root.left.value.equals(leftValue)
                            && root.left.left.value.equals(leftLeftValue)
                            && root.left.right.value.equals(leftRightValue)
                            && root.right.value.equals(rightValue),
                    expression + " has the expected left-associative AST");
        } catch (RuntimeException e) {
            fail(expression + " AST inspection failed: " + e.getMessage());
        }
    }

    private static void testUnaryAst(String expression) {
        try {
            ASTNode root = parse(expression);
            check(root.value.equals("*")
                            && root.left.value.equals("2")
                            && root.right.value.equals("NEG")
                            && root.right.left.value.equals("3"),
                    expression + " has the expected NEG AST node");
        } catch (RuntimeException e) {
            fail(expression + " AST inspection failed: " + e.getMessage());
        }
    }

    private static void testPowerAst(String expression) {
        try {
            ASTNode root = parse(expression);
            check(root.value.equals("^")
                            && root.left.value.equals("2")
                            && root.right.value.equals("^")
                            && root.right.left.value.equals("3")
                            && root.right.right.value.equals("2"),
                    expression + " has the expected right-associative AST");
        } catch (RuntimeException e) {
            fail(expression + " AST inspection failed: " + e.getMessage());
        }
    }

    private static ASTNode parse(String expression) {
        return new Parser(new Lexer(expression).tokenize()).parse();
    }

    private static ASTNode parseStatement(String expression) {
        return new Parser(new Lexer(expression).tokenize()).parseStatement();
    }

    private static void check(boolean condition, String message) {
        if (condition) {
            passed++;
            System.out.println("PASS: " + message);
        } else {
            fail(message);
        }
    }

    private static void fail(String message) {
        failed++;
        System.out.println("FAIL: " + message);
    }
}
