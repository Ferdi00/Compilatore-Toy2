package nodes;

public class StringConst extends Node{

    private String TYPENODE = "STRING";
    public StringConst(String value) {
        super(value);
    }

    public String getTYPENODE() {
        return TYPENODE;
    }
}
