package nl.siegmann.epublib.bookprocessor;


import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.BookProcessor;
import nl.siegmann.epublib.service.MediatypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Helper class for BookProcessors that only manipulate html type resources.
 *
 * @author paul
 *
 */
public abstract class HtmlBookProcessor implements BookProcessor {

    private static final Logger log = LoggerFactory.getLogger(HtmlBookProcessor.class);
    public static final String OUTPUT_ENCODING = "UTF-8";

    @Override
    public Book processBook(final Book book) {
        for (final Resource resource : book.getResources().getAll()) {
            try {
                cleanupResource(resource, book);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return book;
    }

    private void cleanupResource(final Resource resource, final Book book) throws IOException {
        if (resource.getMediaType() == MediatypeService.XHTML) {
            final byte[] cleanedHtml = processHtml(resource, book, Constants.CHARACTER_ENCODING);
            resource.setData(cleanedHtml);
            resource.setInputEncoding(Constants.CHARACTER_ENCODING);
        }
    }

    protected abstract byte[] processHtml(Resource resource, Book book, String encoding) throws IOException;
}
