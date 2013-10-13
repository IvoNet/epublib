package nl.siegmann.epublib.utilities;

import junit.framework.TestCase;
import nl.siegmann.epublib.Constants;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class HtmlSplitterTest extends TestCase {

    public void test1() {
        final HtmlSplitter htmlSplitter = new HtmlSplitter();
        try {
            final String bookResourceName = "/holmes_scandal_bohemia.html";
            final Reader input = new InputStreamReader(HtmlSplitterTest.class
                                                               .getResourceAsStream(bookResourceName),
                                                       Constants.CHARACTER_ENCODING);
            final int maxSize = 3000;
            final List<List<XMLEvent>> result = htmlSplitter.splitHtml(input, maxSize);
            final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            for (final List<XMLEvent> aResult : result) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final XMLEventWriter writer = xmlOutputFactory.createXMLEventWriter(out);
                for (final XMLEvent xmlEvent : aResult) {
                    writer.add(xmlEvent);
                }
                writer.close();
                final byte[] data = out.toByteArray();
                assertTrue(data.length > 0);
                assertTrue(data.length <= maxSize);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
