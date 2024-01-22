package res;

public class PlusOP extends Node{

    public PlusOP(Node n1, Node n2) {
        super("PlusOP");
        addChild(n1);
        addChild(n2);

    }

}
