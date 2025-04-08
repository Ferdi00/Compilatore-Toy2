package visitor.scoping;

import nodes.Node;
import visitor.NodeVisitor;

import java.util.*;

public class ScopingVisitor implements NodeVisitor<Node> {
    private ScopingTable scopingTable;
    private final Map<String, Map<String, String>> procParamTypeMap = new HashMap<>();  // Mappa di mappe
    public Map getParamTypeMap (){
        return this.procParamTypeMap;
    }
    @Override
    public ScopingTable visitNode(Node node) {

        List<Node> list1 = node.getList1();
        List<Node> list2 = node.getList2();
        List<Node> children = node.getChildNodes();

        if (node.getValue().equals("program")) {
            scopingTable = new ScopingTable("global");
            visitRemainingNodes(children, list1, list2);
        }
        else if (node.getValue().contains("-")) {

            String TypeNode = node.getValue().split("-")[0];
            String NodeName = node.getValue().split("-")[1] ;
            boolean isCreated = false;

            switch (TypeNode) {
                case "Function" -> {
                    List<Node> TypeList = node.getList1();
                    if (children == null || children.isEmpty() || children.get(0) == null) {
                        StringBuilder signature = new StringBuilder(" FUNCTION, ( null -> ");
                        if (TypeList != null && !TypeList.isEmpty()) {
                            for (Node i : TypeList) {
                                signature.append(i.getValue()).append(",");
                                signature = new StringBuilder(signature.substring(0, signature.length() - 1));
                                signature.append(" )");

                                scopingTable.addProcedureOrFunction(NodeName, signature.toString());
                                scopingTable = scopingTable.createChildScopingTable(NodeName,false);
                                // scopingTable = scopingTable.getParent();
                            }
                        }
                    }
                    assert children != null;
                    for (Node n : children) {
                        if (n != null && "FuncParams".equals(n.getValue())) {

                            StringBuilder signature = new StringBuilder(" FUNCTION, (" + n.getChildNodes().get(1).getValue());
                            list1 = n.getList1();
                            if (list1 != null && !list1.isEmpty()) {
                                for (Node i : list1) {
                                    if (i.getClass().getSimpleName().equals("TypeOP"))
                                        signature.append(",").append(i.getValue());
                                }
                            }
                            signature.append(" -> ");
                            if (TypeList != null && !TypeList.isEmpty()) {
                                for (Node i : TypeList) {
                                    signature.append(i.getValue()).append(",");
                                }
                            }
                            signature = new StringBuilder(signature.substring(0, signature.length() - 1));
                            signature.append(")");
                            scopingTable.addProcedureOrFunction(NodeName, signature.toString());
                            scopingTable = scopingTable.createChildScopingTable(NodeName,false);
                            scopingTable.addVar(n.getChildNodes().get(0).getValue(), " VAR, " + n.getChildNodes().get(1).getValue());
                            if (list1 != null && !list1.isEmpty()) {
                                for (int i = 0; i < list1.size(); i += 2) {
                                    String value = list1.get(i).getValue();
                                    String type = list1.get(i + 1).getValue();
                                    scopingTable.addVar(value, " VAR, " + type);
                                }
                            }
                        } else if (n != null && "BodyOP".equals(n.getValue()) && scopingTable != null) {
                            n.accept(this);
                        }
                        assert n != null;
                        assert scopingTable != null;

                    }
                    ScopingTable oldst = scopingTable;

                    scopingTable = scopingTable.getParent();

                    if(scopingTable == null)
                        scopingTable = oldst;

                }
                case "Procedure" -> {
                    for (Node n : children) {
                        if (n != null && "ProcParams".equals(n.getValue())) {
                            StringBuilder signature = new StringBuilder(" PROCEDURE, (" + n.getChildNodes().get(1).getValue());
                            list1 = n.getList1();
                            if (list1 != null && !list1.isEmpty()) {
                                Collections.reverse(list1);
                                for (Node i : list1) {
                                    if (i.getClass().getSimpleName().equals("TypeOP"))
                                        signature.append(",").append(i.getValue());
                                }
                            }
                            signature.append(" -> null)");
                            scopingTable.addProcedureOrFunction(NodeName, signature.toString());
                            scopingTable = scopingTable.createChildScopingTable(NodeName,false);
                            scopingTable.addVar(n.getChildNodes().get(0).getValue(), " VAR, " + n.getChildNodes().get(1).getValue());

                            if (list1 != null && !list1.isEmpty()) {
                                for (int i = 0; i < list1.size(); i += 2) {
                                    String type = list1.get(i).getValue();
                                    String value = list1.get(i + 1).getValue();
                                    scopingTable.addVar(value, " VAR, " + type);
                                }
                            }
                            isCreated = true;

                            Map<String, String> paramTypeMap = new LinkedHashMap<>(); // Usa LinkedHashMap per mantenere l'ordine di inserimento
                            // Ottieni la lista dei parametri dal nodo (nodelist1)
                            List<Node> l2 = n.getList1();
                            List<Node> l1 = n.getChildNodes();

                            // Itera attraverso la prima lista dei parametri
                            if (l1 != null && !l1.isEmpty()) {
                                for (Node paramNode : l1) {
                                    if (paramNode != null && paramNode.getClass().getSimpleName().equals("ProcParamIdOP")) {
                                        // Recupera il nome del parametro
                                        String paramName = paramNode.getValue();
                                        // Risolvi il tipo del parametro usando la scoping table
                                        String paramType = paramNode.getTYPENODE();
                                        // Salva il nome del parametro e il tipo nella mappa
                                        paramTypeMap.put(paramName, paramType);
                                    }
                                }
                            }



                            // Itera attraverso la seconda lista dei parametri
                            if (l2 != null && !l2.isEmpty()) {
                                for (Node paramNode : l2) {
                                    if (paramNode != null && paramNode.getClass().getSimpleName().equals("ProcParamIdOP")) {
                                        // Recupera il nome del parametro
                                        String paramName = paramNode.getValue();
                                        // Risolvi il tipo del parametro usando la scoping table
                                        String paramType = paramNode.getTYPENODE();
                                        // Salva il nome del parametro e il tipo nella mappa
                                        paramTypeMap.put(paramName, paramType);

                                    }

                                }
                            }
                            procParamTypeMap.put(NodeName, paramTypeMap);
                        }

                        if (n != null && "BodyOP".equals(n.getValue())) {
                            if (!isCreated) {
                                scopingTable.addProcedureOrFunction(NodeName, " PROCEDURE, ( null -> null)");
                                scopingTable = scopingTable.createChildScopingTable(NodeName,false);
                            }

                            n.accept(this);
                        }

                    }
                    scopingTable = scopingTable.getParent();
                }

            }
        }
        else{
            switch (node.getValue()) {
                case "DeclsOP" -> handleDeclsOPNode(node, children, list1, list2);
                case "WhileOP" -> handleWhileOPNode(node, children);
                case "IfStatOP" -> handleIfStatOPNode(node, children, list1);
                default -> visitRemainingNodes(children, list1, list2);
            }
        }
        return scopingTable;
    }
    private void handleIfStatOPNode(Node node, List<Node> children, List<Node> list1) {
        // Initialize the counter for the current scope if not already initialized

        if (scopingTable.getIfCounter() == null) {
            scopingTable.setIfCounter(0);
        }

        // Increment the counter for the current scope
        int currentIfCounter = scopingTable.getIfCounter();

        // Create a new scope for the IfStatOP
        if (currentIfCounter == 0) {
            scopingTable = scopingTable.createChildScopingTable(node.getValue() + " Body",true);
        } else {
            scopingTable = scopingTable.createChildScopingTable(node.getValue() + currentIfCounter + " Body",true);
        }

        for (Node n : children) {
            if (n != null && "BodyOP".equals(n.getValue())) {
                n.accept(this); // Visit the body of the if
                if (list1 != null) {
                    int elifCounter = 1; // Initialize the counter for ElifOP
                    for (Node l : list1) {
                        if (l != null && "ElifOP".equals(l.getValue())) {
                            // Create a new scope for each ElifOP block
                            scopingTable = scopingTable.createChildScopingTable(l.getValue() + elifCounter + " Body",false);
                            elifCounter++; // Increment the counter after each use
                            l.accept(this); // Visit the ElifOP block
                            scopingTable = scopingTable.getParent(); // Return to the IfStatOP scope
                        }
                    }
                }
            }
        }

        // Handle ElseOP blocks separately
        for (Node n : children) {
            if (n != null && "ElseOP".equals(n.getValue())) {
                scopingTable = scopingTable.createChildScopingTable(n.getValue() + " Body",false);
                n.accept(this);
                scopingTable = scopingTable.getParent(); // Return to the parent scope after ElseOP
            }
        }
        scopingTable = scopingTable.getParent(); // Return to the parent scope after IfStatOP
    }
    private void handleWhileOPNode(Node node, List<Node> children){
        scopingTable = scopingTable.createChildScopingTable(node.getValue()+" Body",false);
        for (Node n : children) {
            if (n != null && "BodyOP".equals(n.getValue())) {
                scopingTable=scopingTable.getChildScopingTable("WhileOP Body");
                n.accept(this);
            }
            scopingTable = scopingTable.getParent();
        }
    }
    private void handleDeclsOPNode(Node node, List<Node> children, List<Node> list1, List<Node> list2){
        List<String> lista_nomi = new LinkedList<>();
        List<String> lista_tipi = new LinkedList<>();
        int varCounter = 0;

        if (list1!= null && !list1.isEmpty()){
            for (Node n : list1) {
                lista_nomi.add(n.getValue());
            }
        }
        if (list2!= null && !list2.isEmpty()){
            for (Node n : list2) {
                lista_tipi.add(n.getTYPENODE());
                varCounter++;
            }
        }
        else if (children != null && !children.isEmpty()) {
            for (Node n : children) {
                lista_tipi.add(n.getValue());
            }
        }
        if (varCounter!=0 && varCounter!= lista_nomi.size()){
            throw new Error("ERRORE NELLA DICHIARAZIONE DELLE VARIABILI: "+lista_nomi+" NELLA FUNZIONE: "+scopingTable.scopeName);
        }
        for (int i = 0; i < lista_nomi.size(); i++) {
            String nome = lista_nomi.get(i); // Otteniamo il nome corrente dalla lista
            if (node.getChildNodes() != null && !node.getChildNodes().isEmpty()) {
                scopingTable.addVar(nome, " VAR, " + node.getChildNodes().get(0).getValue());
            } else {
                scopingTable.addVar(nome, " VAR, " + lista_tipi.get(i));
            }
        }
    }
    private void visitRemainingNodes(List<Node> children, List<Node> list1, List<Node> list2){
        for (Node n : children) {
            if (n != null){
                n.accept(this);}
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
}


