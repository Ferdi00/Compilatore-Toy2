package nodes;

public class GtOP extends Node{


    public GtOP(Node n1, Node n2) {
        super("GtOP");
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
