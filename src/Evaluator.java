import java.util.Map;
import java.util.HashMap;

public class Evaluator {
    private static boolean isIdentifier(String value) {
        return value.matches("[A-Za-z_][A-Za-z0-9_]*");
    }
    public static int evaluate(ASTNode root) {
        return evaluate(root, new HashMap<>());
    }
    public static int evaluate(ASTNode root, Map<String, Integer> variables) {
        if (root == null) {
            throw new IllegalArgumentException("Invalid AST: node is null");
        }

        if (root.value.equals("*")) {
            return evaluate(root.left, variables) * evaluate(root.right, variables);
        } else if (root.value.equals("/")){
            int divisor = evaluate(root.right, variables);
            if (divisor == 0) {
                throw new ArithmeticException("Division by zero");
            }
            return evaluate(root.left, variables) / divisor;
        } else if (root.value.equals("+")) {
            return evaluate(root.left, variables) + evaluate(root.right, variables);
        } else if (root.value.equals("-")) {
            return evaluate(root.left, variables) - evaluate(root.right, variables);
        } else if(root.value.equals("NEG")) {
            return -evaluate(root.left, variables);
        } else if (root.value.equals("^")) {
            int base = evaluate(root.left, variables);
            int exponent = evaluate(root.right, variables);

            if (exponent < 0) {
                throw new ArithmeticException(
                        "Negative exponents are not supported for integer evaluation");
            }

            int result = 1;
            for (int i = 0; i < exponent; i++) {
                result *= base;
            }
            return result;
        } else if (root.value.matches("\\d+")) {
            return Integer.parseInt(root.value);
        }
        else if (isIdentifier(root.value)) {
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
