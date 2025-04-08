package nodes;

import java.util.LinkedList;
import java.util.List;

public class ProcCallOp extends Node{
    public final Id i;
    public final List<Node> list1;
    public ProcCallOp(Id i, LinkedList<Node> pe) {
        super("ProcCall-"+i.getValue());
        if(i.getValue().isEmpty()){
            throw new IllegalStateException();
        }
        this.i = i;
        list1 = pe;
        this.setListname1 ("ProcExprs");
    }

    public List<Node> getList1(){
        return list1;
    }
}