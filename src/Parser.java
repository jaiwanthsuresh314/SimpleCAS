import java.util.ArrayList;
public class Parser {
    private final ArrayList<String> tokens;
    private int pos = 0;

    public Parser(ArrayList<String> tokens) {
        this.tokens = tokens;
    }

    private String currToken() {
        if(pos >= tokens.size()) {
            return "EOF";
        }
        return tokens.get(pos);
    }

    private void match(String expected) {
        if(currToken().equals(expected)) {
            pos++;
        }
        else {
            throw new IllegalArgumentException("Expected " + expected + ", found " + currToken());
        }
    }

    private String peekToken() {
        if (pos + 1 >= tokens.size()) {
            return "EOF";
        }
        return tokens.get(pos + 1);
    }

    private ASTNode Statement() {
        if(LanguageRules.isIdentifier(currToken()) && peekToken().equals("ASSIGN")) {
            String id = currToken();
            pos++;
            match("ASSIGN");
            return new ASTNode("=", new ASTNode(id), E());
        }
        return E();
    }

    private ASTNode E() {
        ASTNode left = T();
        while(currToken().equals("ADD") || currToken().equals("SUB")) {
            if(currToken().equals("ADD")) {
                match("ADD");
                ASTNode right = T();

                left = new ASTNode("+", left, right);
            }
            else if(currToken().equals("SUB")) {
                match("SUB");
                ASTNode right = T();

                left = new ASTNode("-", left, right);
            }
        }

        return left;
    }

    private ASTNode T() {
        ASTNode left = F();
        while(currToken().equals("MUL") || currToken().equals("DIV")) {
            if(currToken().equals("MUL")) {
                match("MUL");
                ASTNode right = F();

                left = new ASTNode("*", left, right);
            }
            else if(currToken().equals("DIV")) {
                match("DIV");
                ASTNode right = F();

                left = new ASTNode("/", left, right);
            }
        }
        return left;
    }

    private ASTNode F() {
        if(currToken().equals("SUB")) {
            match("SUB");
            ASTNode node = F();
            return new ASTNode("NEG",node, null);
        }
        ASTNode node = P();

        return node;
    }

    private ASTNode P() {
        ASTNode left = Primary();

        if(currToken().equals("POW")) {
            match("POW");
            ASTNode right = F();
            return new ASTNode("^", left, right);
        }
        return left;
    }

    private ASTNode Primary() {
        if(currToken().equals("LPAREN")) {
            match("LPAREN");
            ASTNode node = E();
            match("RPAREN");
            return node;
        } else if(LanguageRules.isNumber(currToken())) {
            String number = currToken();
            pos++;
            return new ASTNode(number);
        } else if(LanguageRules.isIdentifier(currToken())) {
            String id = currToken();
            pos++;
            return new ASTNode(id);
        } else {
            throw new IllegalArgumentException("Unexpected Token: " + currToken());
        }
    }
    public ASTNode parse() {
        pos = 0;
        ASTNode root = E();

        if(pos != tokens.size()) {
            throw new IllegalArgumentException("Unexpected Token: " + currToken());
        }
        return root;
    }

    public ASTNode parseStatement() {
        pos = 0;
        ASTNode root = Statement();

        if(pos != tokens.size()) {
            throw new IllegalArgumentException("Unexpected Token: " + currToken());
        }
        return root;
    }
}
