public class ASTNode {
    String value;
    ASTNode left, right;

    public ASTNode(String value) {
        this.value = value;
        left = right = null;
    }

    public ASTNode(String value, ASTNode left, ASTNode right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }
}
