package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.BookProcessor;

import java.util.Collection;

public class FixMissingResourceBookProcessor implements BookProcessor {

    @Override
    public Book processBook(final Book book) {
        return book;
    }

    private void fixMissingResources(final Collection<TOCReference> tocReferences, final Book book) {
        for (final TOCReference tocReference : tocReferences) {
            if (tocReference.getResource() == null) {
                //FIXME ??!! what??!! I'm empty ??	
            }
        }
    }
}
