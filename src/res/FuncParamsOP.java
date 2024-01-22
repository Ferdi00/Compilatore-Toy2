package res;

import java.util.List;

public class FuncParamsOP extends Node{

    public final List<Node> list1;
    public FuncParamsOP(Node i, Node t, List<Node> op) {
        super("FuncParams");
        this.addChild(i);
        this.addChild(t);
        list1 = op;
        this.setListname1 ("OtherFuncParams");
    }

    public List<Node> getList1(){
        return list1;
    }

}