package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * Cleans up regular html into xhtml.
 * Uses HtmlCleaner to do this.
 *
 * @author paul
 *
 */
public class TextReplaceBookProcessor extends HtmlBookProcessor {

    @SuppressWarnings("unused") private static final Logger log = LoggerFactory
            .getLogger(TextReplaceBookProcessor.class);

    @Override
    public byte[] processHtml(final Resource resource, final Book book, final String outputEncoding)
            throws IOException {
        final Reader reader = resource.getReader();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(out, Constants.CHARACTER_ENCODING);
        for (final String line : IOUtils.readLines(reader)) {
            writer.write(processLine(line));
            writer.flush();
        }
        return out.toByteArray();
    }

    private String processLine(final String line) {
        return line.replace("&apos;", "'");
    }
}
