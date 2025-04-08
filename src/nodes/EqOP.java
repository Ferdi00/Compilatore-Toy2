package nodes;

public class EqOP extends Node{

    public EqOP(Node n1, Node n2) {
        super("EqOP");
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
