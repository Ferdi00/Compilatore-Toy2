package scoping;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScopingTable {

    private final String scopeName;

    private ScopingTable parent;

    private Map<String, ScopingTable> childrens = new LinkedHashMap<>();
    private Map<String, String> vars = new HashMap<>();


    public ScopingTable(String scopeName) {
        this.scopeName = scopeName;
    }
    public void addVar(String name, String type){
        if (vars.containsKey(name)) {
            System.out.println("ERRORE: VARIABILE GIA' DICHIARATA");
        } else {
            vars.put(name, type);
        }
    }

    public void addProcedure(String name, String type){
        if (vars.containsKey(name)) {
            System.out.println("ERRORE: VARIABILE GIA' DICHIARATA");
        } else {
            vars.put(name, type);
        }
    }

    public String toString() {
        return "SCOPINGTABLE:"+scopeName+" VARS:"+vars;
    }
}
