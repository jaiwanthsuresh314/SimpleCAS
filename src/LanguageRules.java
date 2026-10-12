public final class LanguageRules {
    private LanguageRules() {
    }

    public static boolean isIdentifier(String value) {
        return value.matches("[A-Za-z_][A-Za-z0-9_]*");
    }

    public static boolean isNumber(String value) {
        return value.matches("\\d+");
    }

    public static boolean isBinaryOperator(String value) {
        return value.equals("+")
                || value.equals("-")
                || value.equals("*")
                || value.equals("/")
                || value.equals("^");
    }
}
