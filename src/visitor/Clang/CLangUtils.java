package visitor.Clang;
import nodes.IOArgsNode;
import nodes.Node;
import nodes.StatOP;
import visitor.scoping.ScopingTable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.*;

public class CLangUtils {

    private final StringBuilder file;
    private final ScopingTable scopingTable;
    final List<String> globalVariables = new ArrayList<>();

    final Map<String, ProcedureData> procVariables = new LinkedHashMap<>();

    final StringBuilder functionBuffer = new StringBuilder();
    private boolean initialized = false;

    public CLangUtils(StringBuilder file, ScopingTable scopingTable) {
        this.file = file;
        this.scopingTable = scopingTable;
    }
    public void finalizeFile() {
        if (!initialized) {
            StringBuilder finalOutput = new StringBuilder();

            // Intestazioni
            finalOutput.append("#include <stdio.h>\n")
                    .append("#include <stdlib.h>\n").append("#include <stdbool.h>\n")
                    .append("#include <string.h>\n\n")
                    .append("char messaggio[150];\n");

            // Variabili globali
            if (!globalVariables.isEmpty()) {

                globalVariables.forEach(decl -> finalOutput.append(decl).append("\n"));
                finalOutput.append("\n");
            }

            // Prototipi delle funzioni
            finalOutput.append("// Function prototypes\n");
            for (Map.Entry<String, String> entry : scopingTable.getTable().entrySet()) {
                String[] metadata = entry.getValue().split(",", 2);
                if (metadata.length < 2) continue;

                String type = metadata[0].trim();
                if (!type.equals("FUNCTION") && !type.equals("PROCEDURE")) continue;

                String funcName = entry.getKey();
                String signature = metadata[1].trim()
                        .replaceAll("[()]", "")
                        .replaceAll("\\s+", "");

                String[] signatureParts = signature.split("->");
                String paramsPart = signatureParts[0].trim();
                String returnPart = signatureParts.length > 1 ?
                        signatureParts[1].trim() :
                        "null";


                List<String> cParams = new ArrayList<>();
                ProcedureData procData = procVariables.get(funcName);
                if (!paramsPart.equalsIgnoreCase("null") && !paramsPart.isEmpty()) {
                    String[] params = paramsPart.split(",");

                    if (procData != null) {
                        List<String> paramNames = new ArrayList<>(procData.getParameters().keySet());

                        for (int i = 0; i < params.length; i++) {
                            String paramType = params[i].trim();
                            String paramName = paramNames.get(i);
                            ParameterInfo paramInfo = procData.getParameters().get(paramName);
                            boolean isOut = (paramInfo != null && paramInfo.isOut());
                            String cType = mapTypeToC(paramType, isOut);
                            cParams.add(cType);
                        }
                    } else {
                        for (String param : params) {
                            String cType = mapTypeToC(param.trim(), false);
                            cParams.add(cType);
                        }
                    }
                }

                String cReturnType = funcName.equals("main") ? " int" : getString(returnPart);

                // Costruzione prototipo
                finalOutput.append(cReturnType)
                        .append(" ")
                        .append(funcName)
                        .append("(")
                        .append(String.join(", ", cParams))
                        .append(");\n");
            }
            finalOutput.append("\n");

            // Funzioni e procedure
            finalOutput.append(functionBuffer);

            file.setLength(0);
            file.append(finalOutput);
            initialized = true;
        }
    }

    private String getString(String returnPart) {
        String cReturnType;
        if (returnPart.equals("null")) {
            cReturnType = "void";
        }
        else {
            List<String> returnTypes = Arrays.asList(returnPart.split(","));
            if (returnTypes.size() > 1) {
                // Caso multipli valori di ritorno
                String firstType = returnTypes.get(0).trim();
                cReturnType = mapTypeToC(firstType, false) + "*";
            }
            else {
                // Caso singolo valore di ritorno
                cReturnType = mapTypeToC(returnPart.trim(), false);
            }
        }
        return cReturnType;
    }

    public String getGeneratedCode() {
        return file.toString();
    }
    String generateInputStatement(StatOP inputNode, ScopingTable st) {
        StringBuilder inputStatement = new StringBuilder();
        StringBuilder formatString = new StringBuilder();
        StringBuilder scanfFormatString = new StringBuilder();
        List<String> scanfVariables = new ArrayList<>();
        String type = inputNode.getTYPENODE();
        IOArgsNode ioArgsNode = inputNode.getIOArgsNode();

        if (type != null && type.equals("READ") && ioArgsNode != null) {
            List<Node> exprNodes = ioArgsNode.getExprNodeList();
            List<Boolean> dollarSigns = ioArgsNode.getDollarSignList();

            // Verifica consistenza tra exprNodes e dollarSigns
            if (exprNodes.size() != dollarSigns.size()) {
                throw new Error("Errore: exprNodeList e dollarSignList hanno dimensioni diverse");
            }

            for (int i = 0; i < exprNodes.size(); i++) {
                Node exprNode = exprNodes.get(i);
                boolean needsAmpersand = dollarSigns.get(i);

                if (exprNode.getClass().getSimpleName().equals("StringConst")) {
                    // Aggiungi stringa al prompt
                    formatString.append(exprNode.getValue().replace("\"", "\\\""));
                }
                else if (exprNode.getClass().getSimpleName().equals("Id")) {
                    // Processa variabile per scanf
                    String varName = exprNode.getValue();
                    String varType = findTypeFromScope(varName, st);

                    if (varType == null) {
                        throw new Error("Variabile non dichiarata: " + varName);
                    }

                    // Aggiungi segnaposto per scanf
                    switch (varType) {
                        case "INTEGER" -> scanfFormatString.append("%d ");
                        case "REAL" -> scanfFormatString.append("%lf ");
                        case "STRING" -> scanfFormatString.append("%s ");
                        case "BOOLEAN" -> scanfFormatString.append("%d ");
                        default -> throw new Error("Tipo non supportato per input: " + varType);
                    }

                    // Aggiungi variabile con eventuale &
                    String formattedVar = needsAmpersand && !varType.equals("STRING") ?
                            "&" + varName : varName;
                    scanfVariables.add(formattedVar);
                }
            }

            // Costruisci printf per il prompt
            if (!formatString.isEmpty()) {
                inputStatement.append("\n\tprintf(\"")
                        .append(formatString)
                        .append("\");\n");
            }

            // Costruisci scanf per l'input
            if (!scanfFormatString.isEmpty()) {
                inputStatement.append("    scanf(\"")
                        .append(scanfFormatString.toString().trim())
                        .append("\", ");

                inputStatement.append(String.join(", ", scanfVariables))
                        .append(");\n");
            }
        }

        return inputStatement.toString();
    }
    // Funzione per generare le istruzioni di assegnamento

    // Funzione per generare le espressioni (es. per PlusOP)
    Node generateExpression(Node node, ScopingTable st, ClangVisitor visitor) {

        Node resultNode = new Node();

        if (node.getValue().equals("ParOP")) {
            return generateExpression(node.getChildNodes().get(0), st, visitor);
        }

        if (node.getValue().contains("FunCall")) {
            String funcName = node.getValue().split("-")[1];
            String callCode = visitor.generateFunctionCall(node, st);
            callCode = callCode.replaceAll("\\s+", "");
            if (callCode.endsWith(";")) {
                callCode = callCode.substring(0, callCode.length() - 1); // Rimuovi ; finale
            }
            resultNode.setValue(callCode);
            resultNode.setTYPENODE(st.getFunctionReturnType(funcName));
            return  resultNode;
        }

        // Gestione dei diversi operatori
        switch (node.getValue()) {
            case "PlusOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st, visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st, visitor);

                String leftType = leftNode.getTYPENODE();
                String rightType = rightNode.getTYPENODE();

                if (leftType == null || rightType == null) {
                    leftType = findTypeFromScope(leftNode.getValue(), st);
                    rightType = findTypeFromScope(rightNode.getValue(), st);
                }

                if ("STRING".equals(leftType) && "STRING".equals(rightType)) {
                    String expression = "char* temp = malloc(strlen(" + leftNode.getValue() + ") + strlen(" + rightNode.getValue() + ") + 1);" +
                            "\nstrcpy(temp, " + leftNode.getValue() + ");" +
                            "\nstrcat(temp, " + rightNode.getValue() + ");";
                    resultNode.setValue(expression);
                    resultNode.setTYPENODE("STRING");
                } else {
                    String expression = leftNode.getValue() + " + " + rightNode.getValue();
                    resultNode.setValue(expression);
                    if ("REAL".equals(leftType) || "REAL".equals(rightType)) {
                        resultNode.setTYPENODE("REAL");
                    } else {
                        resultNode.setTYPENODE("INTEGER");
                    }
                }
                break;
            }

            case "MinusOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st,visitor);
                String expression = leftNode.getValue() + " - " + rightNode.getValue(); // Combina le parti con -
                resultNode.setValue(expression);

                String leftType = leftNode.getTYPENODE();
                String rightType = rightNode.getTYPENODE();

                if (leftType == null || rightType == null) {
                    leftType = findTypeFromScope(leftNode.getValue(), st);
                    rightType = findTypeFromScope(rightNode.getValue(), st);
                }

                if ("REAL".equals(leftType) || "REAL".equals(rightType)) {
                    resultNode.setTYPENODE("REAL");
                } else {
                    resultNode.setTYPENODE("INTEGER");
                }
                break;
            }

            case "TimesOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st,visitor);
                String expression = leftNode.getValue() + " * " + rightNode.getValue(); // Combina le parti con *
                resultNode.setValue(expression);

                String leftType = leftNode.getTYPENODE();
                String rightType = rightNode.getTYPENODE();

                if (leftType == null || rightType == null) {
                    leftType = findTypeFromScope(leftNode.getValue(), st);
                    rightType = findTypeFromScope(rightNode.getValue(), st);
                }

                if ("REAL".equals(leftType) || "REAL".equals(rightType)) {
                    resultNode.setTYPENODE("REAL");
                } else {
                    resultNode.setTYPENODE("INTEGER");
                }
                break;
            }

            case "DivOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st,visitor);
                String expression = leftNode.getValue() + " / " + rightNode.getValue(); // Combina le parti con /
                resultNode.setValue(expression);
                resultNode.setTYPENODE("REAL");
                break;
            }

            case "GtOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st,visitor);
                String expression = formatOperand(leftNode, st) + " > " + formatOperand(rightNode, st);
                resultNode.setValue(expression);
                resultNode.setTYPENODE("BOOLEAN");
                break;
            }

            case "LtOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st,visitor);
                String expression = formatOperand(leftNode, st) + " < " + formatOperand(rightNode, st);
                resultNode.setValue(expression);
                resultNode.setTYPENODE("BOOLEAN");
                break;
            }

            case "GeOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st,visitor);
                String expression = formatOperand(leftNode, st) + " >= " + formatOperand(rightNode, st);
                resultNode.setValue(expression);
                resultNode.setTYPENODE("BOOLEAN");
                break;
            }

            case "LeOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st,visitor);
                String expression = formatOperand(leftNode, st) + " <= " + formatOperand(rightNode, st);
                resultNode.setValue(expression);
                resultNode.setTYPENODE("BOOLEAN");
                break;
            }

            case "EqOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st,visitor);
                String expression;
                if (leftNode.getTYPENODE().equals("STRING") || rightNode.getTYPENODE().equals("STRING")) {
                    expression = "strcmp(" + formatOperand(leftNode, st) + ", " + formatOperand(rightNode, st) + ") == 0";
                } else {
                    expression = formatOperand(leftNode, st) + " == " + formatOperand(rightNode, st);
                }
                resultNode.setValue(expression);
                resultNode.setTYPENODE("BOOLEAN");
                break;
            }

            case "NeOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st,visitor);
                String expression;
                if (leftNode.getTYPENODE().equals("STRING") || rightNode.getTYPENODE().equals("STRING")) {
                    expression = "strcmp(" + formatOperand(leftNode, st) + ", " + formatOperand(rightNode, st) + ") != 0";
                } else {
                    expression = formatOperand(leftNode, st) + " != " + formatOperand(rightNode, st);
                }
                resultNode.setValue(expression);
                resultNode.setTYPENODE("BOOLEAN");
                break;
            }

            case "AndOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st,visitor);
                String expression = formatOperand(leftNode, st) + " && " + formatOperand(rightNode, st);
                resultNode.setValue(expression);
                resultNode.setTYPENODE("BOOLEAN");
                break;
            }

            case "OrOP": {
                Node leftNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                Node rightNode = generateExpression(node.getChildNodes().get(1), st,visitor);
                String expression = formatOperand(leftNode, st) + " || " + formatOperand(rightNode, st);
                resultNode.setValue(expression);
                resultNode.setTYPENODE("BOOLEAN");
                break;
            }

            case "NotOP": {
                Node childNode = generateExpression(node.getChildNodes().get(0), st,visitor);
                String expression = "!" + formatOperand(childNode, st);
                resultNode.setValue(expression);
                resultNode.setTYPENODE("BOOLEAN");
                break;
            }

            default:
                // Caso base: se è una variabile o un valore letterale
                resultNode.setValue(node.getValue());
                String type = node.getTYPENODE();
                if (type == null) {
                    type = findTypeFromScope(node.getValue(), st);
                }
                resultNode.setTYPENODE(type);
                break;
        }


        return resultNode;
    }

    // Funzione di supporto per formattare l'operando come stringa o variabile
    private String formatOperand(Node node, ScopingTable st) {
        String value = node.getValue();
        String type = node.getTYPENODE();

        // Se non è una variabile, ma una stringa letterale, aggiungi le virgolette
        if (findTypeFromScope(value, st) == null && "STRING".equals(type)) {
            return "\"" + value + "\"";
        }

        return value; // Restituisci il valore originale per le variabili
    }
    String findTypeFromScope(String varName, ScopingTable st) {

        // Ottieni la tabella delle variabili per l'ambito corrente
        Map<String, String> variables = st.getTable();

        // Verifica se la variabile è presente nell'ambito corrente
        if (variables.containsKey(varName)) {
            String entry = variables.get(varName);

            if(!entry.contains("FUNCTION") && !entry.contains("PROCEDURE")) {
                String varTypeFull = variables.get(varName);
                return varTypeFull.split(",")[1].strip(); // Estrai il tipo dalla dichiarazione
            }
        } else if (st.getParent() != null) { // Se non è presente, controlla l'ambito genitore
            return findTypeFromScope(varName, st.getParent());
        }

        return null; // Tipo non trovato in nessuno degli ambiti
    }
    void processBodyDeclarations(Node ifNode, StringBuilder signature, ScopingTable st, ClangVisitor visitor) {
            for(Node child : ifNode.getChildNodes()) {
                if(child != null && child.getValue().equals("BodyOP")) {
                    visitor.visitNodeRecursively(child, signature,st,false);
                }
            }

    }
    void processElifBlocks(Node ifNode, ScopingTable parentScope, StringBuilder ifStatement, ClangVisitor visitor) {
        int elifCounter = 0;

        if(ifNode.getList1() != null){
        for(Node child : ifNode.getList1()) {
            if(child.getValue().contains("ElifOP")) {
                elifCounter++;
                String elifScopeName = "ElifOP" + elifCounter + " Body";
                ScopingTable elifScope = parentScope.getChildScopingTable(elifScopeName);

                if(elifScope != null) {
                    Node elifCondNode = generateExpression(child.getChildNodes().get(0), elifScope, visitor);
                    String elifCondition = processCondition(elifCondNode.getValue());

                    ifStatement.append("\n\telse if (").append(elifCondition).append(") {\n");
                    ifStatement.append(generateVariableDeclarationsFromST(elifScope, new HashSet<>()));
                    processElifBody(child, ifStatement, elifScope, visitor);
                    ifStatement.append("    }\n");
                }
            }
        }
        }


    }

    public String generateAssignments(Node bodyNode, ScopingTable currentScope, ClangVisitor visitor) {
        StringBuilder assignments = new StringBuilder();

        if (bodyNode != null) {
            switch (bodyNode.getValue()) {
                case "AssignOP" -> {
                    Node rightSide = bodyNode.getList2().get(0);
                    if (rightSide.getValue().contains("FunCall") || rightSide.getValue().contains("ProcCall")) {

                        // Gestisce l'assegnazione con una chiamata a funzione/procedura

                    }
                    else{
                        String targetVar = bodyNode.getList1().get(0).getValue();
                        boolean isOutParam = false;

                        String currentProcedure = currentScope.scopeName;
                        CLangUtils.ProcedureData procData = procVariables.get(currentProcedure);

                        if (procData != null) {
                            CLangUtils.ParameterInfo paramInfo = procData.getParameters().get(targetVar);
                            isOutParam = (paramInfo != null && paramInfo.isOut());
                        }

                        Node rightExpr = generateExpression(bodyNode.getList2().get(0), currentScope, visitor);
                        if (rightExpr.getTYPENODE() != null && rightExpr.getTYPENODE().equals("STRING")) {
                            assignments.append("    strcpy(")
                                    .append(targetVar)
                                    .append(", ")
                                    .append(rightExpr.getValue())
                                    .append(");\n");
                        }
                        else if(rightExpr.getValue().toLowerCase().contains("true") || rightExpr.getValue().toLowerCase().contains("false")){
                            assignments.append("    ")
                                    .append(isOutParam ? "*" : "")
                                    .append(targetVar)
                                    .append(" = ")
                                    .append(rightExpr.getValue().toLowerCase())
                                    .append(";\n");

                        }else {
                            assignments.append("    ")
                                    .append(isOutParam ? "*" : "")
                                    .append(targetVar)
                                    .append(" = ")
                                    .append(rightExpr.getValue())
                                    .append(";\n");
                        }
                    }

                }
                case "IfStatOP" -> assignments.append(visitor.generateIfStatement(bodyNode, currentScope));
                case "WhileOP" -> assignments.append(visitor.generateWhileStatement(bodyNode, currentScope));
            }
        }

        return assignments.toString();
    }
    private void processElifBody(Node elifNode, StringBuilder ifStatement, ScopingTable st, ClangVisitor visitor) {
        for(Node n : elifNode.getChildNodes()) {
            if(n.getValue().equals("BodyOP")) {
                visitor.visitNodeRecursively(n,ifStatement,st,false);
            }
        }
    }
    void processElseBlock(Node ifNode, ScopingTable parentScope, StringBuilder ifStatement, ClangVisitor visitor) {
        for(Node child : ifNode.getChildNodes()) {
            if(child != null && child.getValue().equals("ElseOP")) {
                ScopingTable elseScope = parentScope.getChildScopingTable("ElseOP Body");

                if(elseScope != null) {
                    ifStatement.append("\n\telse {\n")
                            .append(generateVariableDeclarationsFromST(elseScope, new HashSet<>()));

                    processElseBody(child, ifStatement, elseScope, visitor);
                    ifStatement.append("    }\n");
                }
            }
        }
    }
    private void processElseBody(Node elseNode, StringBuilder ifStatement, ScopingTable st, ClangVisitor visitor) {
        for(Node n : elseNode.getChildNodes()) {
            if(n.getValue().equals("BodyOP")) {
                visitor.visitNodeRecursively(n,ifStatement,st,false);
            }
        }
        ifStatement.append(generateAssignments(elseNode.getChildNodes().get(0),st, visitor));
    }
    String mapTypeToC(String toy2Type, Boolean isOut) {
        if (isOut != null && isOut) {
            return switch (toy2Type) {
                case "INTEGER" -> "int*";
                case "REAL" -> "double*";
                case "STRING" -> "char*";
                case "BOOLEAN" -> "bool";
                default -> "void";
            };
        } else {
            return switch (toy2Type) {
                case "INTEGER" -> "int";
                case "REAL" -> "double";
                case "STRING" -> "char*";
                case "BOOLEAN" -> "bool";
                default -> "void";
            };
        }
    }
    // Genera le dichiarazioni delle variabili per un determinato scope, evitando i duplicati con i parametri di input
    String generateVariableDeclarationsFromST(ScopingTable scope, Set<String> inputParams) {
        StringBuilder declarations = new StringBuilder();
        Map<String, String> variables = scope.getTable();

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String varName = entry.getKey();

            // Salta le variabili che sono già presenti tra i parametri di input
            if (inputParams.contains(varName)) {
                continue; // Salta la dichiarazione
            }

            // Estrai il tipo di dato reale
            String varType = entry.getValue().split(",")[1].strip();

            String cType = mapTypeToC(varType, null);
            if(cType.equals("char*")){
                cType = "char";
                declarations.append("    ").append(cType).append(" ").append(varName).append("[50];\n");}
            else
                declarations.append("    ").append(cType).append(" ").append(varName).append(";\n");

        }

        return declarations.toString();
    }
    // Funzione per gestire la dichiarazione delle variabili da VarDeclOP
    String generateVarDeclarations(Node varDeclNode) {
        StringBuilder declarations = new StringBuilder();

        // Itera su ogni DeclsOP in NODELIST1
        if (varDeclNode.getList1() != null && !varDeclNode.getList1().isEmpty()) {
            for (Node declsNode : varDeclNode.getList1()) {
                if (declsNode.getValue().equals("DeclsOP")) {

                    List<Node> varNames = declsNode.getList1(); // Nomi delle variabili
                    List<Node> initialValues = declsNode.getList2(); // Valori iniziali


                    if(initialValues != null){
                        // Dichiarazione di variabili con eventuali valori iniziali
                        for (int i = 0; i < varNames.size(); i++) {
                            String left = varNames.get(i).getValue();
                            String right = initialValues.get(i).getValue();
                            String type = initialValues.get(i).getTYPENODE();

                            if(type.equals("STRING"))
                                declarations.append("    strcpy(").append(left).append(",\"").append(right).append("\")");
                            else if (type.equals("BOOLEAN")) {
                                declarations.append("    ").append(left).append(" = ").append(right.toLowerCase());
                            } else
                                declarations.append("    ").append(left).append(" = ").append(right);

                            declarations.append(";\n");
                        }
                    }
                }
            }
        }
        return declarations.toString();
    }
    record ParameterInfo(boolean isOut, int position) {
    }
    static class ProcedureData {
        private final Map<String, ParameterInfo> parameters = new LinkedHashMap<>();

        public ProcedureData() {
        }

        public void addParameter(String paramName, ParameterInfo info) {
            parameters.put(paramName, info);
        }

        public Map<String, ParameterInfo> getParameters() {
            return parameters;
        }

    }
    String processCondition(String condition) {
        Pattern pattern = Pattern.compile("\\b\\w+\\b");
        Matcher matcher = pattern.matcher(condition);
        StringBuilder sb = new StringBuilder();


        while (matcher.find()) {
            String token = matcher.group();
            boolean found = false;
            if(token.contains("True") || token.contains("False"))
                token = token.toLowerCase();

            for (ProcedureData data : procVariables.values()) {
                ParameterInfo info = data.getParameters().get(token);
                if (info != null && info.isOut()) {
                    matcher.appendReplacement(sb, "*" + token);
                    found = true;
                    break;
                }
            }

            if (!found) {
                matcher.appendReplacement(sb, token);
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

}