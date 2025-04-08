package nodes;

public class ProcParamIdOP extends Node{
    private String TYPENODE;
    public ProcParamIdOP(String S, String type) {
        super(S);
        this.TYPENODE = type;

    }

    public ProcParamIdOP(String S) {
        super(S);

    }

    public String getTYPENODE() {
        return TYPENODE;
    }
    public void setTYPENODE(String TYPENODE) {
        this.TYPENODE = TYPENODE;
    }
}