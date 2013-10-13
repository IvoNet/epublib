package nl.siegmann.epublib.utilities;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Splits up a xhtml document into pieces that are all valid xhtml documents.
 *
 * @author paul
 *
 */
class HtmlSplitter {

    private final XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
    private final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    private List<XMLEvent> headerElements = new ArrayList<XMLEvent>();
    private List<XMLEvent> footerElements = new ArrayList<XMLEvent>();
    private int footerCloseTagLength;
    private final List<XMLEvent> elementStack = new ArrayList<XMLEvent>();
    private StringWriter currentDoc = new StringWriter();
    private List<XMLEvent> currentXmlEvents = new ArrayList<XMLEvent>();
    private XMLEventWriter out;
    private int maxLength = 300000; // 300K, the max length of a chapter of an epub document
    private final List<List<XMLEvent>> result = new ArrayList<List<XMLEvent>>();

    public List<List<XMLEvent>> splitHtml(final Reader reader, final int maxLength) throws XMLStreamException {
        final XMLEventReader xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(reader);
        return splitHtml(xmlEventReader, maxLength);
    }

    private static int calculateTotalTagStringLength(final List<XMLEvent> xmlEvents) {
        int result = 0;
        for (final XMLEvent xmlEvent : xmlEvents) {
            result += xmlEvent.toString().length();
        }
        return result;
    }

    List<List<XMLEvent>> splitHtml(final XMLEventReader reader, final int maxLength) throws XMLStreamException {
        this.headerElements = getHeaderElements(reader);
        this.footerElements = getFooterElements();
        this.footerCloseTagLength = calculateTotalTagStringLength(this.footerElements);
        this.maxLength = (int) ((float) maxLength * 0.9);
        this.currentXmlEvents = new ArrayList<XMLEvent>();
        this.currentXmlEvents.addAll(this.headerElements);
        this.currentXmlEvents.addAll(this.elementStack);
        this.out = this.xmlOutputFactory.createXMLEventWriter(this.currentDoc);
        for (final XMLEvent headerXmlEvent : this.headerElements) {
            this.out.add(headerXmlEvent);
        }
        XMLEvent xmlEvent = reader.nextEvent();
        while (!isBodyEndElement(xmlEvent)) {
            processXmlEvent(xmlEvent, this.result);
            xmlEvent = reader.nextEvent();
        }
        this.result.add(this.currentXmlEvents);
        return this.result;
    }


    private void closeCurrentDocument() throws XMLStreamException {
        closeAllTags(this.currentXmlEvents);
        this.currentXmlEvents.addAll(this.footerElements);
        this.result.add(this.currentXmlEvents);
    }

    private void startNewDocument() throws XMLStreamException {
        this.currentDoc = new StringWriter();
        this.out = this.xmlOutputFactory.createXMLEventWriter(this.currentDoc);
        for (final XMLEvent headerXmlEvent : this.headerElements) {
            this.out.add(headerXmlEvent);
        }
        for (final XMLEvent stackXmlEvent : this.elementStack) {
            this.out.add(stackXmlEvent);
        }

        this.currentXmlEvents = new ArrayList<XMLEvent>();
        this.currentXmlEvents.addAll(this.headerElements);
        this.currentXmlEvents.addAll(this.elementStack);
    }

    private void processXmlEvent(final XMLEvent xmlEvent, final List<List<XMLEvent>> docs) throws XMLStreamException {
        this.out.flush();
        final String currentSerializerDoc = this.currentDoc.toString();
        if ((currentSerializerDoc.length() + xmlEvent.toString().length() + this.footerCloseTagLength)
            >= this.maxLength) {
            closeCurrentDocument();
            startNewDocument();
        }
        updateStack(xmlEvent);
        this.out.add(xmlEvent);
        this.currentXmlEvents.add(xmlEvent);
    }

    private void closeAllTags(final List<XMLEvent> xmlEvents) throws XMLStreamException {
        for (int i = this.elementStack.size() - 1; i >= 0; i--) {
            final XMLEvent xmlEvent = this.elementStack.get(i);
            final XMLEvent xmlEndElementEvent = this.xmlEventFactory
                    .createEndElement(xmlEvent.asStartElement().getName(), null);
            xmlEvents.add(xmlEndElementEvent);
        }
    }

    private void updateStack(final XMLEvent xmlEvent) {
        if (xmlEvent.isStartElement()) {
            this.elementStack.add(xmlEvent);
        } else if (xmlEvent.isEndElement()) {
            final XMLEvent lastEvent = this.elementStack.get(this.elementStack.size() - 1);
            if (lastEvent.isStartElement() &&
                xmlEvent.asEndElement().getName().equals(lastEvent.asStartElement().getName())) {
                this.elementStack.remove(this.elementStack.size() - 1);
            }
        }
    }

    private List<XMLEvent> getHeaderElements(final XMLEventReader reader) throws XMLStreamException {
        final List<XMLEvent> result = new ArrayList<XMLEvent>();
        XMLEvent event = reader.nextEvent();
        while ((event != null) && (!isBodyStartElement(event))) {
            result.add(event);
            event = reader.nextEvent();
        }

        // add the body start tag to the result
        if (event != null) {
            result.add(event);
        }
        return result;
    }

    private List<XMLEvent> getFooterElements() throws XMLStreamException {
        final List<XMLEvent> result = new ArrayList<XMLEvent>();
        result.add(this.xmlEventFactory.createEndElement("", null, "body"));
        result.add(this.xmlEventFactory.createEndElement("", null, "html"));
        result.add(this.xmlEventFactory.createEndDocument());
        return result;
    }

    private static boolean isBodyStartElement(final XMLEvent xmlEvent) {
        return xmlEvent.isStartElement() && "body".equals(xmlEvent.asStartElement().getName().getLocalPart());
    }

    private static boolean isBodyEndElement(final XMLEvent xmlEvent) {
        return xmlEvent.isEndElement() && "body".equals(xmlEvent.asEndElement().getName().getLocalPart());
    }
}
