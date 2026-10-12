import java.util.ArrayList;
public class Lexer {
    private int pos = 0;
    private final String input;
    Lexer(String input) {
        this.input = input;
        this.pos = 0;
    }
    private String readNumber()
    {
        StringBuilder res = new StringBuilder();

        while(pos < input.length() && Character.isDigit(input.charAt(pos))) {
            res.append(input.charAt(pos));
            pos++;
        }
        return res.toString();
    }
    private String readIdentifier() {
        StringBuilder res = new StringBuilder();

        if (pos < input.length()
                && (Character.isLetter(input.charAt(pos))
                || input.charAt(pos) == '_')) {
            res.append(input.charAt(pos));
            pos++;
        }

        while (pos < input.length()
                && (Character.isLetterOrDigit(input.charAt(pos))
                || input.charAt(pos) == '_')) {
            res.append(input.charAt(pos));
            pos++;
        }

        return res.toString();
    }
    private String classifySymbol() {
        if(input.charAt(pos) == '+') {
            return "ADD";
        } else if(input.charAt(pos) == '-') {
            return "SUB";
        } else if(input.charAt(pos) == '*') {
            return "MUL";
        } else if(input.charAt(pos) == '/') {
            return "DIV";
        } else if(input.charAt(pos) == '^') {
            return "POW";
        } else if(input.charAt(pos) == '(') {
            return "LPAREN";
        } else if(input.charAt(pos) == ')') {
            return "RPAREN";
        } else {
            throw new IllegalArgumentException("Invalid character: " + input.charAt(pos));
        }
    }
    public ArrayList<String> tokenize() {
        ArrayList<String> tokens = new ArrayList<>();
        while(pos < input.length()) {
            char curr = input.charAt(pos);

            if(Character.isWhitespace(curr)) {
                pos++;
            } else if(Character.isDigit(curr)) {
                tokens.add(readNumber());
            } else if(Character.isLetter(curr) || input.charAt(pos) == '_') {
                tokens.add(readIdentifier());
            } else {
                tokens.add(classifySymbol());
                pos++;
            }
        }


        return tokens;
    }
}
