package es4;

import java_cup.runtime.Symbol;
import res.ProgramOP;
import scoping.ScopingTable;
import scoping.ScopingVisitor;
import visitor.PrintVisitor;
import visitor.XmlVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Tester {

    public static void main(String[] args) {


        // Verifica se è stato fornito un argomento per il nome del file di testo
        if (args.length < 1) {
            System.out.println("Per favore, fornisci il nome del file di testo come argomento.");
            return;
        }

        // Costruisci il percorso del file di input
        String filePath = "test" + File.separator + args[0];
        File input = new File(filePath);

        try {
            // Apri il file di input per la lettura
            FileReader fileReader = new FileReader(input);
            Lexer lexer = new Lexer(fileReader);

            parser p = new parser(lexer);
           // p.debug_parse();
            Symbol res = p.parse();

            PrintVisitor prvis = new PrintVisitor();
            ((ProgramOP)res.value).accept(prvis);
          /*XmlVisitor xmlvis = new XmlVisitor();
            ((ProgramOP)res.value).accept(xmlvis);
            xmlvis.saveOnFile(filePath.replaceAll("toy2","xml"));*/
            System.out.println("\n\n\n SCOPING VISITOR");
            ScopingVisitor scvis = new ScopingVisitor();
            ScopingTable sc =((ProgramOP)res.value).accept(scvis);
            System.out.println(sc);
            // Chiudi il FileReader dopo aver terminato la lettura*/
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File non trovato: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
