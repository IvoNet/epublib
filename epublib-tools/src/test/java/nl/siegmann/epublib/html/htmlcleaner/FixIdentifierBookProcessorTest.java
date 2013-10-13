package nl.siegmann.epublib.html.htmlcleaner;

import junit.framework.TestCase;
import nl.siegmann.epublib.bookprocessor.FixIdentifierBookProcessor;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.util.CollectionUtil;

public class FixIdentifierBookProcessorTest extends TestCase {

    public void test_empty_book() {
        final Book book = new Book();
        final FixIdentifierBookProcessor fixIdentifierBookProcessor = new FixIdentifierBookProcessor();
        final Book resultBook = fixIdentifierBookProcessor.processBook(book);
        assertEquals(1, resultBook.getMetadata().getIdentifiers().size());
        final Identifier identifier = CollectionUtil.first(resultBook.getMetadata().getIdentifiers());
        assertEquals(Identifier.Scheme.UUID, identifier.getScheme());
    }

    public void test_single_identifier() {
        final Book book = new Book();
        final Identifier identifier = new Identifier(Identifier.Scheme.ISBN, "1234");
        book.getMetadata().addIdentifier(identifier);
        final FixIdentifierBookProcessor fixIdentifierBookProcessor = new FixIdentifierBookProcessor();
        final Book resultBook = fixIdentifierBookProcessor.processBook(book);
        assertEquals(1, resultBook.getMetadata().getIdentifiers().size());
        final Identifier actualIdentifier = CollectionUtil.first(resultBook.getMetadata().getIdentifiers());
        assertEquals(identifier, actualIdentifier);
    }
}
