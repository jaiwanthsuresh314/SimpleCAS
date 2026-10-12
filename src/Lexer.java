import java.util.ArrayList;
public class Lexer {
    public static ArrayList<String> tokenize(String input) {
        ArrayList<String> tokens = new ArrayList<>();
        String curr = ""; boolean isNumber = false;
        for(int i = 0; i < input.length(); i++) {
            if(Character.isDigit(input.charAt(i)) && !isNumber) {
                isNumber = true;
                curr += input.charAt(i);
                continue;
            }
            if(isNumber) {
                if(Character.isDigit(input.charAt(i))) {
                    curr += input.charAt(i);
                    continue;
                }
                else {
                    tokens.add(curr);
                    isNumber = false;
                    curr = "";
                }
            }
            if(input.charAt(i) == '+') {
                tokens.add("ADD");
            }
            else if(input.charAt(i) == '-') {
                tokens.add("SUB");
            }
            else if(input.charAt(i) == '*') {
                tokens.add("MUL");
            }
            else if(input.charAt(i) == '/') {
                tokens.add("DIV");
            }
            else if(input.charAt(i) == '(') {
                tokens.add("LPAREN");
            }
            else if(input.charAt(i) == ')') {
                tokens.add("RPAREN");
            }
            else if(input.charAt(i) == '^') {
                tokens.add("POW");
            }
            else if(Character.isWhitespace(input.charAt(i))){
                curr = "";
                continue;
            }
            else {
                throw new IllegalArgumentException("Invalid character: " + input.charAt(i));
            }
        }
        if(!curr.isEmpty()) {
            tokens.add(curr);
        }
        return tokens;
    }
}
