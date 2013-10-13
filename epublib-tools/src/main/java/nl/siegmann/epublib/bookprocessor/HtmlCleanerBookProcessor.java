package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.util.NoCloseWriter;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DoctypeToken;
import org.htmlcleaner.EpublibXmlSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Cleans up regular html into xhtml. Uses HtmlCleaner to do this.
 *
 * @author paul
 *
 */
public class HtmlCleanerBookProcessor extends HtmlBookProcessor {

    @SuppressWarnings("unused") private static final Logger log = LoggerFactory
            .getLogger(HtmlCleanerBookProcessor.class);

    private final HtmlCleaner htmlCleaner;

    public HtmlCleanerBookProcessor() {
        this.htmlCleaner = createHtmlCleaner();
    }

    private static HtmlCleaner createHtmlCleaner() {
        final HtmlCleaner result = new HtmlCleaner();
        final CleanerProperties cleanerProperties = result.getProperties();
        cleanerProperties.setOmitXmlDeclaration(true);
        cleanerProperties.setOmitDoctypeDeclaration(false);
        cleanerProperties.setRecognizeUnicodeChars(true);
        cleanerProperties.setTranslateSpecialEntities(false);
        cleanerProperties.setIgnoreQuestAndExclam(true);
        cleanerProperties.setUseEmptyElementTags(false);
        return result;
    }

    @Override
    public byte[] processHtml(final Resource resource, final Book book, final String outputEncoding)
            throws IOException {

        // clean html
        final TagNode node = this.htmlCleaner.clean(resource.getReader());

        // post-process cleaned html
        node.setAttribute("xmlns", Constants.NAMESPACE_XHTML);
        node.setDocType(createXHTMLDoctypeToken());

        // write result to output
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(out, outputEncoding);
        writer = new NoCloseWriter(writer);
        final EpublibXmlSerializer xmlSerializer = new EpublibXmlSerializer(this.htmlCleaner
                                                                                    .getProperties(), outputEncoding);
        xmlSerializer.write(node, writer, outputEncoding);
        writer.flush();

        return out.toByteArray();
    }

    private DoctypeToken createXHTMLDoctypeToken() {
        return new DoctypeToken("html", "PUBLIC", "-//W3C//DTD XHTML 1.1//EN",
                                "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd");
    }
}
