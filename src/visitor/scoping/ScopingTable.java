package visitor.scoping;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScopingTable {

    public final String scopeName;

    private ScopingTable parent;

    private final Map<String, ScopingTable> childrens = new LinkedHashMap<>();
    private final Map<String, String> table = new HashMap<>();
    private Integer ifCounter;


    public ScopingTable getParent() {
        return parent;
    }

    public void setParent(ScopingTable parent) {
        this.parent = parent;
    }


    public ScopingTable(String scopeName) {
        this.scopeName = scopeName;
    }

    public Integer getIfCounter() {
        return ifCounter;
    }

    public void setIfCounter(Integer ifCounter) {
        this.ifCounter = ifCounter;
    }

    public void addVar(String name, String type) {
        Map.Entry<String, String> entry = isDeclaredInScope(name);
        if (entry != null) {
            throw new Error("ERRORE: VARIABILE GIA' DICHIARATA: " + entry.getKey() + " con tipo " + entry.getValue().split(",")[1].trim());
        } else {
            table.put(name, type);
        }
    }

    public void addProcedureOrFunction(String name, String type) {
        Map.Entry<String, String> entry = isDeclared(name);
        if (entry != null) {
            String[] parts = entry.getValue().split(", ");
            String returnType = parts[1];
            throw new Error("ERRORE: FUNZIONE/PROCEDURA GIA' DICHIARATA: " + entry.getKey() + returnType);
        } else {
            table.put(name, type);
        }
    }


    public ScopingTable createChildScopingTable(String scopeName, Boolean isIf) {
        if(isIf){
            ifCounter++;
        }
        ScopingTable childScopingTable = new ScopingTable(scopeName);
        childScopingTable.setParent(this);
        childrens.put(scopeName, childScopingTable);
        return childScopingTable;
    }

    public ScopingTable getChildScopingTable(String scopeName) {
        if (childrens.containsKey(scopeName)) {
            return childrens.get(scopeName);
        } else {
            throw new Error("ERRORE: TABELLA NON TROVATA: "+scopeName);
        }
    }

    public ScopingTable getChildScopingTable2(String scopeName) {
        return childrens.getOrDefault(scopeName, null);
    }


    public Map.Entry<String, String> isDeclared(String name) {
        if (table.containsKey(name)) {
            return new AbstractMap.SimpleEntry<>(name, table.get(name));
        } else if (parent != null ) {
            return parent.isDeclared(name);
        } else {
            return null;
        }
    }

    public Map.Entry<String, String> isDeclaredInScope(String name) {
        if (table.containsKey(name)) {
            return new AbstractMap.SimpleEntry<>(name, table.get(name));
        } else {
            return null;
        }
    }

    public String getFunctionReturnType(String functionName) {
        Map.Entry<String, String> entry = isDeclared(functionName);
        if (entry != null) {
            String[] parts = entry.getValue().split(" -> ");
            parts = parts[1].split(",");
            parts[0] = parts[0].trim();
            if(parts[0].endsWith(")")) {
                parts[0] = parts[0].substring(0, parts[0].length() - 1);
            }
            return parts[0];
        } else if (parent != null ) {
            return parent.getFunctionReturnType(functionName);
        } else {
            return null;
        }
    }

    public String getVariableTypeFormatted(String variableName) {
        Map.Entry<String, String> entry = isDeclared(variableName);
        StringBuilder formatString = new StringBuilder();
        String type;
        if (entry != null) {
            String[] parts = entry.getValue().split(", ");
            type = parts[1].trim();
            switch (type) {
                case "INTEGER" -> formatString.append("%d");
                case "REAL" -> formatString.append("%lf");
                case "STRING" -> formatString.append("%s");
                default -> {
                }
            }
            return formatString.toString();
        } else if (parent != null ) {
            return parent.getVariableTypeFormatted(variableName);
        } else {
            return null;
        }
    }


    public ScopingTable getGlobalScope() {
        ScopingTable current = this;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }



    public Map<String, String> getTable() {
        return table;
    }
    public String toString() {

        StringBuilder childrenMapList = new StringBuilder();
        if(!childrens.isEmpty()){
            for (Map.Entry<String, ScopingTable> entry : childrens.entrySet()) {
                childrenMapList.append(entry.getValue());
            }
        }
        StringBuilder orderedTable = new StringBuilder();
        for (Map.Entry<String, String> entry : table.entrySet()) {
            orderedTable.append("<").append(entry.getKey().toUpperCase()).append(",").append(entry.getValue()).append(">\n");
        }
        if(getParent()!= null){return "-- <ST:"+scopeName.toUpperCase()+" -- PARENT:"+getParent().scopeName.toUpperCase()+"> --\n" + orderedTable+ "\n"+childrenMapList;}
        return "-- <ST:"+scopeName.toUpperCase()+" -- PARENT: NULL> --\n" + orderedTable+ "\n"+childrenMapList;
    }
}
