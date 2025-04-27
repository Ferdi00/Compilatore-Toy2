package visitor.typechecking;

import nodes.Node;
import visitor.NodeVisitor;
import visitor.scoping.ScopingTable;

import java.util.*;

public class TypeVisitor implements NodeVisitor<Node> {
    private ScopingTable scopingTable;
    private final TypeVisitorUtils utils = new TypeVisitorUtils();
    private Map<String, Map<String, String>> procParamTypeMap = new HashMap<>();  // Mappa di mappe

    Set<String> excluded = new HashSet<>(Set.of("IntConst", "RealConst", "StringConst"));

    public TypeVisitor(ScopingTable scopingTable, Map<String, Map<String, String>> procParamTypeMap) {
        this.scopingTable = scopingTable;
        this.procParamTypeMap = procParamTypeMap;
    }

    public Object visitNode(Node node) {
        if (node == null) {
            return null;
        }

        String node_name = node.getValue();
        List<Node> list1 = node.getList1();
        List<Node> list2 = node.getList2();
        List<Node> children = node.getChildNodes();

        if (node_name.contains("-")) {
            String TypeNode = node_name.split("-")[0];
            String NodeName = node_name.split("-")[1];


            switch (TypeNode) {
                case "Function" -> {

                    scopingTable = scopingTable.getGlobalScope();
                    scopingTable = scopingTable.getChildScopingTable(NodeName);
                    if (scopingTable == null) {
                        return null;
                    }

                    utils.check_Function(node, scopingTable, NodeName);

                    for (Node n : children) {
                        if (n != null && "BodyOP".equals(n.getValue())) {
                            n.accept(this);
                        }
                    }

                    ScopingTable oldst = scopingTable;
                    scopingTable = scopingTable.getParent();
                    if (scopingTable == null)
                        scopingTable = oldst;
                }

                case "Procedure" -> {
                    scopingTable = scopingTable.getGlobalScope();
                    scopingTable = scopingTable.getChildScopingTable(NodeName);
                    if (scopingTable == null) {
                        return null;
                    }
                    utils.check_Procedure(node);

                    for (Node n : children) {
                        if (n != null && "BodyOP".equals(n.getValue())) {
                            n.accept(this);
                        }
                    }

                    ScopingTable oldst = scopingTable;
                    scopingTable = scopingTable.getParent();
                    if (scopingTable == null)
                        scopingTable = oldst;
                }

                case "ProcCall" -> {
                    if (procParamTypeMap == null) {
                        throw new Error("La mappa dei parametri non è stata inizializzata.");
                    }
                    utils.check_ParamCall(scopingTable,node, procParamTypeMap, NodeName);  // Passare la mappa paramTypeMap
                }

                case "FunCall" -> {
                    Map.Entry<String, String> entry = scopingTable.isDeclared(NodeName);
                    if (entry == null) {
                        throw new Error("La variabile non è stata dichiarata: " + node.getValue() + " Current ST: " + scopingTable);
                    }
                    utils.check_FunCall(node, scopingTable, NodeName);
                }
            }
        }


        else if (node.getClass().getSimpleName().equals("Id")) {
            Map.Entry<String, String> entry = scopingTable.isDeclared(node.getValue());
            if (entry == null) {
                throw new Error("LA VARIABILE NON E' STATA DICHIARATA: " + node.getValue() + " CURRENT ST: " + scopingTable);
            } else {
                node.setTYPENODE(entry.getValue().split(",")[1].trim());
            }
        }

        else if(node_name.equals("PlusOP")||node_name.equals("TimesOP")||node_name.equals("MinusOP")){
            node = utils.check_binOP(node,scopingTable);
        }
        else if(node_name.equals("LtOP")||node_name.equals("LeOP")||node_name.equals("GtOP")||node_name.equals("GeOP")||node_name.equals("EqOP")||node_name.equals("NeOP")){
            node = utils.check_relOP(node,scopingTable);
        }else if(node_name.equals("AssignOP") || (node_name.equals("DeclsOP") && node.getOperation()!= null && node.getOperation().equals("Assign"))){
            utils.check_assignOP(node,scopingTable);
        }

        else if (node_name.equals("WhileOP")) {
            utils.check_whileOP(node);
            scopingTable = scopingTable.getChildScopingTable("WhileOP Body");

            if (scopingTable == null) {
                return null;
            }

            for (Node n : children) {
                if (n != null) {
                    n.accept(this);
                }
            }

            ScopingTable oldst = scopingTable;
            scopingTable = scopingTable.getParent();
            if(scopingTable == null)
                scopingTable = oldst;

        }

        else if (node.getValue().equals("IfStatOP")){

            utils.check_ifStatOP(node);
            int currentIfCounter = scopingTable.getIfCounter()-1;
            for (Node n : children) {
                if (n != null) {
                    if ("BodyOP".equals(n.getValue())) {
                        if(currentIfCounter == 0){
                            scopingTable = scopingTable.getChildScopingTable("IfStatOP Body");
                            currentIfCounter++;
                        }else{
                            scopingTable = scopingTable.getChildScopingTable("IfStatOP"+currentIfCounter+" Body");
                            currentIfCounter++;
                        }
                        if (scopingTable == null) {
                            continue;
                        }
                        n.accept(this);

                    } else if ("ElseOP".equals(n.getValue())) {
                        scopingTable = scopingTable.getChildScopingTable("ElseOP Body");
                        if (scopingTable == null) {
                            continue;
                        }
                        n.accept(this);
                        scopingTable = scopingTable.getParent();
                    }

                }

            }


            if (list1 != null) {
                for (Node l : list1) {
                    if (l != null) {
                        l.accept(this);
                    }
                }
            }


            scopingTable = scopingTable.getParent();

        }

        if(!node_name.contains("Procedure") && !node_name.contains("Function") && !node_name.equals("IfStatOP") && !node_name.equals("WhileOP") && !excluded.contains(node.getClass().getSimpleName())){

            for (Node n : children) {
                if (n != null) {
                    n.accept(this);
                }
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

        return null;
    }
}
