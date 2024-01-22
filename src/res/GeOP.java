package res;

public class GeOP extends Node{

    public GeOP(Node n1, Node n2) {
        super("GEOP");
        addChild(n1);
        addChild(n2);
    }
}
