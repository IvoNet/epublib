package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads the package document metadata.
 *
 * In its own separate class because the PackageDocumentReader became a bit large and unwieldy.
 *
 * @author paul
 *
 */
// package
class PackageDocumentMetadataReader extends PackageDocumentBase {

    private static final Logger log = LoggerFactory.getLogger(PackageDocumentMetadataReader.class);

    public static Metadata readMetadata(final Document packageDocument) {
        final Metadata result = new Metadata();
        final Element metadataElement = DOMUtil
                .getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.metadata);
        if (metadataElement == null) {
            log.error("Package does not contain element " + OPFTags.metadata);
            return result;
        }
        result.setTitles(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.title));
        result.setPublishers(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.publisher));
        result.setDescriptions(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE,
                                                            DCTags.description));
        result.setRights(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.rights));
        result.setTypes(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.type));
        result.setSubjects(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.subject));
        result.setIdentifiers(readIdentifiers(metadataElement));
        result.setAuthors(readCreators(metadataElement));
        result.setContributors(readContributors(metadataElement));
        result.setDates(readDates(metadataElement));
        result.setOtherProperties(readOtherProperties(metadataElement));
        result.setMetaAttributes(readMetaProperties(metadataElement));
        final Element languageTag = DOMUtil
                .getFirstElementByTagNameNS(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.language);
        if (languageTag != null) {
            result.setLanguage(DOMUtil.getTextChildrenContent(languageTag));
        }


        return result;
    }

    /**
     * consumes meta tags that have a property attribute as defined in the standard. For example:
     * &lt;meta property="rendition:layout"&gt;pre-paginated&lt;/meta&gt;
     * @param metadataElement
     * @return
     */
    private static Map<QName, String> readOtherProperties(final Element metadataElement) {
        final Map<QName, String> result = new HashMap<QName, String>();

        final NodeList metaTags = metadataElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.meta);
        for (int i = 0; i < metaTags.getLength(); i++) {
            final Node metaNode = metaTags.item(i);
            final Node property = metaNode.getAttributes().getNamedItem(OPFAttributes.property);
            if (property != null) {
                final String name = property.getNodeValue();
                final String value = metaNode.getTextContent();
                result.put(new QName(name), value);
            }
        }

        return result;
    }

    /**
     * consumes meta tags that have a property attribute as defined in the standard. For example:
     * &lt;meta property="rendition:layout"&gt;pre-paginated&lt;/meta&gt;
     * @param metadataElement
     * @return
     */
    private static Map<String, String> readMetaProperties(final Element metadataElement) {
        final Map<String, String> result = new HashMap<String, String>();

        final NodeList metaTags = metadataElement.getElementsByTagName(OPFTags.meta);
        for (int i = 0; i < metaTags.getLength(); i++) {
            final Element metaElement = (Element) metaTags.item(i);
            final String name = metaElement.getAttribute(OPFAttributes.name);
            final String value = metaElement.getAttribute(OPFAttributes.content);
            result.put(name, value);
        }

        return result;
    }

    private static String getBookIdId(final Document document) {
        final Element packageElement = DOMUtil
                .getFirstElementByTagNameNS(document.getDocumentElement(), NAMESPACE_OPF, OPFTags.packageTag);
        if (packageElement == null) {
            return null;
        }
        final String result = packageElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.uniqueIdentifier);
        return result;
    }

    private static List<Author> readCreators(final Element metadataElement) {
        return readAuthors(DCTags.creator, metadataElement);
    }

    private static List<Author> readContributors(final Element metadataElement) {
        return readAuthors(DCTags.contributor, metadataElement);
    }

    private static List<Author> readAuthors(final String authorTag, final Element metadataElement) {
        final NodeList elements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, authorTag);
        final List<Author> result = new ArrayList<Author>(elements.getLength());
        for (int i = 0; i < elements.getLength(); i++) {
            final Element authorElement = (Element) elements.item(i);
            final Author author = createAuthor(authorElement);
            if (author != null) {
                result.add(author);
            }
        }
        return result;

    }

    private static List<Date> readDates(final Element metadataElement) {
        final NodeList elements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCTags.date);
        final List<Date> result = new ArrayList<Date>(elements.getLength());
        for (int i = 0; i < elements.getLength(); i++) {
            final Element dateElement = (Element) elements.item(i);
            final Date date;
            try {
                date = new Date(DOMUtil.getTextChildrenContent(dateElement), dateElement
                        .getAttributeNS(NAMESPACE_OPF, OPFAttributes.event));
                result.add(date);
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
            }
        }
        return result;

    }

    private static Author createAuthor(final Element authorElement) {
        final String authorString = DOMUtil.getTextChildrenContent(authorElement);
        if (StringUtil.isBlank(authorString)) {
            return null;
        }
        final int spacePos = authorString.lastIndexOf(' ');
        final Author result;
        result = spacePos < 0 ? new Author(authorString) :
                 new Author(authorString.substring(0, spacePos), authorString.substring(spacePos + 1));
        result.setRole(authorElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.role));
        return result;
    }


    private static List<Identifier> readIdentifiers(final Element metadataElement) {
        final NodeList identifierElements = metadataElement
                .getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCTags.identifier);
        if (identifierElements.getLength() == 0) {
            log.error("Package does not contain element " + DCTags.identifier);
            return new ArrayList<Identifier>();
        }
        final String bookIdId = getBookIdId(metadataElement.getOwnerDocument());
        final List<Identifier> result = new ArrayList<Identifier>(identifierElements.getLength());
        for (int i = 0; i < identifierElements.getLength(); i++) {
            final Element identifierElement = (Element) identifierElements.item(i);
            final String schemeName = identifierElement.getAttributeNS(NAMESPACE_OPF, DCAttributes.scheme);
            final String identifierValue = DOMUtil.getTextChildrenContent(identifierElement);
            if (StringUtil.isBlank(identifierValue)) {
                continue;
            }
            final Identifier identifier = new Identifier(schemeName, identifierValue);
            if (identifierElement.getAttribute("id").equals(bookIdId)) {
                identifier.setBookId(true);
            }
            result.add(identifier);
        }
        return result;
    }
}
