package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for working with the DOM.
 *
 * @author paul
 *
 */
// package
class DOMUtil {


    /**
     * First tries to get the attribute value by doing an getAttributeNS on the element,
     * if that gets an empty element it does a getAttribute without namespace.
     *
     * @param element
     * @param namespace
     * @param attribute
     * @return
     */
    public static String getAttribute(final Element element, final String namespace, final String attribute) {
        String result = element.getAttributeNS(namespace, attribute);
        if (StringUtil.isEmpty(result)) {
            result = element.getAttribute(attribute);
        }
        return result;
    }

    /**
     * Gets all descendant elements of the given parentElement with the given namespace and tagname and returns their
     * text child as a list of String.
     *
     * @param parentElement
     * @param namespace
     * @param tagname
     * @return
     */
    public static List<String> getElementsTextChild(final Element parentElement, final String namespace,
                                                    final String tagname) {
        final NodeList elements = parentElement.getElementsByTagNameNS(namespace, tagname);
        final List<String> result = new ArrayList<String>(elements.getLength());
        for (int i = 0; i < elements.getLength(); i++) {
            result.add(getTextChildrenContent((Element) elements.item(i)));
        }
        return result;
    }

    /**
     * Finds in the current document the first element with the given namespace and elementName and with the given
     * findAttributeName and findAttributeValue.
     * It then returns the value of the given resultAttributeName.
     *
     * @param document
     * @param namespace
     * @param elementName
     * @param findAttributeName
     * @param findAttributeValue
     * @param resultAttributeName
     * @return
     */
    public static String getFindAttributeValue(final Document document, final String namespace,
                                               final String elementName, final String findAttributeName,
                                               final String findAttributeValue, final String resultAttributeName) {
        final NodeList metaTags = document.getElementsByTagNameNS(namespace, elementName);
        for (int i = 0; i < metaTags.getLength(); i++) {
            final Element metaElement = (Element) metaTags.item(i);
            if (findAttributeValue.equalsIgnoreCase(metaElement.getAttribute(findAttributeName))
                && StringUtil.isNotBlank(metaElement.getAttribute(resultAttributeName))) {
                return metaElement.getAttribute(resultAttributeName);
            }
        }
        return null;
    }

    /**
     * Gets the first element that is a child of the parentElement and has the given namespace and tagName
     *
     * @param parentElement
     * @param namespace
     * @param tagName
     * @return
     */
    public static Element getFirstElementByTagNameNS(final Element parentElement, final String namespace,
                                                     final String tagName) {
        final NodeList nodes = parentElement.getElementsByTagNameNS(namespace, tagName);
        if (nodes.getLength() == 0) {
            return null;
        }
        return (Element) nodes.item(0);
    }

    /**
     * The contents of all Text nodes that are children of the given parentElement.
     * The result is trim()-ed.
     *
     * The reason for this more complicated procedure instead of just returning the data of the firstChild is that
     * when the text is Chinese characters then on Android each Characater is represented in the DOM as
     * an individual Text node.
     *
     * @param parentElement
     * @return
     */
    public static String getTextChildrenContent(final Element parentElement) {
        if (parentElement == null) {
            return null;
        }
        final StringBuilder result = new StringBuilder();
        final NodeList childNodes = parentElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node node = childNodes.item(i);
            if ((node == null) ||
                (node.getNodeType() != Node.TEXT_NODE)) {
                continue;
            }
            result.append(((Text) node).getData());
        }
        return result.toString().trim();
    }

}
