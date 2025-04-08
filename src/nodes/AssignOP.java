package nodes;

import java.util.LinkedList;
import java.util.List;

public class AssignOP extends Node{
    LinkedList<Node> list1;
    LinkedList<Node> list2;
    public AssignOP(Node n1) {
        super("StatOP");
        addChild(n1);
    }
    public AssignOP(LinkedList<Node> l, String name1) {
        super("StatOP");
        this.list1 = l;
        this.setListname1(name1);
    }
    public AssignOP(LinkedList<Node> l1, LinkedList<Node> l2, String name1, String name2) {
        super("StatOP");
        this.list1 = l1;
        this.list2 = l2;
        this.setListname1(name1);
        this.setListname2(name2);
    }

    public List<Node> getList1(){
        return list1;
    }

    public List<Node> getList2(){
        return list2;
    }

}
