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

        Map<String, Map<String, String>> procParamTypeMap;

        if (args.length < 1) {
            System.out.println("Per favore, fornisci il nome del file di testo come argomento.");
            return;
        }

        String filePath = args[0];
        String outputDir = "test_files/c_out/";
        new File(outputDir).mkdirs();

        FileReader reader = new FileReader(filePath);
        Lexer lexer = new Lexer(reader);
        parser p = new parser(lexer);
        try {
            Symbol res = p.parse();
            //PrintVisitor printVisitor = new PrintVisitor();
            //((ProgramOP) res.value).accept(printVisitor);

            ScopingVisitor scvis = new ScopingVisitor();
            ScopingTable scopingTable = ((ProgramOP) res.value).accept(scvis);
            //System.out.println(scopingTable);
            procParamTypeMap = scvis.getParamTypeMap();
            TypeVisitor tvis = new TypeVisitor(scopingTable, procParamTypeMap);
            ((ProgramOP) res.value).accept(tvis);
            ClangVisitor cvis = new ClangVisitor(scopingTable);
            ((ProgramOP) res.value).accept(cvis);
            String generatedCode = cvis.getFinalCode();

            String baseName = new File(filePath).getName().replaceFirst("\\.txt$", "");
            File outputFile = new File(outputDir + baseName + ".c");
            outputFile.delete();

            FileWriter myWriter = new FileWriter(outputFile);
            myWriter.write(generatedCode);
            myWriter.close();

            // Compila solo se non in ambiente CI
            if (System.getenv("CI") == null) {
                String exeExtension = System.getProperty("os.name").toLowerCase().contains("win") ? ".exe" : "";
                String exePath = outputDir + baseName + exeExtension;
                new File(exePath).delete();

                ProcessBuilder builder = new ProcessBuilder("clang", "-o", exePath, outputFile.getAbsolutePath());
                builder.redirectErrorStream(true);
                Process process = builder.start();

                // Leggi l'output del processo
                BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = r.readLine()) != null) {
                    System.out.println(line);
                }
                process.waitFor();
                System.out.println("Compilazione completata. file generato: " + exePath);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File non trovato: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}