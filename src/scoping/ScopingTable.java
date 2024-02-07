package scoping;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScopingTable {

    public final String scopeName;

    private ScopingTable parent;

    private Map<String, ScopingTable> childrens = new LinkedHashMap<>();
    private Map<String, String> table = new HashMap<>();

    public ScopingTable getParent() {
        return parent;
    }

    public void setParent(ScopingTable parent) {
        this.parent = parent;
    }


    public ScopingTable(String scopeName) {
        this.scopeName = scopeName;
    }
    public void addVar(String name, String type){
        if (table.containsKey(name)) {
            throw new Error("ERRORE: VARIABILE GIA' DICHIARATA");
        } else {
            table.put(name, type);
        }
    }

    public void addProcedureOrFunction(String name, String type){
        if (table.containsKey(name)) {
            throw new Error("ERRORE: FUNZIONE/PROCEDURA GIA' DICHIARATA");
        } else {
            table.put(name, type);
        }
    }

    public ScopingTable createChildScopingTable(String scopeName) {
        ScopingTable childScopingTable = new ScopingTable(scopeName);
        childScopingTable.setParent(this);
        childrens.put(scopeName, childScopingTable);
        return childScopingTable;
    }

    public ScopingTable getChildScopingTable(String scopeName) {
        if (childrens.containsKey(scopeName)) {
            return childrens.get(scopeName);
        } else {
            throw new Error("ERRORE: TABELLA NON TROVATA");
        }
    }

    public Map<String, String> getTable() {
        return table;
    }
    public String toString() {

        String childrenMapList = "";
        if(childrens!= null && !childrens.isEmpty()){
            for (Map.Entry<String, ScopingTable> entry : childrens.entrySet()) {
                childrenMapList += entry.getValue();
            }
        }
        String orderedTable = "";
        for (Map.Entry<String, String> entry : table.entrySet()) {
            orderedTable += "<"+entry.getKey().toUpperCase()+","+entry.getValue()+">\n";
        }
        if(getParent()!= null){return "-- <ST:"+scopeName.toUpperCase()+" -- PARENT:"+getParent().scopeName.toUpperCase()+"> --\n" + orderedTable+ "\n"+childrenMapList;}
        return "-- <ST:"+scopeName.toUpperCase()+" -- PARENT: NULL> --\n" + orderedTable+ "\n"+childrenMapList;
    }
}
