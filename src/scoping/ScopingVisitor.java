package scoping;

import res.Node;
import visitor.NodeVisitor;
import java.util.LinkedList;
import java.util.List;

public class ScopingVisitor implements NodeVisitor<Node> {
    private ScopingTable scopingTable;
    @Override
    public Object visitNode(Node node) {

        //System.out.println(scopingTable);


        List<Node> list1 = node.getList1();
        List<Node> list2 = node.getList2();
        List<Node> children = node.getChildNodes();

        if (node.getValue().equals("program")) {
            scopingTable = new ScopingTable("global");
        }
        else if (node.getValue().contains("-")) {

            String TypeNode = node.getValue().split("-")[0];
            String NodeName = node.getValue().split("-")[1] ;

            if (TypeNode.equals("Function")) {

                List<Node>  TypeList = node.getList1();
                if(children == null || children.isEmpty() || children.get(0) == null){
                    StringBuilder signature = new StringBuilder(" FUNCTION, ( -> ");
                    if(TypeList!= null && !TypeList.isEmpty()){
                        for (Node i : TypeList){
                            signature.append(i.getValue()).append(",");
                            signature = new StringBuilder(signature.substring(0, signature.length() - 1));
                            signature.append(" )");
                            scopingTable.addProcedureOrFunction(NodeName, signature.toString());
                            scopingTable = scopingTable.createChildScopingTable(NodeName);
                            scopingTable = scopingTable.getParent();
                        }
                    }
                }
                assert children != null;
                for (Node n : children) {
                    if (n != null && "FuncParams".equals(n.getValue())) {

                        StringBuilder signature = new StringBuilder(" FUNCTION, (" + n.getChildNodes().get(1).getValue());
                        list1 = n.getList1();
                        if(list1!= null && !list1.isEmpty()){
                            for (Node i : list1){
                                if(i.getClass().getSimpleName().equals("TypeOP"))
                                    signature.append(",").append(i.getValue());
                            }
                        }
                        signature.append(" -> ");
                        if(TypeList!= null && !TypeList.isEmpty()){
                            for (Node i : TypeList){
                                    signature.append(i.getValue()).append(",");
                            }
                        }
                        signature = new StringBuilder(signature.substring(0, signature.length() - 1));
                        signature.append(")");
                        scopingTable.addProcedureOrFunction(NodeName, signature.toString());
                        scopingTable = scopingTable.createChildScopingTable(NodeName);
                        scopingTable.addVar(n.getChildNodes().get(0).getValue()," VAR, "+n.getChildNodes().get(1).getValue());
                        if(list1!= null && !list1.isEmpty()){
                        for (int i = 0; i < list1.size(); i += 2) {
                            String value = list1.get(i).getValue();
                            String type = list1.get(i + 1).getValue();
                            scopingTable.addVar(value," VAR, "+type);
                        }}
                    }
                    else if (n != null && "BodyOP".equals(n.getValue()) && scopingTable != null) {
                        n.accept(this);
                    }
                    assert n != null;
                    assert scopingTable != null;

                }
                scopingTable = scopingTable.getParent();

            }
            else if (TypeNode.equals("Procedure")) {
                for (Node n : children) {
                    if (n != null && "ProcParams".equals(n.getValue())) {
                        StringBuilder signature = new StringBuilder(" PROCEDURE, (" + n.getChildNodes().get(1).getValue());
                        list1 = n.getList1();
                        if(list1!= null && !list1.isEmpty()){
                            for (Node i : list1){
                                if(i.getClass().getSimpleName().equals("TypeOP"))
                                    signature.append(",").append(i.getValue());
                            }
                        }
                        signature.append(" -> null)");
                        scopingTable.addProcedureOrFunction(NodeName, signature.toString());
                        scopingTable = scopingTable.createChildScopingTable(NodeName);
                        scopingTable.addVar(n.getChildNodes().get(0).getValue()," VAR, "+n.getChildNodes().get(1).getValue());

                        if(list1!= null && !list1.isEmpty()){
                        for (int i = 0; i < list1.size(); i += 2) {
                            String value = list1.get(i).getValue();
                            String type = list1.get(i + 1).getValue();
                            scopingTable.addVar(value," VAR, "+type);
                        }}
                    }

                    else if (n != null && "BodyOP".equals(n.getValue())) {
                        n.accept(this);
                    }

                }
                scopingTable = scopingTable.getParent();
            }
        }

       else if (node.getValue().equals("DeclsOP")) {
            List<String> lista_nomi = new LinkedList<>();
            List<String> lista_tipi = new LinkedList<>();
            if (list1!= null && !list1.isEmpty()){
                for (Node n : list1) {
                    lista_nomi.add(n.getValue());
                }
            }
            if (list2!= null && !list2.isEmpty()){
                for (Node n : list2) {
                    lista_tipi.add(n.getTYPENODE());
                }

            }
            for(String nome : lista_nomi){
                if(node.getChildNodes()!= null && !node.getChildNodes().isEmpty()){
                    scopingTable.addVar(nome," VAR, "+node.getChildNodes().get(0).getValue());
                }
                else
                    scopingTable.addVar(nome," VAR, "+lista_tipi.get(0));
            }
        }

        else if (node.getValue().equals("WhileOP")) {
            System.out.println("Scoping quando sto nel while "+scopingTable.scopeName);
            scopingTable = scopingTable.createChildScopingTable(node.getValue()+" Body");
            for (Node n : children) {
                if (n != null && "BodyOP".equals(n.getValue())) {
                    System.out.println("Scoping quando sto nel while e if "+scopingTable.scopeName);
                    scopingTable=scopingTable.getChildScopingTable("WhileOP Body");
                    n.accept(this);
                }
                scopingTable = scopingTable.getParent();
            }
        }

        else if (node.getValue().equals("IfStatOP")) {
            scopingTable = scopingTable.createChildScopingTable(node.getValue()+" Body");
            for (Node n : children) {
                if (n != null && "BodyOP".equals(n.getValue())) {
                    n.accept(this);
                    scopingTable = scopingTable.getParent();
                }
                if (n != null && "ElseOP".equals(n.getValue())) {
                    scopingTable = scopingTable.createChildScopingTable(n.getValue()+" Body");
                    n.accept(this);
                }
            }
            if(list1 != null){
            for (Node l : list1){
                scopingTable = scopingTable.getParent();
                scopingTable = scopingTable.createChildScopingTable(l.getValue()+" Body");
                l.accept(this);
            }}
            scopingTable = scopingTable.getParent();
        }

        //System.out.println("pre visita: "+node.getValue()+" ");
       //Il restante codice è per visitare i restanti nodi esclusi PPROCEDUREOP e FUNCTIONOP essendo stati già visitati
        if(!node.getValue().contains("Procedure") && !node.getValue().contains("Function") && !node.getValue().equals("IfStatOP") && !node.getValue().equals("WhileOP")){

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
        }




        return scopingTable;
    }}
