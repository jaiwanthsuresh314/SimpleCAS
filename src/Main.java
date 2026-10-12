import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Main {


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
                ArrayList<String> tokens = new Lexer(input).tokenize();
                ASTNode root = new Parser(tokens).parseStatement();
                Semantic.analyze(root, variables);
                System.out.println(Evaluator.evaluate(root, variables));
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

    public static void traverse(ASTNode root) {
        if(root != null) {
            traverse(root.left);
            System.out.print(root.value);
            traverse(root.right);
        }
    }

    public static void levelOrder(ASTNode root) {
        if (root == null) return;

        Queue<ASTNode> q = new LinkedList<>();
        q.add(root);

        while (!q.isEmpty()) {
            ASTNode curr = q.poll();
            System.out.print(curr.value + " ");

            if (curr.left != null) {
                q.add(curr.left);
            }
            if (curr.right != null) {
                q.add(curr.right);
            }
        }
    }
}
