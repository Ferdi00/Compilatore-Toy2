package nodes;

public class ElifOP extends Node{
    public ElifOP(Node expr, Node body) {
        super("ElifOP");
        this.addChild(expr);
        this.addChild(body);
    }
    private String TYPENODE = "NOTYPE";
    public String getTYPENODE() {
        return TYPENODE;
    }

    public void setTYPENODE(String TYPENODE) {
        this.TYPENODE = TYPENODE;
    }
}
