package nl.siegmann.epublib.html.htmlcleaner;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CommentNode;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.EndTagToken;
import org.htmlcleaner.TagNode;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.util.List;
import java.util.Map;

class XmlEventSerializer implements XMLEventReader {

    private final CleanerProperties props;

    protected XmlEventSerializer(final CleanerProperties props) {
        this.props = props;
    }


    public void writeXml(final TagNode tagNode, final XMLStreamWriter writer) throws XMLStreamException {
//        if ( !props.isOmitXmlDeclaration() ) {
//            String declaration = "<?xml version=\"1.0\"";
//            if (charset != null) {
//                declaration += " encoding=\"" + charset + "\"";
//            }
//            declaration += "?>";
//            writer.write(declaration + "\n");
//		}

//		if ( !props.isOmitDoctypeDeclaration() ) {
//			DoctypeToken doctypeToken = tagNode.getDocType();
//			if ( doctypeToken != null ) {
//				doctypeToken.serialize(this, writer);
//			}
//		}
//		
        serialize(tagNode, writer);

        writer.flush();
    }

    void serializeOpenTag(final TagNode tagNode, final XMLStreamWriter writer) throws XMLStreamException {
        final String tagName = tagNode.getName();

        writer.writeStartElement(tagName);
        final Map tagAtttributes = tagNode.getAttributes();
        for (final Object o : tagAtttributes.entrySet()) {
            final Map.Entry entry = (Map.Entry) o;
            final String attName = (String) entry.getKey();
            final String attValue = (String) entry.getValue();

            if (!this.props.isNamespacesAware() && ("xmlns".equals(attName) || attName.startsWith("xmlns:"))) {
                continue;
            }
            writer.writeAttribute(attName, attValue);
        }
    }

    void serializeEmptyTag(final TagNode tagNode, final XMLStreamWriter writer) throws XMLStreamException {
        final String tagName = tagNode.getName();

        writer.writeEmptyElement(tagName);
        final Map tagAtttributes = tagNode.getAttributes();
        for (final Object o : tagAtttributes.entrySet()) {
            final Map.Entry entry = (Map.Entry) o;
            final String attName = (String) entry.getKey();
            final String attValue = (String) entry.getValue();

            if (!this.props.isNamespacesAware() && ("xmlns".equals(attName) || attName.startsWith("xmlns:"))) {
                continue;
            }
            writer.writeAttribute(attName, attValue);
        }
    }

    void serializeEndTag(final TagNode tagNode, final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }


    void serialize(final TagNode tagNode, final XMLStreamWriter writer) throws XMLStreamException {
        if (tagNode.getChildren().isEmpty()) {
            serializeEmptyTag(tagNode, writer);
        } else {
            serializeOpenTag(tagNode, writer);

            final List tagChildren = tagNode.getChildren();
            for (final Object item : tagChildren) {
                if (item != null) {
                    serializeToken(item, writer);
                }
            }
            serializeEndTag(tagNode, writer);
        }
    }


    private void serializeToken(final Object item, final XMLStreamWriter writer) throws XMLStreamException {
        if (item instanceof ContentNode) {
            writer.writeCharacters(((ContentNode) item).getContent().toString());
        } else if (item instanceof CommentNode) {
            writer.writeComment(((CommentNode) item).getContent().toString());
        } else if (item instanceof EndTagToken) {
//        	writer.writeEndElement(); //FIXME ?? Empty??
        } else if (item instanceof TagNode) {
            serialize((TagNode) item, writer);
        }
    }


    @Override
    public void close() throws XMLStreamException {
        // TODO Auto-generated method stub

    }


    @Override
    public String getElementText() throws XMLStreamException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public boolean hasNext() {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public XMLEvent peek() throws XMLStreamException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Object next() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void remove() {
        // TODO Auto-generated method stub

    }
}