package nodes;

public class ElseOP extends Node{
    public ElseOP(Node body) {
        super("ElseOP");
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
