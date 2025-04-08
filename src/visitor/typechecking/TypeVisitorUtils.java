package visitor.typechecking;

import nodes.Node;
import visitor.scoping.ScopingTable;

import java.util.*;

public class TypeVisitorUtils {

    private final ScopingTable sc;

    public TypeVisitorUtils(ScopingTable sc) {
        this.sc = sc;
    }

    public void check_Procedure(Node OP) {
        // Salva il nome della procedura
        String procedureName = OP.getValue();

        // Verifica e visita ricorsivamente i nodi
        check_ProcedureRecursive(OP, procedureName);
    }

    private void check_ProcedureRecursive(Node OP, String procedureName) {
        // Verifica se il nodo attuale è uno statOP
        if (OP.getValue().equals("StatOP")) {
            // Controlla il tipo del nodo
            String nodeType = OP.getTYPENODE();

            // Se il tipo è "Return", lancia un errore
            if (nodeType != null && nodeType.equals("Return")) {
                throw new Error("ERRORE: Trovato un'istruzione di ritorno ('Return') all'interno della procedura '"
                        + procedureName + "'.");
            }
        }

        // Visita ricorsivamente i nodi figli
        List<Node> children = OP.getChildNodes();
        List<Node> list1 = OP.getList1();
        List<Node> list2 = OP.getList2();

        // Itera attraverso i nodi figli e chiama ricorsivamente la funzione
        if (children != null && !children.isEmpty()) {
            for (Node child : children) {
                if (child != null) {
                    check_ProcedureRecursive(child, procedureName);  // Chiamata ricorsiva
                }
            }
        }

        // Itera attraverso list1 e chiama ricorsivamente la funzione
        if (list1 != null && !list1.isEmpty()) {
            for (Node child : list1) {
                if (child != null) {
                    check_ProcedureRecursive(child, procedureName);  // Chiamata ricorsiva
                }
            }
        }

        // Itera attraverso list2 e chiama ricorsivamente la funzione
        if (list2 != null && !list2.isEmpty()) {
            for (Node child : list2) {
                if (child != null) {
                    check_ProcedureRecursive(child, procedureName);  // Chiamata ricorsiva
                }
            }
        }

        // Se non ci sono errori, ritorna true
    }

    public void check_Function(Node OP, ScopingTable sc, String node_name) {
        // Salva il nome della funzione
        String functionName = OP.getValue().split("-")[1];
        // Ottieni la scoping table globale
        ScopingTable globalScope = sc.getGlobalScope();

        searchForAssignOP(OP, sc);

        // Recupera la mappa dalla scoping table globale
        Map<String, String> map = globalScope.getTable();

        // Verifica se la funzione esiste nella mappa
        if (!map.containsKey(node_name)) {
            throw new Error("ERRORE: FUNZIONE NON TROVATA: " + functionName);
        }

        // Ottieni i dettagli della funzione dalla scoping table
        String functionDetails = map.get(node_name);


        // Parsing dei dettagli della funzione per ottenere il tipo di ritorno atteso
        String[] parts = functionDetails.split(", ");
        String returnType = parts[1]; // Ottieni la parte "(parametri -> tipo_di_ritorno)"
        returnType = returnType.substring(1, returnType.length() - 1); // rimuove le parentesi
        returnType = returnType.substring(returnType.indexOf("->") + 2).trim(); // Estrarre il tipo di ritorno
        // Verifica e visita ricorsivamente i nodi
        boolean hasValidReturn = check_FunctionRecursive(OP, functionName, returnType,sc);

        // Se non è stato trovato un ritorno valido, lancia un errore
        if (!hasValidReturn) {
            throw new Error("ERRORE: Nessuna istruzione di ritorno valida trovata nella funzione '" + functionName + "'.");
        }


    }

    private String concatenateReturnTypes(List<Node> nodes, ScopingTable scopingTable) {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        StringBuilder concatenatedTypes = new StringBuilder();
        for (Node node : nodes) {
            if (node != null) {
                if (!concatenatedTypes.isEmpty()) {
                    concatenatedTypes.append(",");
                }
                if (node.getTYPENODE() == null) {
                    if(isOperatorNode(node.getValue())){
                        Node result = check_binOP(node,scopingTable);
                        concatenatedTypes.append(result.getTYPENODE());
                    }
                        else{
                        String result  = resolveTypeFromScope(node.getValue(),scopingTable);
                        concatenatedTypes.append(result);
                        }
                    }
                else
                    concatenatedTypes.append(node.getTYPENODE());
                }
            }

        return concatenatedTypes.toString();
        }



    private boolean check_FunctionRecursive(Node OP, String functionName, String expectedReturnType, ScopingTable scopingTable) {
        // Verifica se il nodo attuale è uno statOP

        if (OP.getValue().equals("StatOP")) {
            // Controlla il tipo del nodo
            String nodeType = OP.getTYPENODE();

            // Se il tipo è "Return", controlla il tipo di ritorno
            if (nodeType != null && nodeType.equals("Return")) {
                // Ottieni il tipo di ritorno effettivo dal nodo

                String actualReturnType = concatenateReturnTypes(OP.getList1(),scopingTable);

                if (!actualReturnType.equals(expectedReturnType)) {
                    throw new Error("ERRORE: Tipo di ritorno errato nella funzione '" + functionName + "'. Atteso: '"
                            + expectedReturnType + "', fornito: '" + actualReturnType + "'.");
                }

                // Se il tipo di ritorno è corretto, ritorna true
                return true;
            }
        }

        // Visita ricorsivamente i nodi figli
        List<Node> children = OP.getChildNodes();
        List<Node> list1 = OP.getList1();
        List<Node> list2 = OP.getList2();

        boolean hasValidReturn = false;

        // Itera attraverso i nodi figli e chiama ricorsivamente la funzione
        if (children != null && !children.isEmpty()) {
            for (Node child : children) {
                if (child != null) {
                    hasValidReturn |= check_FunctionRecursive(child, functionName, expectedReturnType,scopingTable);  // Chiamata ricorsiva
                }
            }
        }

        // Itera attraverso list1 e chiama ricorsivamente la funzione
        if (list1 != null && !list1.isEmpty()) {
            for (Node child : list1) {
                if (child != null) {
                    hasValidReturn |= check_FunctionRecursive(child, functionName, expectedReturnType,scopingTable);  // Chiamata ricorsiva
                }
            }
        }

        // Itera attraverso list2 e chiama ricorsivamente la funzione
        if (list2 != null && !list2.isEmpty()) {
            for (Node child : list2) {
                if (child != null) {
                    hasValidReturn |= check_FunctionRecursive(child, functionName, expectedReturnType,scopingTable);  // Chiamata ricorsiva
                }
            }
        }

        // Ritorna true se è stato trovato almeno un ritorno valido
        return hasValidReturn;
    }

    public void check_ParamCall(ScopingTable sc, Node OP, Map<String, Map<String, String>> paramTypeMap, String nodeName) {


        // Recupera la mappa dei tipi per il nodo specifico

        Map<String, String> expectedParamTypes = paramTypeMap.get(nodeName);

  ;
        List<String> expectedParams = new ArrayList<>();

        if (expectedParamTypes == null) {
            throw new Error("No parameters found for node: " + nodeName);
        }

        List<Node> actualParams = OP.getList1();



        for (String key : expectedParamTypes.keySet()) {
            String type = expectedParamTypes.get(key);
            expectedParams.add(type);
        }

        if(expectedParams.size()!= actualParams.size())
            throw new Error("ERROR! Il numero dei parametri della chiamata a procedura non corrispondono a quelli della procedura " + nodeName+ " Attesi:"+ expectedParams.size()+ " Forniti: "+actualParams.size());


        for (int i = 0; i < expectedParams.size(); i++) {
            String expectedType = expectedParams.get(i);
            String actualType = actualParams.get(i).getTYPENODE();

            if (!((expectedType == null && actualType == null) ||
                    (expectedType != null && expectedType.equals(actualType)))) {
                throw new Error("ERROR! I parametri della chiamata a procedura non corrispondono a quelli della procedura: '" + nodeName + "' - Possibile mancanza del carattere @");
            }
        }


        ScopingTable st_current = sc;
        sc = sc.getGlobalScope();

        // Recupera la mappa dalla scoping table
        Map<String, String> map = sc.getTable();

        // Verifica se la funzione/procedura esiste nella mappa
        if (!map.containsKey(nodeName)) {
            throw new Error("ERRORE: FUNZIONE/PROCEDURA NON TROVATA: " + nodeName);
        }

        // Ottieni la stringa associata al node_name
        String functionDetails = map.get(nodeName);

        // Parsing della stringa per estrarre tipo e parametri
        String[] parts = functionDetails.split(", ");
        String parameters = parts[1]; // es: "(INTEGER,REAL,STRING,REAL,INTEGER -> null)"


        // Rimuovi le parentesi e separa i parametri dagli eventuali valori di ritorno
        parameters = parameters.substring(1, parameters.length() - 1); // rimuove le parentesi

        String[] paramsAndReturn = parameters.split(" -> ");
        String paramList = paramsAndReturn[0]; // es: "INTEGER,REAL,STRING,REAL,INTEGER"

        // Spezza i parametri per ottenere una lista di tipi
        String[] paramTypes = paramList.split(",");

        List<Node> callParamTypes = getParameterTypesFromNode(OP, st_current);



        ArrayList<String> l1 = new ArrayList<>() ;
        ArrayList<String> l2 = new ArrayList<>();
        // Verifica i tipi dei parametri
        for (int i = 0; i < paramTypes.length; i++) {
            String expectedType = paramTypes[i].trim();
            l1.add(expectedType);
            String actualValue = callParamTypes.get(i).getValue();
            String actualType = st_current.isDeclared(actualValue).getValue().split(",")[1].strip();
            l2.add(actualType);

        }
        if(!isCompatibleType(l1,l2))
            throw new Error("ERRORE: LA CHIAMATA A PROCEDURA: " + nodeName +" NON RISPETTA I TIPI DEI PARAMETRI\n TIPI ATTESI: "+l1+"\n TIPI FORNITI: "+l2);
    }

    public void check_FunCall(Node OP, ScopingTable sc, String node_name) {
        // ScopingTable parentsc = sc.getParent();
        // Ottieni la scoping table globale
        ScopingTable st_current = sc;
        sc = sc.getGlobalScope();

        // Recupera la mappa dalla scoping table
        Map<String, String> map = sc.getTable();

        // Verifica se la funzione/procedura esiste nella mappa
        if (!map.containsKey(node_name)) {
            throw new Error("ERRORE: FUNZIONE/PROCEDURA NON TROVATA: " + node_name);
        }

        // Ottieni la stringa associata al node_name
        String functionDetails = map.get(node_name);

        // Parsing della stringa per estrarre tipo e parametri
        String[] parts = functionDetails.split(", ");
        String type = parts[0]; // es: "PROCEDURE" o "FUNCTION"
        String parameters = parts[1]; // es: "(INTEGER,REAL,STRING,REAL,INTEGER -> null)"

        // Rimuovi le parentesi e separa i parametri dagli eventuali valori di ritorno
        parameters = parameters.substring(1, parameters.length() - 1); // rimuove le parentesi
        String[] paramsAndReturn = parameters.split(" -> ");
        String paramList = paramsAndReturn[0]; // es: "INTEGER,REAL,STRING,REAL,INTEGER"
        String returnType = paramsAndReturn[1]; // es: "null" o "STRING"

        // Spezza i parametri per ottenere una lista di tipi
        String[] paramTypes = paramList.split(",");


        // Ottieni il numero e i tipi di parametri dalla chiamata corrente (OP)
        // Qui assumiamo che OP abbia metodi per ottenere i parametri della chiamata
        List<Node> callParamTypes = getParameterTypesFromNode(OP, st_current);


        // Verifica il numero di parametri
        if (paramTypes.length != callParamTypes.size()) {
            throw new Error("ERRORE: NUMERO DI PARAMETRI ERRATO PER LA FUNZIONE: " + node_name);
        }

        // Verifica i tipi dei parametri
        for (int i = 0; i < paramTypes.length; i++) {
            String expectedType = paramTypes[i].trim();
            String actualType = callParamTypes.get(i).getTYPENODE();

        }



        // Se tutto è corretto, ritorna true
    }

    private void searchForAssignOP(Node OP, ScopingTable scopingTable) {
        List<Node> children = OP.getChildNodes();
        List<Node> list1 = OP.getList1();
        List<Node> list2 = OP.getList2();

        // Itera attraverso i nodi figli e chiama ricorsivamente la funzione
        if (children != null && !children.isEmpty()) {
            for (Node child : children) {
                if (child != null) {
                    if (child.getValue().equals("AssignOP")) {
                        check_assignOP(child, scopingTable);
                    }
                    searchForAssignOP(child, scopingTable);  // Chiamata ricorsiva
                }
            }
        }

        // Itera attraverso list1 e chiama ricorsivamente la funzione
        if (list1 != null && !list1.isEmpty()) {
            for (Node child : list1) {
                if (child != null) {
                    if (child.getValue().equals("AssignOP")) {
                        check_assignOP(child, scopingTable);
                    }
                    searchForAssignOP(child, scopingTable);  // Chiamata ricorsiva
                }
            }
        }

        // Itera attraverso list2 e chiama ricorsivamente la funzione
        if (list2 != null && !list2.isEmpty()) {
            for (Node child : list2) {
                if (child != null) {
                    if (child.getValue().equals("AssignOP")) {
                        check_assignOP(child, scopingTable);
                    }
                    searchForAssignOP(child, scopingTable);  // Chiamata ricorsiva
                }
            }
        }
    }

    private List<Node> getParameterTypesFromNode(Node OP, ScopingTable sc) {


        // Se la lista dei parametri è null, ritorna una lista vuota
        List<Node> paramList = OP.getList1();
        List<Node> newParamList = new ArrayList<>(); // Inizializza la nuova lista

        if (paramList != null) {
            for (Node n : paramList) {
                String node_name = n.getValue();
                // Verifica se il nodo è un'operazione binaria
                if (node_name.equals("PlusOP") || node_name.equals("TimesOP") ||
                        node_name.equals("MinusOP") || node_name.equals("DivOP")) {

                    // Controlla e aggiorna il nodo con il tipo corretto
                    n = check_binOP(n, sc);
                }
                // Aggiungi il nodo alla nuova lista
                newParamList.add(n);
            }
        }

        // Ritorna la nuova lista di parametri
        return newParamList;
    }


    public void check_whileOP (Node OP){


    OP.setTYPENODE("NOTYPE");
}

    public void check_ifStatOP (Node OP){

        List<Node> children = OP.getChildNodes();
        List<Node> list1 = OP.getList1();

        // Controlla che il primo nodo di children sia BOOLEAN
        if (!children.isEmpty() && children.get(0).getTYPENODE()!= null && !children.get(0).getTYPENODE().equals("BOOLEAN")) {
            throw new Error("ERRORE DI TIPO: "+ OP.getValue());
        }

        // Controlla i nodi successivi di children se presenti
        for (int i = 1; i < children.size(); i++) {
            if (children.get(i) != null && !children.get(i).getTYPENODE().equals("NOTYPE")) {
                throw new Error("ERRORE DI TIPO: " + OP.getValue());
            }
        }

        // Controlla i nodi in list1 se non è null
        if (list1 != null) {
            for (Node node : list1) {
                if (node != null && !node.getTYPENODE().equals("NOTYPE")) {
                    throw new Error("ERRORE DI TIPO: " + OP.getValue());
                }
            }
        }
        OP.setTYPENODE("NOTYPE");
    }

    public void check_assignOP(Node OP, ScopingTable scopingTable) {

        List<Node> list1 = OP.getList1();
        List<Node> list2 = OP.getList2();

        String type1 = null;
        ArrayList<String> typelist1 = new ArrayList<>();
        ArrayList<String> typelist2 = new ArrayList<>();



        // Verifica i tipi per list1
        if (list1 != null && !list1.isEmpty()) {
            for (Node n : list1) {
                if (n != null) {
                    Map.Entry<String, String> entry = scopingTable.isDeclared(n.getValue());
                    if (entry != null)
                        type1 = entry.getValue().split(",")[1].trim();
                    else
                        throw new Error("ERRORE: VARIABILE NON DICHIARATA: '" + n.getValue()+ "' ALL'INTERNO DELLA FUNZIONE: "+scopingTable.scopeName);
                    typelist1.add(type1);
                }
            }
        }

        // Verifica se list2 contiene un solo elemento che è una chiamata a funzione
        if (list2 != null && list2.size() == 1 && list2.get(0).getValue().startsWith("FunCall")) {
            Node functionCallNode = list2.get(0);
            String functionName = extractFunctionName(functionCallNode.getValue());
            // Controlla la funzione e ottieni i tipi di ritorno
            List<String> returnTypes = getFunctionReturnTypes(functionCallNode, scopingTable, functionName);
            // Aggiungi i tipi di ritorno alla lista dei tipi di list2
            typelist2.addAll(returnTypes);
        } else if (list2 != null && !list2.isEmpty()) {

            for (Node n : list2) {
                if (n != null) {

                    Map.Entry<String, String> entry = scopingTable.isDeclared(n.getValue());
                    String type2 = null;
                    if (entry != null)
                        type1 = entry.getValue().split(",")[1].trim();
                    else
                        type2 = n.getTYPENODE();

                    if(type2!= null )
                        typelist2.add(type2);
                    else
                        typelist2.add(type1); 
                }
            }
        }

        // Controlla se i tipi sono compatibili
        if (!isCompatibleType(typelist1, typelist2)) {
            assert list1 != null;
            assert list2 != null;
            throw new Error("Syntax Error: ERRORE ASSEGNAMENTO VARIABILI: I TIPI NON SONO COMPATIBILI."+ OP.printList1() + "-" + OP.printList2()+" "+typelist1+"-"+typelist2);
        }
    }

    private List<String> getFunctionReturnTypes(Node functionCallNode, ScopingTable scopingTable, String functionName) {
        // Ottieni la scoping table globale
        ScopingTable globalScope = scopingTable.getGlobalScope();
        Map<String, String> map = globalScope.getTable();

        if (!map.containsKey(functionName)) {
            throw new Error("ERRORE: FUNZIONE NON TROVATA: " + functionName);
        }

        // Ottieni i dettagli della funzione
        String functionDetails = map.get(functionName);
        String[] parts = functionDetails.split(", ");
        String type = parts[0]; // PROCEDURE o FUNCTION
        String parameters = parts[1]; // (INTEGER,REAL,STRING -> REAL,STRING)

        // Parsing dei parametri e dei tipi di ritorno
        parameters = parameters.substring(1, parameters.length() - 1); // Rimuove le parentesi
        String[] paramsAndReturn = parameters.split(" -> ");
        String returnTypeList = paramsAndReturn[1]; // Tipi di ritorno, es: "REAL,STRING"

        // Spezza i tipi di ritorno per ottenere una lista
        return Arrays.asList(returnTypeList.split(","));
    }

    private String extractFunctionName(String funCallValue) {
        // Estrai il nome della funzione rimuovendo la parte "FunCall-"
        return funCallValue.substring(funCallValue.indexOf('-') + 1);
    }


    private boolean isCompatibleType(ArrayList<String> list1, ArrayList<String> list2) {

        // Verifica se le liste hanno la stessa lunghezza
        if (list1.size() != list2.size()) {
            return false;
        }

        // Confronta i tipi degli elementi corrispondenti nelle due liste
        for (int i = 0; i < list1.size(); i++) {
            String type1 = list1.get(i);
            String type2 = list2.get(i);

            // Se i tipi non corrispondono, le liste non sono compatibili
            if (!type1.equals(type2)) {
                return false;
            }
        }

        // Se tutte le coppie di tipi corrispondono, le liste sono compatibili
        return true;
    }

    public Node check_relOP(Node OP, ScopingTable scopingTable) {
        List<Node> children = OP.getChildNodes();
        String operator = OP.getValue();

        if (children.size() != 2)
            throw new IllegalArgumentException("ERRORE: L'operatore " + operator + " richiede due operandi");

        String type1 = children.get(0).getTYPENODE();
        String type2 = children.get(1).getTYPENODE();
        String id1 = children.get(0).getValue();
        String id2 = children.get(1).getValue();

        // Verifica se uno dei nodi figli è un operatore
        if (isOperatorNode(type1)) {
            // Se il primo nodo è un operatore, chiama ricorsivamente il metodo su di esso
            Node childOP = check_relOP(children.get(0), scopingTable);
            type1 = childOP.getTYPENODE();
        }

        if (isOperatorNode(type2)) {
            // Se il secondo nodo è un operatore, chiama ricorsivamente il metodo su di esso
            Node childOP = check_relOP(children.get(1), scopingTable);
            type2 = childOP.getTYPENODE();
        }

        Map.Entry<String, String> entry = scopingTable.isDeclared(id1);
        if (entry != null)
            type1 = entry.getValue().split(",")[1].trim();
        entry = scopingTable.isDeclared(id2);
        if (entry != null)
            type2 = entry.getValue().split(",")[1].trim();

        // Controlla i tipi degli operandi in base all'operatore
        String resultType = "BOOLEAN"; // Default: il risultato di un operatore relazionale è sempre BOOLEAN

        switch (operator) {
            case "LTOP", "LEOP", "GTOP", "GEOP" -> {
                if ((type1.equals("INTEGER") && type2.equals("INTEGER")) ||
                        (type1.equals("INTEGER") && type2.equals("REAL")) ||
                        (type1.equals("REAL") && type2.equals("INTEGER")) ||
                        (type1.equals("REAL") && type2.equals("REAL"))) {
                    // I tipi degli operandi sono compatibili
                    resultType = "BOOLEAN";
                } else {
                    throw new Error("ERRORE DI TIPO: Operandi non validi per l'operatore " + operator);
                }
            }
        }

        // Imposta il tipo del risultato
        OP.setTYPENODE(resultType);
        return OP;
    }

    public Node check_binOP(Node OP, ScopingTable scopingTable) {

        List<Node> children = OP.getChildNodes();
        String operator = OP.getValue();

        // Verifica che l'operatore binario abbia esattamente due operandi
        if (children.size() != 2) {
            throw new IllegalArgumentException("ERRORE: L'operatore " + operator + " richiede due operandi");
        }

        // Ottieni i tipi dei due operandi
        String type1 = children.get(0).getTYPENODE();
        String type2 = children.get(1).getTYPENODE();

        String id1 = children.get(0).getValue();
        String id2 = children.get(1).getValue();


        // Ricorsione per risolvere il tipo del primo operando se è un'operazione
        if (type1 == null) {
            if (isOperatorNode(id1)) {
                Node childOP = check_binOP(children.get(0), scopingTable);
                type1 = childOP.getTYPENODE();
            }
            else
                type1 = resolveTypeFromScope(id1, scopingTable);

            }

        // Ricorsione per risolvere il tipo del secondo operando se è un'operazione
        if (type2 == null) {
            if (isOperatorNode(id2)) {
                Node childOP = check_binOP(children.get(1), scopingTable);
                type2 = childOP.getTYPENODE();
            }
            else
                type2 = resolveTypeFromScope(id2, scopingTable);
        }



        // Controlla i tipi degli operandi in base all'operatore
        String resultType = "";

        switch (operator) {
            case "PlusOP" -> {
                if (type1.equals("INTEGER") && type2.equals("INTEGER")) {
                    resultType = "INTEGER";
                } else if ((type1.equals("INTEGER") && type2.equals("REAL")) ||
                        (type1.equals("REAL") && type2.equals("INTEGER")) ||
                        (type1.equals("REAL") && type2.equals("REAL"))) {
                    resultType = "REAL";
                } else if (type1.equals("STRING") && (type2.equals("STRING") || type2.equals("REAL") || type2.equals("INTEGER"))) {
                    resultType = "STRING";
                } else {
                    throw new Error("ERRORE DI TIPO: Operandi non validi per l'operatore PlusOP: TIPO1:" + type1 + " TIPO2:" + type2);
                }
            }
            case "TimesOP" -> {
                if (type1.equals("INTEGER") && type2.equals("INTEGER")) {
                    resultType = "INTEGER";
                } else if ((type1.equals("INTEGER") && type2.equals("REAL")) ||
                        (type1.equals("REAL") && type2.equals("INTEGER")) ||
                        type1.equals("REAL") && type2.equals("REAL")) {
                    resultType = "REAL";
                } else {
                    throw new Error("ERRORE DI TIPO: Operandi non validi per l'operatore TimesOP: TIPO1:" + type1 + " TIPO2:" + type2);
                }
            }
            case "MinusOP" -> {
                if (type1.equals("INTEGER") && type2.equals("INTEGER")) {
                    resultType = "INTEGER";
                } else if (type1.equals("REAL") && type2.equals("REAL")) {
                    resultType = "REAL";
                } else {
                    throw new Error("ERRORE DI TIPO: Operandi non validi per l'operatore MinusOP: TIPO1:" + type1 + " TIPO2:" + type2);
                }
            }
            case "NotOP" -> {
                if (type1.equals("BOOLEAN") && type2.equals("BOOLEAN")) {
                    resultType = "BOOLEAN";
                } else {
                    throw new Error("ERRORE DI TIPO: Operandi non validi per l'operatore NotOP: TIPO1:" + type1 + " TIPO2:" + type2);
                }
            }
            default -> throw new Error("ERRORE: Operatore non riconosciuto: " + operator);
        }

        // Imposta il tipo del risultato nell'operazione corrente
        OP.setTYPENODE(resultType);
        return OP;
    }

    // Metodo per verificare se un nodo è un operatore
    private boolean isOperatorNode(String type) {
        return type != null && (type.equals("PlusOP") || type.equals("TimesOP") || type.equals("MinusOP") || type.equals("NotOP") ||
                type.equals("AndOp") || type.equals("EqOP") || type.equals("GeOP") || type.equals("GtOP")|| type.equals("LeOP")|| type.equals("LtOP")|| type.equals("NeOP")|| type.equals("OrOP"));
    }

    // Funzione per risolvere il tipo risalendo nelle tabelle scope
    private String resolveTypeFromScope(String id, ScopingTable scopingTable) {

        if (id.contains("FunCall")) {
            String functionName = extractFunctionName(id);
            Map.Entry<String, String> entry = scopingTable.isDeclared(functionName);
            if (entry != null) {
                String details = entry.getValue();
                int arrowIndex = details.indexOf("->");
                if (arrowIndex != -1) {
                    String returnType = details.substring(arrowIndex + 2).trim();
                        returnType = returnType.substring(0, returnType.length() - 1).trim();
                    return returnType;
                }
            }
        }

        while (scopingTable != null) {
            Map.Entry<String, String> entry = scopingTable.isDeclared(id);
            if (entry != null) {
                return entry.getValue().split(",")[1].trim();
            }
            scopingTable = scopingTable.getParent(); // Risali alla tabella padre
        }
        return null; // Se non trovato in nessuna tabella
    }

}
