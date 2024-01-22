package res;

public class ElifOP extends Node{
    public ElifOP(Node expr, Node body) {
        super("elif");
        this.addChild(expr);
        this.addChild(body);
    }
}
