package star.sequoia2.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class XMLUtils {
    public static String extractTextFromXml(String xml) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(false);
            f.setExpandEntityReferences(false);
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(new InputSource(new StringReader(xml)));

            Element root = doc.getDocumentElement();
            if (root == null || !"XMLFormattedText".equals(root.getNodeName())) {
                return xml;
            }

            StringBuilder out = new StringBuilder();
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);
                if (n.getNodeType() != Node.ELEMENT_NODE) continue;
                if (!"TextData".equals(n.getNodeName())) continue; // skip nested hover text

                String visible = collectDirectText(n);
                if (visible.isEmpty()) continue;

                String colourDec = attr(n, "colours");
                String boldAttr  = attr(n, "bold");
                boolean bold = "true".equalsIgnoreCase(boldAttr);

                String hover = extractHover(n);
                Click click = extractClick(n);

                String segment = visible;
                if (colourDec != null && !colourDec.isEmpty()) {
                    String hex = toRgbHex(colourDec);
                    segment = "§#" + hex + "ff" + segment;
                }
                if (bold) {
                    segment = "\\b{" + escapeTex(segment) + "}";
                } else {
                    segment = escapeTex(segment);
                }

                if (!hover.isEmpty()) {
                    segment = "\\hover{" + escapeTex(hover) + "}{" + segment + "}";
                }
                if (click != null) {
                    segment = "\\click{" + click.action + "}{" + escapeTex(click.value) + "}{" + segment + "}";
                }

                out.append(segment);
            }

            String s = out.toString();
            return s.isEmpty() ? xml : s;
        } catch (Exception e) {
            return xml;
        }
    }

    private static String collectDirectText(Node textData) {
        StringBuilder sb = new StringBuilder();
        NodeList kids = textData.getChildNodes();
        for (int j = 0; j < kids.getLength(); j++) {
            Node c = kids.item(j);
            if (c.getNodeType() == Node.TEXT_NODE || c.getNodeType() == Node.CDATA_SECTION_NODE) {
                sb.append(c.getNodeValue());
            }
        }
        return sb.toString();
    }

    private static String extractHover(Node textDataNode) {
        NodeList children = textDataNode.getChildNodes();
        StringBuilder hover = new StringBuilder();
        for (int i = 0; i < children.getLength(); i++) {
            Node ch = children.item(i);
            if (ch.getNodeType() == Node.ELEMENT_NODE && "XMLFormattedText".equals(ch.getNodeName())) {
                NodeList inner = ((Element) ch).getElementsByTagName("TextData");
                for (int k = 0; k < inner.getLength(); k++) {
                    Node td = inner.item(k);
                    NodeList tdc = td.getChildNodes();
                    for (int m = 0; m < tdc.getLength(); m++) {
                        Node n = tdc.item(m);
                        if (n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.CDATA_SECTION_NODE) {
                            hover.append(n.getNodeValue());
                        }
                    }
                }
            }
        }
        return hover.toString();
    }

    private record Click(String action, String value) {}

    private static Click extractClick(Node textDataNode) {
        NodeList children = textDataNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node ch = children.item(i);
            if (ch.getNodeType() == Node.ELEMENT_NODE && "ClickAction".equals(ch.getNodeName())) {
                String type = attr(ch, "type");
                String value = attr(ch, "value");
                String action = mapClickAction(type);
                if (action != null && value != null) {
                    return new Click(action, value);
                }
            }
        }
        return null;
    }

    private static String mapClickAction(String type) {
        if (type == null) return null;
        try {
            int t = Integer.parseInt(type);
            return switch (t) {
                case 1 -> "OPEN_URL";
                case 2 -> "RUN_COMMAND";
                case 3 -> "SUGGEST_COMMAND";
                case 4 -> "WSDATA";
                case 5 -> "COPY_TO_CLIPBOARD";
                default -> null;
            };
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String attr(Node node, String name) {
        return node.getAttributes() != null && node.getAttributes().getNamedItem(name) != null
                ? node.getAttributes().getNamedItem(name).getNodeValue()
                : null;
    }

    private static String toRgbHex(String decimal) {
        int v = Integer.parseInt(decimal);
        String hex = Integer.toHexString(v);
        if (hex.length() < 6) {
            hex = "000000".substring(hex.length()) + hex;
        } else if (hex.length() > 6) {
            hex = hex.substring(hex.length() - 6);
        }
        return hex;
    }

    private static String escapeTex(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.replace("\\", "\\\\").replace("{", "\\{").replace("}", "\\}");
    }
}
