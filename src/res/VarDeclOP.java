package res;

import java.util.LinkedList;
import java.util.List;

public class VarDeclOP extends Node{
    LinkedList<Node> list1;
    public VarDeclOP(LinkedList<Node> list) {
        super("VarDeclOP");
        this.list1 = list;
        this.setListname1("Decls");
    }

    public List<Node> getList1(){
        return list1;
    }


}
