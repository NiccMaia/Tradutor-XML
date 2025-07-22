import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

import java.io.File;
import java.util.HashMap;

public class TradutorXML {

    public static void main(String[] args) {
        try {
            File inputFile = new File("entrada.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            HashMap<String, String> dicionario = new HashMap<>();
            dicionario.put("nome", "name");
            dicionario.put("idade", "age");

            translateTags(doc.getDocumentElement(), dicionario);

            // Gravar novo arquivo traduzido
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("saida.xml"));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);

            System.out.println("Arquivo traduzido com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void translateTags(Element element, HashMap<String, String> dicionario) {
        Document doc = element.getOwnerDocument();
        NodeList children = element.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node atual = children.item(i);
            if (atual instanceof Element) {
                translateTags((Element) atual, dicionario);
            }
        }

        String novaTag = dicionario.get(element.getTagName());
        if (novaTag != null) {
            Element novoElemento = doc.createElement(novaTag);

            while (element.hasChildNodes()) {
                novoElemento.appendChild(element.getFirstChild());
            }

            NamedNodeMap atributos = element.getAttributes();
            for (int i = 0; i < atributos.getLength(); i++) {
                Attr attr = (Attr) atributos.item(i);
                novoElemento.setAttribute(attr.getName(), attr.getValue());
            }

            Node pai = element.getParentNode();
            pai.replaceChild(novoElemento, element);
        }
    }
}
