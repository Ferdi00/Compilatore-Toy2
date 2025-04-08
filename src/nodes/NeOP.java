package nodes;

public class NeOP extends Node{

    public NeOP(Node n1, Node n2) {
        super("NeOP");
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
