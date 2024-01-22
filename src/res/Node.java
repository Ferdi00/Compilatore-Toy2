package res;

import visitor.NodeVisitor;

import java.util.LinkedList;
import java.util.List;

public class Node {
    private String listname1;
    private String listname2;

    private String value;
    private List<Node> childNodes;
     LinkedList<Node> list1;
     LinkedList<Node> list2;

    public Node(String value) {
        this.value = value;
        this.childNodes = new LinkedList<>();
    }

    public void addChild(Node childNode) {
        this.childNodes.add(childNode);
    }

    public String getValue() {
        return value;
    }

    public List<Node> getChildNodes() {
        return childNodes;
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
}
