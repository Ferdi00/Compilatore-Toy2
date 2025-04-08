package nodes;

public class GeOP extends Node{

    public GeOP(Node n1, Node n2) {
        super("GeOP");
        addChild(n1);
        addChild(n2);
    }
    private String TYPENODE = "BOOLEAN";
    @Override
    public String getTYPENODE() {
        return TYPENODE;
    }

    public void setTYPENODE(String TYPENODE) {
        this.TYPENODE = TYPENODE;
    }
}
