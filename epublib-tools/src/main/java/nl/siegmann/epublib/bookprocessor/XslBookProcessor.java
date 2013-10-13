package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubProcessorSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;


/**
 * Uses the given xslFile to process all html resources of a Book.
 *
 * @author paul
 *
 */
public class XslBookProcessor extends HtmlBookProcessor {

    private static final Logger log = LoggerFactory.getLogger(XslBookProcessor.class);

    private Transformer transformer;

    public XslBookProcessor(final String xslFileName) throws TransformerConfigurationException {
        final File xslFile = new File(xslFileName);
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        this.transformer = transformerFactory.newTransformer(new StreamSource(xslFile));
    }

    @Override
    public byte[] processHtml(final Resource resource, final Book book, final String encoding) throws IOException {
        final byte[] result;
        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbFactory.newDocumentBuilder();
            db.setEntityResolver(EpubProcessorSupport.getEntityResolver());

            final Document doc = db.parse(new InputSource(resource.getReader()));

            final Source htmlSource = new DOMSource(doc.getDocumentElement());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final Writer writer = new OutputStreamWriter(out, "UTF-8");
            final Result streamResult = new StreamResult(writer);
            try {
                this.transformer.transform(htmlSource, streamResult);
            } catch (TransformerException e) {
                log.error(e.getMessage(), e);
                throw new IOException(e);
            }
            result = out.toByteArray();
            return result;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
