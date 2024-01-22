package res;

public class GtOP extends Node{

    public GtOP(Node n1, Node n2) {
        super("GTOP");
        addChild(n1);
        addChild(n2);
    }
}
