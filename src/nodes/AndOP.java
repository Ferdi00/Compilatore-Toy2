package nodes;

public class AndOP extends Node{

    public AndOP(Node n1, Node n2) {
        super("AndOP");
        addChild(n1);
        addChild(n2);
    }

}
