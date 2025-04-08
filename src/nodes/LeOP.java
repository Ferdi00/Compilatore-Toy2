package nodes;

public class LeOP extends Node{

    public LeOP(Node n1, Node n2) {
        super("LeOP");
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
