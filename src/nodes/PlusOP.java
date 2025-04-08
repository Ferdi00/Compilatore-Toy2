package nodes;

public class PlusOP extends Node{

    private String TYPENODE = "";
    public PlusOP(Node n1, Node n2) {
        super("PlusOP");
        addChild(n1);
        addChild(n2);
    }
    

}
