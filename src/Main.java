import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Main {

    public static int evaluateLine(
            String input,
            Map<String, Integer> variables) {
        ArrayList<String> tokens = new Lexer(input).tokenize();
        ASTNode root = new Parser(tokens).parseStatement();
        Semantic.analyze(root, variables);
        return Evaluator.evaluate(root, variables);
    }

    public static void main(String[] args) {
        Map<String, Integer> variables = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        int exitCode = 0;
        System.out.println("Enter expression to evaluate: ");
        int lineNumber = 0;

        while (sc.hasNextLine()) {
            lineNumber++;
            String input = sc.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            try {
                System.out.println(evaluateLine(input, variables));
            } catch (IllegalArgumentException | ArithmeticException e) {
                System.out.println("Error on line " + lineNumber + ": " + e.getMessage());
                exitCode = 1;
            }
        }

        sc.close();

        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
