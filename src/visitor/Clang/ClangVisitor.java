package visitor.Clang;

import nodes.IOArgsNode;
import nodes.Id;
import nodes.Node;
import nodes.StatOP;
import visitor.NodeVisitor;
import visitor.scoping.ScopingTable;
import java.util.*;

public class ClangVisitor implements NodeVisitor<Node> {

    private final CLangUtils Cutils; // Riferimento globale a CLangUtils
    private final ScopingTable scopingTable;
    Map<String, String> table;

    public ClangVisitor(ScopingTable scopingTable) {
        // Riferimento globale a StringBuilder
        StringBuilder file = new StringBuilder(); // Inizializza il file una volta
        this.Cutils = new CLangUtils(file, scopingTable ); // Passa il riferimento del file a CLangUtils
        this.scopingTable=scopingTable;
        this.table = this.scopingTable.getTable();
    }

    @Override
    public Object visitNode(Node node) {
        if (node == null) {
            return null;
        }
        String node_name = node.getValue();

        if(node_name.contains("Iter")){
           for (Node n : node.getChildNodes()) {
                if(n.getValue().equals("VarDeclOP")){
                    for (Node n2 : n.getList1()){
                        if (n2.getValue().equals("DeclsOP")) {
                            if(scopingTable.scopeName.equals("global")){
                                for(int i = 0; i < n2.getList1().size(); i++) {
                                    String varName = n2.getList1().get(i).getValue();
                                    String varType = n2.getList1().get(i).getTYPENODE();
                                    String varValue = null;
                                    if(n2.getList2() != null && n2.getList2().get(i) != null)
                                        varValue = n2.getList2().get(i).getValue();
                                    String value = table.get(varName);
                                    if (value != null){
                                        addGlobalVariable(varName, varType, varValue);
                                    }
                                }
                            }}
                    }
                }
            }
        }
        if (node_name.contains("-")) {
            String TypeNode = node_name.split("-")[0];
            switch (TypeNode) {
                case "Function" -> addFunction(node, scopingTable);
                case "Procedure" -> addProcedure(node, scopingTable);
            }
        }
        visitRemainingNodes(node.getChildNodes(), node.getList1(), node.getList2());
        return null;
    }
    public void addGlobalVariable(String varName, String varType, String varValue) {
        String cType = Cutils.mapTypeToC(varType, false);
        if(cType.equals("char"))
            Cutils.globalVariables.add(cType + " " + varName + "[] = "+"\""+varValue+"\";");

        else if(cType.equals("char*"))
            Cutils.globalVariables.add(cType + " " + varName + " = "+"\""+varValue+"\";");

        else
            Cutils.globalVariables.add(cType + " " + varName + " = "+varValue+";");

    }
    public void addProcedure(Node OP, ScopingTable scopingTable) {

        // Ottieni il nome della procedura
        String procedureName = OP.getValue().split("-")[1];
        CLangUtils.ProcedureData procedureData = new CLangUtils.ProcedureData();
        Node paramsNode = OP.getChildNodes().get(0);


        StringBuilder procedureSignature = new StringBuilder();
        String returnT;

        returnT = procedureName.equals("main") ? "int " : "void ";

        procedureSignature.append(returnT).append(procedureName).append("(");

        Set<String> inputParams = new HashSet<>();
        int parameterIndex = 0; // Contatore per la posizione

        if (paramsNode != null) {
            List<Node> childNodes = paramsNode.getChildNodes();
            List<Node> parameters = paramsNode.getList1();
            System.out.println("ChildNodes: " + childNodes);
            System.out.println("Parameters: " + parameters);

            inputParams = new HashSet<>();
            boolean firstParam = true;
            if (childNodes != null && !childNodes.isEmpty() && parameters != null) {
                for (int i = 0; i < childNodes.size(); i += 2) {
                    String paramName = childNodes.get(i).getValue();
                    String paramType = childNodes.get(i + 1).getValue();
                    boolean isOUT = (parameters.get(i+1).getTYPENODE() != null &&
                            parameters.get(i+1).getTYPENODE().equals("OUT"));
                    // Inseriamo solo se non è già presente
                    if (!inputParams.contains(paramName)) {
                        procedureData.addParameter(paramName, new CLangUtils.ParameterInfo(isOUT, parameterIndex++));
                        inputParams.add(paramName);
                    }
                    if (!firstParam) {
                        procedureSignature.append(", ");
                    }
                    String cType = Cutils.mapTypeToC(paramType, isOUT);
                    procedureSignature.append(cType).append(" ").append(paramName);
                    firstParam = false;
                }
            }

            if (parameters != null && !parameters.isEmpty()) {
                for (int i = 0; i < parameters.size(); i += 2) {
                    String paramType = parameters.get(i).getValue();
                    String paramName = parameters.get(i + 1).getValue();
                    boolean isOUT = (parameters.get(i+1).getTYPENODE() != null &&
                            parameters.get(i+1).getTYPENODE().equals("OUT"));
                    if (!inputParams.contains(paramName)) {
                        procedureData.addParameter(paramName, new CLangUtils.ParameterInfo(isOUT, parameterIndex++));
                        inputParams.add(paramName);
                    }
                    if (!firstParam) {
                        procedureSignature.append(", ");
                    }
                    String cType = Cutils.mapTypeToC(paramType, isOUT);
                    procedureSignature.append(cType).append(" ").append(paramName);
                    firstParam = false;
                }
            }
        }
        Cutils.procVariables.put(procedureName, procedureData);
        procedureSignature.append(") {\n");

        // Ottieni la ScopingTable per la procedura corrente
        ScopingTable procedureScope = scopingTable.getChildScopingTable(procedureName);
        String variableDeclarations = Cutils.generateVariableDeclarationsFromST(procedureScope, inputParams);
        procedureSignature.append(variableDeclarations); // Aggiungi le dichiarazioni delle variabili

        visitNodeRecursively(OP,procedureSignature, procedureScope, false);
        procedureSignature.append("\n }\n\n");
        Cutils.functionBuffer.append(procedureSignature);
    }
    public String generateFunctionCall(Node functionCallNode, ScopingTable st) {
        StringBuilder functionCall = new StringBuilder();
        String functionName = functionCallNode.getValue().split("-")[1];
        List<Node> arguments = functionCallNode.getList1();
        StringBuilder preCallCode = new StringBuilder();

        functionCall.append("\t").append(functionName).append("(");
        if(arguments!= null && !arguments.isEmpty()) {
            for(int i = 0; i < arguments.size(); i++) {
                boolean isOutParam = false;
                Node argNode = arguments.get(i); // Nodo generico

                // Controllo del tipo e casting condizionale
                if(argNode.getClass().getSimpleName().equals("Id")) {
                    Id arg = (Id) argNode; // Cast sicuro a Id
                    isOutParam = arg.getIsOut();
                }

                Node evaluatedArg = Cutils.generateExpression(argNode, st, this); // Usa il nodo originale
                String evaluatedValue = evaluatedArg.getValue();

                String varType = evaluatedArg.getTYPENODE();



                // Gestione concatenazioni stringa (rimane invariata)
                if(evaluatedValue.contains("+")) {
                    String[] parts = evaluatedValue.split("\\+");
                    StringBuilder format = new StringBuilder();
                    List<String> concatArgs = new ArrayList<>();

                    for(String part : parts) {
                        part = part.trim().replace("\"", "");
                        String partType = Cutils.findTypeFromScope(part, st);

                        if(partType != null) {
                            format.append(switch(partType) {
                                case "STRING" -> " %s ";
                                case "INTEGER" -> " %d ";
                                case "REAL" -> " %lf ";
                                default -> " %s ";
                            });
                            concatArgs.add(part);
                        } else {
                            format.append(part.replace("\n", "\\n"));
                        }
                    }

                    preCallCode.append("\tsnprintf(messaggio, sizeof(messaggio), \"")
                            .append(format)
                            .append("\", ")
                            .append(String.join(", ", concatArgs))
                            .append(");\n");

                    evaluatedValue = "messaggio";
                    varType = "STRING";
                }

                if(varType != null) {
                    if(isOutParam && !varType.equals("STRING")) {
                        functionCall.append("&");
                    }
                    functionCall.append(evaluatedValue);
                } else {
                    functionCall.append("\"").append(evaluatedValue).append("\"");
                }

                if(i < arguments.size() - 1) {
                    functionCall.append(", ");
                }
            }}
        functionCall.append(");\n");
        return preCallCode + functionCall.toString();
    }
    private String generateFunctionInAssignment(Node assignNode, ScopingTable st, String leftVar) {

        StringBuilder assignmentStatement = new StringBuilder();
        boolean multiReturn = assignNode.getList1().size() > 1;
        Node rightNode = assignNode.getList2().get(0); // Nodo per la chiamata a funzione o procedura
        String functionName = rightNode.getValue().split("-")[1];


        // Determina se è una chiamata a funzione o procedura
        if (rightNode.getValue().contains("FunCall")) {
            String functionCall = generateFunctionCall(rightNode, st); // Genera la chiamata a funzione
            if(multiReturn) {
                String type = Cutils.mapTypeToC(st.getFunctionReturnType(functionName),false);
                assignmentStatement.append("\t").append(type).append("* temp = ").append(functionCall);
                for (int i = 0; i< assignNode.getList1().size(); i++){
                    assignmentStatement.append("\t").append(assignNode.getList1().get(i).getValue()).append(" = temp[").append(i).append("];\n");
                }
            }
            else{
                String type = st.getFunctionReturnType(functionName);
                if(type.equals("STRING")){
                    if(functionCall.contains("snprintf")){
                        assignmentStatement.append(functionCall);
                        assignmentStatement.append("\tstrcpy(").append(leftVar).append(",").append("messaggio").append(");\n");
                    }
                    else{
                    assignmentStatement.append("\tstrcpy(").append(leftVar).append(",").append(functionCall).append(");\n");}
                }
                else{
                    assignmentStatement.append("\t").append(leftVar).append(" = ").append(functionCall);
                }
            }
        }
        return assignmentStatement.toString();
    }
    private String generateProcedureCall(Node procedureCallNode, ScopingTable st) {
        StringBuilder procedureCall = new StringBuilder();

        // Ottieni il nome della procedura e i suoi dati
        String procedureName = procedureCallNode.getValue().split("-")[1];
        CLangUtils.ProcedureData procedureData = Cutils.procVariables.get(procedureName);


        // Ottieni gli argomenti della chiamata
        List<Node> arguments = procedureCallNode.getList1();
        procedureCall.append("\t").append(procedureName).append("(");

        if(arguments == null){
            return procedureCall + ");\n";
        }

        if (procedureData != null) {
            // Ottieni l'elenco ordinato dei nomi dei parametri
            List<String> paramNames = new ArrayList<>(procedureData.getParameters().keySet());

            for (int i = 0; i < arguments.size(); i++) {
                Node arg = arguments.get(i);
                Node evaluatedArg = Cutils.generateExpression(arg, st, this);
                String evaluatedValue = evaluatedArg.getValue();

                // Controlla se il parametro corrispondente è OUT
                boolean isOutParam = false;
                String varType = Cutils.findTypeFromScope(evaluatedValue, st);

                if(varType == null && evaluatedValue.matches("[+-]?\\d+")){
                    varType = "INTEGER";
                } else if (varType == null && evaluatedValue.matches("[+-]?\\d*\\.\\d+")){
                    varType = "REAL";
                }
                else if (i < paramNames.size()) {
                    CLangUtils.ParameterInfo paramInfo = procedureData.getParameters().get(paramNames.get(i));
                    isOutParam = (paramInfo != null && paramInfo.isOut());
                }

                // Gestione dell'operatore &
                if (isOutParam && varType != null && !varType.equals("STRING")) {
                    procedureCall.append("&");
                }

                // Aggiungi il valore
                if (varType != null) {
                    procedureCall.append(evaluatedValue);
                } else {
                    procedureCall.append("\"").append(evaluatedValue).append("\"");
                }

                // Aggiungi separatore
                if (i < arguments.size() - 1) {
                    procedureCall.append(", ");
                }
            }
        }
        else{
            for (int i = 0; i < arguments.size(); i++) {

                Id arg = (Id) arguments.get(i);
                Node evaluatedArg = Cutils.generateExpression(arg, st, this);
                String evaluatedValue = evaluatedArg.getValue();
                boolean isOutParam = arg.getIsOut();

                String varType = Cutils.findTypeFromScope(evaluatedValue, st);

                if (isOutParam && varType != null && !varType.equals("STRING")) {
                    procedureCall.append("&");
                }
                // Aggiungi il valore
                if (varType != null) {
                    procedureCall.append(evaluatedValue);
                } else {
                    procedureCall.append("\"").append(evaluatedValue).append("\"");
                }

                // Aggiungi separatore
                if (i < arguments.size() - 1) {
                    procedureCall.append(", ");
                }
            }
        }

        procedureCall.append(");\n");
        return procedureCall.toString();
    }
    public String generateIfStatement(Node ifNode, ScopingTable st) {
        Set<String> inputParams = new HashSet<>();
        StringBuilder ifStatement = new StringBuilder();

        // Genera la condizione dell'if
        Node ifCondNode = Cutils.generateExpression(ifNode.getChildNodes().get(0), st, this);
        String condition = Cutils.processCondition(ifCondNode.getValue());
        ifStatement.append("\n    if (").append(condition).append(") {\n");

        // Gestione degli scope annidati
        int ifCounter = st.getIfCounter();
        ScopingTable currentScope = st;
        ScopingTable targetScope = null;

        // Navigazione sicura nella gerarchia esistente
        if(ifCounter > 0) {
            for(int i = 0; i < ifCounter; i++) {
                String scopeName = (i == 0) ?
                        "IfStatOP Body" :
                        "IfStatOP" + i + " Body";

                // Cerca lo scope nella gerarchia corrente
                ScopingTable childScope = currentScope.getChildScopingTable(scopeName);

                if(childScope != null) {
                    // Mantieni il riferimento allo scope trovato
                    targetScope = childScope;
                    // Sposta il currentScope al parent per il prossimo livello
                    currentScope = childScope.getParent();
                } else {
                    // Interrompi se non troviamo lo scope
                    break;
                }
            }
        } else {
            // Caso base per il primo if
            targetScope = st.getChildScopingTable("IfStatOP Body");
        }

        // Fallback allo scope originale se targetScope è null
        if(targetScope == null) {
            targetScope = st;
        }

        // Genera dichiarazioni variabili dallo scope corretto
        String variableDeclarations = Cutils.generateVariableDeclarationsFromST(targetScope, inputParams);
        ifStatement.append(variableDeclarations);

        // Gestione dichiarazioni variabili nel corpo if
        Cutils.processBodyDeclarations(ifNode, ifStatement, targetScope, this);

        // Genera codice per il corpo dell'if
        Node ifBody = ifNode.getChildNodes().get(1);

        ifStatement.append(Cutils.generateAssignments(ifBody, targetScope, this))
                .append("\n\t}");
        // Gestione elif e else
        Cutils.processElifBlocks(ifNode, targetScope, ifStatement, this);
        Cutils.processElseBlock(ifNode, targetScope, ifStatement, this);



        return ifStatement.toString();
    }
    public String generateWhileStatement(Node whileNode, ScopingTable st) {

        StringBuilder whileStatement = new StringBuilder();

        // Genera l'espressione della condizione del while
        Node conditionNode = Cutils.generateExpression(whileNode.getChildNodes().get(0), st, this);
        String condition = Cutils.processCondition(conditionNode.getValue());
        whileStatement.append("    while (").append(condition).append(") {\n");

        st = st.getChildScopingTable("WhileOP Body");
        String variableDeclarations = Cutils.generateVariableDeclarationsFromST(st, new HashSet<>());
        whileStatement.append(variableDeclarations);

        // Itera sul corpo del while (assumendo che i nodi siano in List2 e in ordine invertito)
        Node whileBody = whileNode.getChildNodes().get(1);
        if (whileBody != null) {
            List<Node> bodyChildren = whileBody.getList2();
            Collections.reverse(bodyChildren);
            for (Node child : bodyChildren) {
                visitNodeRecursively(child, whileStatement, st, false);
            }
        }

        whileStatement.append("    }\n");
        return whileStatement.toString();
    }
    public void addFunction(Node OP, ScopingTable scopingTable) {
        // Ottieni il nome della funzione
        String functionName = OP.getValue().split("-")[1]; // "Function-stampa" -> "stampa"

        // DA FARE: Controlla che function non abbia parametri vuoti (es. Function-stampa-)

        Node paramsNode = OP.getChildNodes().get(0); // Supponiamo che il primo nodo figlio sia 'FuncParams'

        // Recupera il tipo di ritorno dal NodeList1

        String returnTypeToy2 = OP.getList1().get(0).getValue(); // Tipo di ritorno
        String cReturnType = Cutils.mapTypeToC(returnTypeToy2, false); // Mappa il tipo di ritorno a C

        StringBuilder functionSignature = new StringBuilder();
        if(OP.getList1().size() != 1){

            functionSignature.append(cReturnType).append("* ").append(functionName).append("(");
        }
        else{
            functionSignature.append(cReturnType).append(" ").append(functionName).append("(");
        }

        Set<String> inputParams = new HashSet<>();
        // Recupera i parametri dai CHILDNODES e NODELIST1

        if(paramsNode != null) {
            List<Node> childNodes = paramsNode.getChildNodes(); // Parametri in CHILDNODES
            List<Node> parameters = paramsNode.getList1(); // Parametri in NODELIST1

            // Aggiungi i parametri da CHILDNODES
            boolean firstParam = true;
            if (childNodes != null && !childNodes.isEmpty()) {
                for (int i = 0; i < childNodes.size(); i += 2) { // Itera sui parametri (nome e tipo)
                    String paramName = childNodes.get(i).getValue();  // Nome del parametro
                    String paramType = childNodes.get(i + 1).getValue(); // Tipo del parametro
                    Boolean isOUT = (childNodes.get(i + 1).getTYPENODE() != null && parameters.get(i + 1).getTYPENODE().equals("OUT"));
                    String cType = Cutils.mapTypeToC(paramType, isOUT); // Mappa il tipo al tipo C corretto
                    inputParams.add(paramName); // Aggiungi il parametro di input all'insieme
                    if (!firstParam) {
                        functionSignature.append(", ");
                    }
                    functionSignature.append(cType).append(" ").append(paramName);
                    firstParam = false;
                }
            }

            // Aggiungi i parametri da NODELIST1
            if (parameters != null && !parameters.isEmpty()) {
                for (int i = 0; i < parameters.size(); i += 2) { // Itera sui parametri (nome e tipo)
                    String paramName = parameters.get(i).getValue(); // Nome del parametro
                    String paramType = parameters.get(i + 1).getValue(); // Tipo del parametro
                    Boolean isOUT = (parameters.get(i + 1).getTYPENODE() != null && parameters.get(i + 1).getTYPENODE().equals("OUT"));
                    String cType = Cutils.mapTypeToC(paramType, isOUT); // Mappa il tipo tenendo conto di "OUT"

                    inputParams.add(paramName); // Aggiungi il parametro di input all'insieme
                    if (!firstParam) {
                        functionSignature.append(", ");
                    }
                    functionSignature.append(cType).append(" ").append(paramName);
                    firstParam = false;
                }
            }
        }

        functionSignature.append(") {\n");

        // Ottieni la ScopingTable per la funzione corrente
        ScopingTable functionScope = scopingTable.getChildScopingTable(functionName);
        String variableDeclarations = Cutils.generateVariableDeclarationsFromST(functionScope, inputParams);
        functionSignature.append(variableDeclarations); // Aggiungi le dichiarazioni delle variabili

        // Visita ricorsivamente i nodi per generare il corpo della funzione
        visitNodeRecursively(OP, functionSignature, functionScope, false);
        functionSignature.append("\n}\n\n");

        // Aggiunge la funzione al file
        Cutils.functionBuffer.append(functionSignature);
    }
    public String generateOutputStatement(Node node, ScopingTable st) {
        StringBuilder outputStatement = new StringBuilder();
        String type = node.getTYPENODE();
        StringBuilder formatString = new StringBuilder();

        if (type != null) {
            // Gestione del caso Return
            if ("Return".equals(type)) {
                if (node.getList1().size() == 1) {
                    Node n = node.getList1().get(0);
                    String childType = n.getTYPENODE();
                    String expr = Cutils.generateExpression(n, st, this).getValue();
                    if (childType != null && childType.equals("STRING") && !expr.contains("temp")) {
                        outputStatement.append("    return \"").append(expr).append("\";\n");
                    } else if (childType != null && childType.equals("STRING") && expr.contains("temp")) {
                        outputStatement.append(expr).append("\nreturn temp;");
                    } else {
                        outputStatement.append("    return ").append(expr).append(";\n");
                    }
                } else {
                    // Caso di return con più valori: allocazione di un array temporaneo
                    String mappedType = Cutils.mapTypeToC(node.getList1().get(0).getTYPENODE(), false);
                    outputStatement.append("\t").append(mappedType)
                            .append(" *temp = (")
                            .append(mappedType)
                            .append("*) malloc( 4 * sizeof(")
                            .append(mappedType)
                            .append("));\n");

                    for (int i = 0; i < node.getList1().size(); i++) {
                        Node n = node.getList1().get(i);
                        StringBuilder expr;
                        String childType = n.getTYPENODE();
                        if (n.getClass().getSimpleName().equals("FunCallOp") || n.getClass().getSimpleName().equals("ProcCallOp")){
                            expr = new StringBuilder(n.getValue().split("-")[1] + "(");
                            for(Node param : n.getList1()){
                                expr.append(param.getValue()).append(",");
                            }
                            expr = new StringBuilder(expr.substring(0, expr.length() - 1));
                            expr.append(")");
                    }
                        else {
                            expr = new StringBuilder(Cutils.generateExpression(n, st, this).getValue());
                        }

                        if (childType != null && childType.equals("STRING")) {
                            outputStatement.append("    strcpy(temp[")
                                    .append(i)
                                    .append("], ")
                                    .append(expr)
                                    .append(");\n");
                        } else {
                            outputStatement.append("    temp[")
                                    .append(i)
                                    .append("] = ")
                                    .append(expr)
                                    .append(";\n");
                        }
                    }
                    outputStatement.append("    return temp;\n");
                }
            }
            // Gestione di WRITE e WRITERETURN
            else if ("WRITE".equals(type) || "WRITERETURN".equals(type)) {
                if (node instanceof StatOP statOP) {
                    IOArgsNode writeNode = statOP.getIOArgsNode();
                    List<Node> exprNodes = writeNode.getExprNodeList();
                    List<Boolean> dollarSigns = writeNode.getDollarSignList();
                    List<String> printfArgs = new ArrayList<>();


                    // Costruzione della stringa di formato e degli argomenti per il printf
                    for (int i = 0; i < exprNodes.size(); i++) {
                        Node exprNode = exprNodes.get(i);
                        boolean isDollar = dollarSigns.get(i);


                        if (exprNode.getValue().contains("FunCall")) {

                            // Se l'espressione è una chiamata a funzione, generiamo il codice ad hoc
                            String[] lines = generateFunctionCall(exprNode, st).split("\n");
                            if (lines.length > 1) {
                                outputStatement.append(lines[0]);
                                // Rimuovo eventuale carattere finale indesiderato
                                String functionCallPart = lines[1].substring(0, lines[1].length() - 1);
                                String funcName = functionCallPart.split("\\(")[0].trim();
                                String format;
                                switch (scopingTable.getFunctionReturnType(funcName)) {
                                    case "INTEGER" -> format = "%d";
                                    case "REAL"    -> format = "%lf";
                                    case "STRING"  -> format = "%s";
                                    default        -> format = "%d";
                                }
                                outputStatement.append("\n\tprintf(\"")
                                        .append(format)
                                        .append("\",")
                                        .append(functionCallPart.trim())
                                        .append(");\n");

                            } else {
                                String format;
                                switch (st.getFunctionReturnType(lines[0].split("\\(")[0].trim())) {
                                    case "INTEGER" -> format = "%d";
                                    case "REAL"    -> format = "%lf";
                                    case "STRING"  -> format = "%s";
                                    default        -> format = "%d";
                                }

                                if(formatString.isEmpty()){
                                outputStatement.append("\n\tprintf(\"")
                                        .append(format)
                                        .append("\",")
                                        .append(lines[0].substring(0, lines[0].length() - 1).trim())
                                        .append(");\n");
                                }else {
                                    formatString.append(format).append("\",")
                                            .append(lines[0].substring(0, lines[0].length() - 1).trim());

                                    }
                            }


                        } else {
                            if (isDollar) {
                                // Gestione di espressioni/variabili con segnaposto
                                Node evaluated = Cutils.generateExpression(exprNode, st, this);
                                String evaluatedType = evaluated.getTYPENODE();
                                if (evaluatedType == null) {
                                    evaluatedType = Cutils.findTypeFromScope(evaluated.getValue(), st);
                                }
                                switch (evaluatedType) {
                                    case "INTEGER" -> formatString.append(" %d ");
                                    case "REAL"    -> formatString.append(" %lf ");
                                    case "STRING"  -> formatString.append(" %s ");
                                    default        -> formatString.append(" %d ");
                                }
                                printfArgs.add(evaluated.getValue());
                            } else {
                                // Gestione di stringhe letterali
                                String literal = exprNode.getValue()
                                        .replace("\"", "\\\"")
                                        .replace("\n", "\\n");
                                formatString.append(literal);
                            }
                        }
                    }

                    // Se si tratta di WRITERETURN, aggiunge uno newline alla fine
                    if ("WRITERETURN".equals(type)) {
                        formatString.append("\\n");
                    }

                    if (!formatString.isEmpty()) {
                        outputStatement.append("\tprintf(\"")
                                .append(formatString);


                        if(!outputStatement.toString().endsWith(")"))
                            outputStatement.append("\"");


                        //System.out.println("#1 "+outputStatement);
                        if (!printfArgs.isEmpty()) {
                            outputStatement.append(", ")
                                    .append(String.join(", ", printfArgs));
                        }
                        //System.out.println("#2 "+outputStatement);




                        outputStatement.append(");\n"); // Chiudi comunque con ';'
                    }

                }
            }
            else {
                return outputStatement.toString();
            }
        }
        return outputStatement.toString();
    }
    public String getFinalCode() {
        Cutils.finalizeFile();
        return Cutils.getGeneratedCode();
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
    void visitNodeRecursively(Node node, StringBuilder signature, ScopingTable st, Boolean assign) {
        if (node == null) return; // Caso base: nodo nullo

        if (signature!= null && signature.toString().contains("return temp;")) {
            return; // Interrompi se abbiamo già generato il return
        }

        Boolean assignInProc = assign;
        // Usa delle liste per raccogliere le istruzioni separatamente
        List<String> initialAssignments = new ArrayList<>();
        List<String> conditionalStatements = new ArrayList<>();
        List<String> otherStatements = new ArrayList<>();



        // Gestisci il nodo corrente in base al suo valore
        if (node.getValue().contains("IfStatOP")) {
            conditionalStatements.add(generateIfStatement(node, st));
        } else if (node.getValue().contains("WhileOP")) {
            conditionalStatements.add(generateWhileStatement(node, st));
        } else if (node.getValue().contains("VarDeclOP")) {
            initialAssignments.add(Cutils.generateVarDeclarations(node)); // Dichiarazioni variabili come assegnamenti iniziali
        } else if (node.getValue().contains("AssignOP")) {
            if (!assignInProc) {
                // Controllo se l'assegnazione include una chiamata a funzione o procedura
                String leftvar = node.getList1().get(0).getValue();
                Node rightSide = node.getList2().get(0); // Supponendo che la parte destra dell'assegnamento sia in List2
                if (rightSide.getValue().contains("FunCall")) {
                    // Gestisce l'assegnazione con una chiamata a funzione/procedura
                    String assignmentCode = generateFunctionInAssignment(node, st, leftvar);
                    initialAssignments.add(assignmentCode);
                } else {
                    // Normale assegnazione senza chiamata a funzione/procedura
                    initialAssignments.add(Cutils.generateAssignments(node,st, this));
                }
            } else {
                assignInProc = false;
            }
        } else if (node.getValue().contains("StatOP")) {
            otherStatements.add(generateOutputStatement(node, st));
            otherStatements.add(Cutils.generateInputStatement((StatOP) node, st));
        } else if (node.getValue().contains("FunCall")) {
            otherStatements.add(generateFunctionCall(node, st));
        } else if (node.getValue().contains("ProcCall")) {
            otherStatements.add(generateProcedureCall(node, st));
        }


        // Aggiungi le assegnazioni iniziali all'inizio
        for (String assignment : initialAssignments) {
            assert signature != null;
            signature.append(assignment);
        }

        // Aggiungi tutte le altre istruzioni condizionali
        for (String statement : conditionalStatements) {
            assert signature != null;
            signature.append(statement);
        }

        // Aggiungi tutte le altre istruzioni
        for (String statement : otherStatements) {
            assert signature != null;
            signature.append(statement);
        }


        // Visita ricorsivamente tutti i figli in ordine inverso
        if (!node.getValue().equals("IfStatOP") && !node.getValue().equals("WhileOP") && !node.getValue().equals("AssignOP")) {
            if (node.getChildNodes() != null && !node.getChildNodes().isEmpty()) {
                for (int i = node.getChildNodes().size() - 1; i >= 0; i--) {
                    visitNodeRecursively(node.getChildNodes().get(i), signature, st, assignInProc);
                }
            }

            // Visita ricorsivamente i nodi in List1 in ordine inverso
            if (node.getList1() != null && !node.getList1().isEmpty()) {
                for (int i = node.getList1().size() - 1; i >= 0; i--) {
                    visitNodeRecursively(node.getList1().get(i), signature, st, assignInProc);
                }
            }
            // Visita ricorsivamente i nodi in List2 in ordine inverso
            if (node.getList2() != null && !node.getList2().isEmpty()) {
                for (int i = node.getList2().size() - 1; i >= 0; i--) {
                    visitNodeRecursively(node.getList2().get(i), signature, st, assignInProc);
                }
            }
        }

    }
}
