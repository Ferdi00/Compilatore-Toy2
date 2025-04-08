package nodes;

public class Id extends Node{

    private String TYPENODE;
    private Boolean isOut = false;
    public Id(String value) {
        super(value);
    }
    public Id(String value, String type) {
        super(value);
        this.TYPENODE = type;
        if(type.equals("OUT"))
            this.isOut = true;
        System.out.println("ho trovato un out id: "+value+" - "+type);
    }

    @Override
    public String getTYPENODE() {
        return TYPENODE;
    }

    public Boolean getIsOut() {
        return isOut;
    }

    public void setTYPENODE(String TYPENODE) {
        this.TYPENODE = TYPENODE;
    }
}
