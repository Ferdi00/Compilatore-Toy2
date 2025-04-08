package nodes;

public class WhileOP extends Node{
    public WhileOP(Node expr, Node body) {
        super("WhileOP");
        this.addChild(expr);
        this.addChild(body);
    }
}
