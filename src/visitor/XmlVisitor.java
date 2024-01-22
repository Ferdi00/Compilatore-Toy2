package visitor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import res.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class XmlVisitor implements NodeVisitor<Node>{
    private Document document;

    public void saveOnFile(String filepath) throws TransformerException {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.transform(new DOMSource(document), new StreamResult(new File(filepath)));
    }

    public XmlVisitor() throws ParserConfigurationException {
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }
    @Override
    public Object visitNode(Node node) {
        String trasformed = transformString(node.getValue());
        Element parent = document.createElement(trasformed);
       // System.out.println("\nPARENT: " + parent);

        List<Node> list1 = node.getList1();
        List<Node> list2 = node.getList2();

        List<Node> children = node.getChildNodes();
        for (Node n : children) {
            if (n != null) {
                Element childEle = (Element) n.accept(this);
                //System.out.println("\nChildEle: " + childEle);
                parent.appendChild(childEle);
            }
        }

        if (list1!= null && !list1.isEmpty()){
            Element listEle = document.createElement("Lista1-" + node.getListname1());
           for (Node n : list1) {
               Element childEle = (Element) n.accept(this);
               listEle.appendChild(childEle);
               parent.appendChild(listEle);
            }
        }

        if (list2!= null && !list2.isEmpty()){
            //System.out.println("sto in lista2");
            Element listEle = document.createElement("Lista2-" + node.getListname2());
            //System.out.println("lista appena creata:"+listEle);
            for (Node n : list2) {
                if(n != null){
                    Element childEle = (Element) n.accept(this);
                    //System.out.println("ELEMENTO LISTA2: "+childEle);
                    listEle.appendChild(childEle);
                    parent.appendChild(listEle);
                }

            }
        }
        document.appendChild(parent);

        return parent;
    }


    public static String transformString(String input) {
        // Sostituisci gli spazi vuoti con "_"
        String output = input.replaceAll("[\\s()/:?]", "_");
        return output;
    }
    //scrivi funzione per convertire numeri e spazi in xml

}

