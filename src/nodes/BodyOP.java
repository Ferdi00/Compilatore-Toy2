package nodes;

import java.util.LinkedList;
import java.util.List;

public class BodyOP extends Node{

    private LinkedList<Node> list1;
    private LinkedList<Node> list2;
    private String TYPENODE = "NOTYPE";
    public BodyOP() {
        super("BodyOP");
        this.list1 = new LinkedList<>();
        this.list2 = new LinkedList<>();
        this.setListname1 ("VarDeclOPs");
        this.setListname2 ("StatOPs");
    }

    public void addVarDecl(VarDeclOP v){
        this.list1.add(v);
    }
    public void addStat(StatOP s){
        this.list2.add(s);
    }

    public List<Node> getList1(){
        return this.list1;
    }

    public List<Node> getList2(){
        return this.list2;
    }

    @Override
    public String getTYPENODE() {
        return TYPENODE;
    }

    public void setTYPENODE(String TYPENODE) {
        this.TYPENODE = TYPENODE;
    }
}
