package nodes;

public class IntConst extends Node{

    private String TYPENODE = "INTEGER";
    public IntConst(String value) {
        super(value);
    }

    public String getTYPENODE() {
        return TYPENODE;
    }
}
