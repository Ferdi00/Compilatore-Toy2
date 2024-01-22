package scoping;

import org.w3c.dom.Element;
import res.Node;
import visitor.NodeVisitor;

import java.util.LinkedList;
import java.util.List;

public class ScopingVisitor implements NodeVisitor<Node> {
    private ScopingTable scopingTable;
    @Override
    public Object visitNode(Node node) {

        if(scopingTable!= null)
            System.out.println(scopingTable.toString());

        List<Node> list1 = node.getList1();
        List<Node> list2 = node.getList2();
        List<Node> children = node.getChildNodes();

        if (node.getValue().equals("program")) {
            scopingTable = new ScopingTable("global");
        }
        if (node.getValue().contains("-")) {

            String TypeNode = node.getValue().split("-")[0];
            String NodeName = node.getValue().split("-")[1] ;

            System.out.println("ho trovato un: "+TypeNode+" valore: "+NodeName);
        }
        /*if (node.getValue().equals("program")) {
            scopingTable = new ScopingTable("global");
        }
        if (node.getValue().equals("program")) {
            scopingTable = new ScopingTable("global");
        }*/


        if (node.getValue().equals("DeclsOP")) {

           // System.out.println("STAMPO QUELLO CHE TROVO IN DECLSOP");
            String value = "";
            String type = "";
            if (list1!= null && !list1.isEmpty()){
                List<String> l = new LinkedList<>();
                for (Node n : list1) {
                    l.add(n.getValue());
                    value = n.getValue();
                }
                //System.out.println("NODELIST1: "+l);
            }
            if (list2!= null && !list2.isEmpty()){
                List<String> l = new LinkedList<>();
                for (Node n : list2) {
                    l.add(n.getClass().getSimpleName().toUpperCase());
                    type = n.getClass().getSimpleName().toUpperCase();
                }
                //System.out.println("NODELIST2: "+l);
            }
            scopingTable.addVar(value,type);

        }
        if (node.getValue().contains("Function")) {
            scopingTable = new ScopingTable(node.getValue());
        }



        for (Node n : children) {
            if (n != null)
                n.accept(this);
        }

        if (list1 != null && !list1.isEmpty()) {
            for (Node n : list1) {
                if (n != null) {
                    n.accept(this);
                }
            }
        }

        if (list2 != null && !list2.isEmpty()) {
            for (Node n : list2) {
                if (n != null) {
                    n.accept(this);
                }
            }
        }

        return null;
    }}
