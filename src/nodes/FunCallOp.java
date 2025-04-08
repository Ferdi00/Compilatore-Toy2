package nodes;

import java.util.LinkedList;
import java.util.List;

public class FunCallOp extends Node{
    public final Id i;
    public final List<Node> list1;
    public FunCallOp(Id i, LinkedList<Node> pe) {
        super("FunCall-"+i.getValue());
        if(i.getValue().isEmpty()){
            throw new IllegalStateException();
        }
        this.i = i;
        this.list1 = pe;
        this.setListname1 ("Exprs");
    }

    public List<Node> getList1(){
        return list1;
    }

}