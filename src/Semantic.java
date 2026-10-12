import java.util.Map;

public class Semantic {
    public static void analyze(ASTNode root, Map<String, Integer> variables) {
        if (root == null) {
            throw new IllegalArgumentException(
                    "Cannot analyze a null AST"
            );
        }

        if (root.value.equals("=")) {
            analyzeAssignment(root, variables);
            return;
        }

        if (root.value.equals("NEG")) {
            analyze(root.left, variables);
            return;
        }

        if (LanguageRules.isBinaryOperator(root.value)) {
            analyze(root.left, variables);
            analyze(root.right, variables);
            return;
        }

        if (LanguageRules.isNumber(root.value)) {
            return;
        }

        if (LanguageRules.isIdentifier(root.value)) {
            if (!variables.containsKey(root.value)) {
                throw new IllegalArgumentException(
                        "Undefined variable: " + root.value
                );
            }
            return;
        }

        throw new IllegalArgumentException(
                "Invalid AST node: " + root.value
        );
    }
    private static void analyzeAssignment(
            ASTNode root,
            Map<String, Integer> variables) {

        if (root.left == null
                || !LanguageRules.isIdentifier(root.left.value)) {
            throw new IllegalArgumentException(
                    "Invalid assignment target"
            );
        }

        // Analyze the right side before defining the new variable.
        analyze(root.right, variables);
    }

}
