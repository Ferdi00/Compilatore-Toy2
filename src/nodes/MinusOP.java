package nodes;

public class MinusOP extends Node{

    public MinusOP(Node n1, Node n2) {
        super("MinusOP");
        addChild(n1);
        addChild(n2);
    }

}
