package nodes;

import java.util.LinkedList;
import java.util.List;

public class FunctionOP extends Node{

    public final List<Node> list1;
    public FunctionOP(Node id, Node fp, LinkedList<Node> ts , Node b) {
        super("Function-"+id.getValue());
        this.addChild(fp);
        this.addChild(b);
        list1 = ts;
        this.setListname1 ("Types");
    }

    public List<Node> getList1(){
        return list1;
    }

}
