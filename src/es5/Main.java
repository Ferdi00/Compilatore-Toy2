package es5;

import java_cup.runtime.Symbol;
import nodes.ProgramOP;
import visitor.Clang.ClangVisitor;
import visitor.scoping.ScopingTable;
import visitor.scoping.ScopingVisitor;
import visitor.PrintVisitor;
import visitor.XmlVisitor;
import visitor.typechecking.TypeVisitor;

import java.io.*;
import java.util.Map;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {

         Map<String, Map<String, String>> procParamTypeMap;  // Mappa di mappe

        // Verifica se è stato fornito un argomento per il nome del file di testo
        if (args.length < 1) {
            System.out.println("Per favore, fornisci il nome del file di testo come argomento.");
            return;
        }

        String filePath = args[0];

        FileReader reader = new FileReader(filePath);
        Lexer lexer = new Lexer(reader);
        parser p = new parser(lexer);

        try {
            Symbol res = p.parse();
            //PrintVisitor prvis = new PrintVisitor();
            //((ProgramOP)res.value).accept(prvis);
            //mlVisitor xmlvis = new XmlVisitor();
            //((ProgramOP)res.value).accept(xmlvis);
            //xmlvis.saveOnFile(filePath.replaceAll("toy2","xml"));
            //------------
            ScopingVisitor scvis = new ScopingVisitor();
            ScopingTable scopingTable =((ProgramOP)res.value).accept(scvis);
            procParamTypeMap =scvis.getParamTypeMap();// Mappa di mappe scvis.getParamTypeMap();
            //------------
            TypeVisitor tvis = new TypeVisitor(scopingTable, procParamTypeMap );
            ((ProgramOP)res.value).accept(tvis);
            //------------
            ClangVisitor cvis = new ClangVisitor(scopingTable);
            ((ProgramOP)res.value).accept(cvis);
            String generatedCode = cvis.getFinalCode();

            //genera file C
            new File(filePath + ".c").delete();
            FileWriter myWriter = new FileWriter(filePath + ".c");
            myWriter.write(generatedCode);
            myWriter.close();

            //compila file C
            String estensione;
            if (System.getProperty("os.name").contains("Windows")) {
                estensione = ".exe";
            } else {
                estensione = ".out";
            }
            new File(filePath + estensione).delete();
            ProcessBuilder builder = new ProcessBuilder("clang", "-o", filePath + estensione, filePath + ".c");
            builder.start();
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));

        } catch (FileNotFoundException e) {
            System.out.println("File non trovato: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
