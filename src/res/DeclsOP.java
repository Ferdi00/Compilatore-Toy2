package res;

import java.util.LinkedList;
import java.util.List;

public class DeclsOP extends Node{
    LinkedList<Node> list1;
    LinkedList<Node> list2;
    public DeclsOP(LinkedList<Node> l, Node n) {
        super("DeclsOP");
        addChild(n);
        this.list1 = l;
        this.setListname1 ("Ids");
    }
    public DeclsOP(LinkedList<Node> l1, LinkedList<Node> l2) {
        super("DeclsOP");
        this.list1 = l1;
        this.list2 = l2;
        this.setListname1 ("Ids");
        this.setListname2 ("Consts");
    }

    public List<Node> getList1(){
        return list1;
    }

    public List<Node> getList2(){
        return list2;
    }

}
