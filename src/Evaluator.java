import java.util.Map;
import java.util.HashMap;

public class Evaluator {
    private static int add(int left, int right) {
        try {
            return Math.addExact(left, right);
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Integer overflow");
        }
    }

    private static int subtract(int left, int right) {
        try {
            return Math.subtractExact(left, right);
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Integer overflow");
        }
    }

    private static int multiply(int left, int right) {
        try {
            return Math.multiplyExact(left, right);
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Integer overflow");
        }
    }

    private static int negate(int value) {
        try {
            return Math.negateExact(value);
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Integer overflow");
        }
    }

    private static int divide(int dividend, int divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        if (dividend == Integer.MIN_VALUE && divisor == -1) {
            throw new ArithmeticException("Integer overflow");
        }
        return dividend / divisor;
    }

    private static int power(int base, int exponent) {
        if (exponent < 0) {
            throw new ArithmeticException(
                    "Negative exponents are not supported for integer evaluation");
        }

        int result = 1;
        int factor = base;

        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result = multiply(result, factor);
            }

            exponent >>= 1;

            if (exponent > 0) {
                factor = multiply(factor, factor);
            }
        }

        return result;
    }

    private static int parseNumber(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ArithmeticException("Integer overflow");
        }
    }

    public static int evaluate(ASTNode root) {
        return evaluate(root, new HashMap<>());
    }
    public static int evaluate(ASTNode root, Map<String, Integer> variables) {
        if (root == null) {
            throw new IllegalArgumentException("Invalid AST: node is null");
        }

        if (root.value.equals("*")) {
            return multiply(
                    evaluate(root.left, variables),
                    evaluate(root.right, variables));
        } else if (root.value.equals("/")){
            int dividend = evaluate(root.left, variables);
            int divisor = evaluate(root.right, variables);
            return divide(dividend, divisor);
        } else if (root.value.equals("+")) {
            return add(
                    evaluate(root.left, variables),
                    evaluate(root.right, variables));
        } else if (root.value.equals("-")) {
            return subtract(
                    evaluate(root.left, variables),
                    evaluate(root.right, variables));
        } else if(root.value.equals("NEG")) {
            return negate(evaluate(root.left, variables));
        } else if (root.value.equals("^")) {
            int base = evaluate(root.left, variables);
            int exponent = evaluate(root.right, variables);
            return power(base, exponent);
        } else if (root.value.equals("=")) {
            if (root.left == null || !LanguageRules.isIdentifier(root.left.value)) {
                throw new IllegalArgumentException(
                        "Invalid assignment target: " + root.left);
            }

            int value = evaluate(root.right, variables);
            variables.put(root.left.value, value);
            return value;
        } else if (LanguageRules.isNumber(root.value)) {
            return parseNumber(root.value);
        }
        else if (LanguageRules.isIdentifier(root.value)) {
            if (!variables.containsKey(root.value)) {
                throw new IllegalArgumentException(
                        "Undefined variable: " + root.value
                );
            }

            return variables.get(root.value);
        }
        throw new IllegalArgumentException("Invalid AST node: " + root.value);
    }
}
