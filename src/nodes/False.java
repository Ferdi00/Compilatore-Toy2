package nodes;

public class False extends Node{

    public False() {
        super("False");
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
