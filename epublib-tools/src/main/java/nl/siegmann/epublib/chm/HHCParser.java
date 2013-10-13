package nl.siegmann.epublib.chm;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.util.ResourceUtil;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses the windows help index (.hhc) file.
 *
 * @author paul
 *
 */
class HHCParser {

    public static final String DEFAULT_HTML_INPUT_ENCODING = "Windows-1251";

    public static List<TOCReference> parseHhc(final InputStream hhcFile, final Resources resources)
            throws IOException, ParserConfigurationException, XPathExpressionException {
        final HtmlCleaner htmlCleaner = new HtmlCleaner();
        final CleanerProperties props = htmlCleaner.getProperties();
        final TagNode node = htmlCleaner.clean(hhcFile);
        final Document hhcDocument = new DomSerializer(props).createDOM(node);
        final XPath xpath = XPathFactory.newInstance().newXPath();
        final Node ulNode = (Node) xpath.evaluate("body/ul", hhcDocument
                .getDocumentElement(), XPathConstants.NODE);
        final List<TOCReference> sections = processUlNode(ulNode, resources);
        return sections;
    }

    /*
     * Sometimes the structure is:
     * <li> <!-- parent element -->
     * 	<object> ... </object>
     *  <ul> ... </ul> <!-- child elements -->
     * </li>
     *
     * And sometimes:
     * <li> <!-- parent element -->
     * 	<object> ... </object>
     * </li>
     * <ul> ... </ul> <!-- child elements -->
     */
    private static List<TOCReference> processUlNode(final Node ulNode, final Resources resources) {
        List<TOCReference> result = new ArrayList<TOCReference>();
        final NodeList children = ulNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node node = children.item(i);
            if ("li".equals(node.getNodeName())) {
                final List<TOCReference> section = processLiNode(node, resources);
                result.addAll(section);
            } else if ("ul".equals(node.getNodeName())) {
                final List<TOCReference> childTOCReferences = processUlNode(node, resources);
                if (result.isEmpty()) {
                    result = childTOCReferences;
                } else {
                    result.get(result.size() - 1).getChildren().addAll(childTOCReferences);
                }
            }
        }
        return result;
    }


    private static List<TOCReference> processLiNode(final Node liNode, final Resources resources) {
        List<TOCReference> result = new ArrayList<TOCReference>();
        final NodeList children = liNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node node = children.item(i);
            if ("object".equals(node.getNodeName())) {
                final TOCReference section = processObjectNode(node, resources);
                if (section != null) {
                    result.add(section);
                }
            } else if ("ul".equals(node.getNodeName())) {
                final List<TOCReference> childTOCReferences = processUlNode(node, resources);
                if (result.isEmpty()) {
                    result = childTOCReferences;
                } else {
                    result.get(result.size() - 1).getChildren().addAll(childTOCReferences);
                }
            }
        }
        return result;
    }


    /**
     * Processes a CHM object node into a TOCReference
     * If the local name is empty then a TOCReference node is made with a null href value.
     *
     * <object type="text/sitemap">
     * 		<param name="Name" value="My favorite section" />
     * 		<param name="Local" value="section123.html" />
     *		<param name="ImageNumber" value="2" />
     * </object>
     *
     * @param objectNode
     *
     * @return A TOCReference of the object has a non-blank param child with name 'Name' and a non-blank param name
     * 'Local'
     */
    private static TOCReference processObjectNode(final Node objectNode, final Resources resources) {
        TOCReference result = null;
        final NodeList children = objectNode.getChildNodes();
        String name = null;
        String href = null;
        for (int i = 0; i < children.getLength(); i++) {
            final Node node = children.item(i);
            if ("param".equals(node.getNodeName())) {
                final String paramName = ((Element) node).getAttribute("name");
                if ("Name".equals(paramName)) {
                    name = ((Element) node).getAttribute("value");
                } else if ("Local".equals(paramName)) {
                    href = ((Element) node).getAttribute("value");
                }
            }
        }
        if ((!StringUtils.isBlank(href)) && (href != null) && href.startsWith("http://")) {
            return result;
        }
        if (!StringUtils.isBlank(name)) {
            Resource resource = resources.getByHref(href);
            if (resource == null) {
                resource = ResourceUtil.createResource(name, href);
                resources.add(resource);
            }
            result = new TOCReference(name, resource);
        }
        return result;
    }
}
