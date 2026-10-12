public class Evaluator {
    public static int evaluate(ASTNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Invalid AST: node is null");
        }

        if (root.value.equals("*")) {
            return evaluate(root.left) * evaluate(root.right);
        } else if (root.value.equals("/")){
            int divisor = evaluate(root.right);
            if (divisor == 0) {
                throw new ArithmeticException("Division by zero");
            }
            return evaluate(root.left) / divisor;
        } else if (root.value.equals("+")) {
            return evaluate(root.left) + evaluate(root.right);
        } else if (root.value.equals("-")) {
            return evaluate(root.left) - evaluate(root.right);
        } else if(root.value.equals("NEG")) {
            return -evaluate(root.left);
        } else if (root.value.equals("^")) {
            int base = evaluate(root.left);
            int exponent = evaluate(root.right);

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
        throw new IllegalArgumentException("Invalid AST node: " + root.value);
    }
}
