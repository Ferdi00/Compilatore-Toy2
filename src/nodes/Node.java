package nodes;

import visitor.NodeVisitor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Node {
    private String listname1;
    private String listname2;

    private String value;
    private String operation;
    private String TYPENODE;
    private List<Node> childNodes;
     LinkedList<Node> list1;
     LinkedList<Node> list2;

    public Node(String value) {
        this.value = value;
        this.childNodes = new LinkedList<>();
    }

    public Node() {
        this.childNodes = new LinkedList<>();
    }

    public void modifyName() {
        if (this.value != null && !this.value.isEmpty()) {
            this.value = "$(" + this.value + ")";
            System.out.println("Nuovo valore del nodo: " + this.value);
        } else {
            System.out.println("Il valore del nodo è null o vuoto, non può essere modificato.");
        }
    }

    public Node copy(String newValue) {
        return new Node(newValue);
    }


    public void addChild(Node childNode) {
        this.childNodes.add(childNode);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String s) {
        this.value = s;
    }

    public List<Node> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<Node> childNodes) {
        this.childNodes = childNodes;
    }

    public <T> T accept(NodeVisitor<Node> visitor) {
        return (T) visitor.visitNode(this);
    }

    public List<Node> getList1(){
        return list1;
    }

    public List<Node> getList2(){
        return list2;
    }
    public String getListname1(){
        return this.listname1;
    }
    public String getListname2(){
        return this.listname2;
    }
    public void setListname1(String Name){
        this.listname1 = Name;
    }
    public void setListname2(String Name){
        this.listname2 = Name;
    }
    public String getTYPENODE() {
        return TYPENODE;
    }
    public void setTYPENODE(String TYPENODE) {
        this.TYPENODE = TYPENODE;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public ArrayList<String> printList1() {
        ArrayList<String> stringList = new ArrayList<>();
        list1 = (LinkedList<Node>) getList1();
        if (list1 != null && !list1.isEmpty()) {
            for (Node n : list1) {
                if (n != null) {
                    stringList.add(n.getValue());
                }
            }
        }

        return stringList;
    }

    public ArrayList<String> printList2() {
        ArrayList<String> stringList = new ArrayList<>();
        list2 = (LinkedList<Node>) getList2();
        if (list2 != null && !list2.isEmpty()) {
            for (Node n : list2) {
                if (n != null) {
                    stringList.add(n.getValue());
                }
            }
        }

        return stringList;
    }
}
