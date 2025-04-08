package nodes;

import java.util.LinkedList;
import java.util.List;

public class StatOP extends Node{
    LinkedList<Node> list1;
    LinkedList<Node> list2;
    IOArgsNode ioArgsNode;

    private String TYPENODE;


    public StatOP(Node n1) {
        super("StatOP");
        addChild(n1);
    }
    public StatOP(LinkedList<Node> l, String name1) {
        super("StatOP");
        this.list1 = l;
        this.setListname1(name1);
    }

    public StatOP(LinkedList<Node> l, String name1, String type) {
        super("StatOP");
        TYPENODE = type;
        this.list1 = l;
        this.setListname1(name1);
    }

    public StatOP(IOArgsNode io, String name1, String type) {

        super("StatOP");
        TYPENODE = type;
        this.ioArgsNode = io;
        this.setListname1(name1);
    }

    public StatOP(LinkedList<Node> l1,LinkedList<Node> l2, String name1, String name2, String type) {
        super(type);
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

    public IOArgsNode getIOArgsNode(){
        return ioArgsNode;
    }

   public String getTYPENODE() {
    return TYPENODE;
}

    public void setTYPENODE(String TYPENODE) {
        this.TYPENODE = TYPENODE;}

    }
