package nodes;

import java.util.List;

public class ProcParamsOP extends Node{
    public final List<Node> list1;
    public ProcParamsOP(Node p, Node t, List<Node> op) {
        super("ProcParams");
        this.addChild(p);
        this.addChild(t);
        list1 = op;
        this.setListname1("OtherProcParams");
    }

    public List<Node> getList1(){
        return list1;
    }

}