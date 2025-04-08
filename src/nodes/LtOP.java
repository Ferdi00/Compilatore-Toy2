package nodes;

public class LtOP extends Node{

    public LtOP(Node n1, Node n2) {
        super("LtOP");
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
