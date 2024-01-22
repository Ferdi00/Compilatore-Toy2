package res;

public class EqOP extends Node{

    public EqOP(Node n1, Node n2) {
        super("EqOP");
        addChild(n1);
        addChild(n2);
    }
}
