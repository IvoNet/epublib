package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.domain.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A book processor that combines several other bookprocessors
 *
 * Fixes coverpage/coverimage. Cleans up the XHTML.
 *
 * @author paul.siegmann
 */
public class BookProcessorPipeline implements BookProcessor {

    private final Logger log = LoggerFactory.getLogger(BookProcessorPipeline.class);
    private List<BookProcessor> bookProcessors;

    protected BookProcessorPipeline() {
        this(null);
    }

    public BookProcessorPipeline(final List<BookProcessor> bookProcessingPipeline) {
        this.bookProcessors = bookProcessingPipeline;
    }


    @Override
    public Book processBook(Book book) throws IOException {
        if (this.bookProcessors == null) {
            return book;
        }
        for (final BookProcessor bookProcessor : this.bookProcessors) {
            try {
                book = bookProcessor.processBook(book);
            } catch (Exception e) {
                this.log.error(e.getMessage(), e);
                throw new IOException(e);
            }
        }
        return book;
    }

    public void addBookProcessor(final BookProcessor bookProcessor) {
        if (this.bookProcessors == null) {
            this.bookProcessors = new ArrayList<BookProcessor>();
        }
        this.bookProcessors.add(bookProcessor);
    }

    public void addBookProcessors(final Collection<BookProcessor> bookProcessors) {
        if (this.bookProcessors == null) {
            this.bookProcessors = new ArrayList<BookProcessor>();
        }
        this.bookProcessors.addAll(bookProcessors);
    }


    public List<BookProcessor> getBookProcessors() {
        return this.bookProcessors;
    }


    public void setBookProcessingPipeline(final List<BookProcessor> bookProcessingPipeline) {
        this.bookProcessors = bookProcessingPipeline;
    }

}
