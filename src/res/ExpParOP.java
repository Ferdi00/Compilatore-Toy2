package res;

public class ExpParOP extends Node{
    public ExpParOP(Node n1) {
        super("()");
        this.addChild(n1);
    }

}
