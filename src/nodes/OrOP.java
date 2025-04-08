package nodes;

public class OrOP extends Node{

    public OrOP(Node n1, Node n2) {
        super("OrOP");
        addChild(n1);
        addChild(n2);
    }

}
