package visitor;

import res.*;
import res.StringConst;

import java.util.LinkedList;
import java.util.List;

public class PrintVisitor implements NodeVisitor<Node>{
    @Override
    public Node visitNode(Node node) {
        System.out.println("\nNODE: " + node.getValue());

        List<Node> children = node.getChildNodes();
        List<java.lang.String> childNodes = new LinkedList<>();
        List<Class<?>> UnVisitList = List.of(ProcParamIdOP.class, TypeOP.class, Id.class, RealConst.class, StringConst.class);

        for (Node n : children) {
            if (n != null) {
                childNodes.add(n.getValue());
            }
        }

        if(!childNodes.isEmpty())
            System.out.println("CHILDNODES: " + childNodes);

        List<Node> list1 = node.getList1();
        List<Node> list2 = node.getList2();

        if (list1!= null && !list1.isEmpty()){
            List<java.lang.String> l = new LinkedList<>();
            for (Node n : list1) {
                l.add(n.getValue());
            }
            System.out.println("NODELIST1: "+l);
        }
        if (list2!= null && !list2.isEmpty()){
            List<java.lang.String> l = new LinkedList<>();
            for (Node n : list2) {
                l.add(n.getValue());
            }
            System.out.println("NODELIST2: "+l);
        }

        if (list1!= null && !list1.isEmpty()){
        for (Node n : list1) {
            //System.out.println("TIPO NODO: "+n.getClass());
            if(n != null && (UnVisitList.stream().noneMatch(classe -> classe.equals(n.getClass()))))
                n.accept(this);
        }}
        if (list2!= null && !list2.isEmpty()){
        for (Node n : list2) {
            if(n != null &&(UnVisitList.stream().noneMatch(classe -> classe.equals(n.getClass()))))
                    n.accept(this);
            }}


        for (Node n : children) {
            if (n != null) {
               // System.out.println("TIPO NODO: "+n.getClass());
                if((UnVisitList.stream().noneMatch(classe -> classe.equals(n.getClass()))))
                    n.accept(this);
            }
        }

        return null;
    }

}
