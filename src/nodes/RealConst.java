package nodes;

public class RealConst extends Node{
    private String TYPENODE = "REAL";
    public RealConst(String value) {
        super(value);
    }

    public String getTYPENODE() {
        return TYPENODE;
    }
}
