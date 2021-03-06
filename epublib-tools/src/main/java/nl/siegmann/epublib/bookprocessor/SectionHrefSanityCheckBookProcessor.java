package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.BookProcessor;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes Sections from the page flow that differ only from the previous section's href by the '#' in the url.
 *
 * @author paul
 *
 */
public class SectionHrefSanityCheckBookProcessor implements BookProcessor {

    @Override
    public Book processBook(final Book book) {
        book.getSpine().setSpineReferences(checkSpineReferences(book.getSpine()));
        return book;
    }

    private static List<SpineReference> checkSpineReferences(final Spine spine) {
        final List<SpineReference> result = new ArrayList<SpineReference>(spine.size());
        Resource previousResource = null;
        for (final SpineReference spineReference : spine.getSpineReferences()) {
            if ((spineReference.getResource() == null)
                || StringUtils.isBlank(spineReference.getResource().getHref())) {
                continue;
            }
            if ((previousResource == null)
                || (spineReference.getResource() == null)
                || (!(spineReference.getResource().getHref().equals(previousResource.getHref())))) {
                result.add(spineReference);
            }
            previousResource = spineReference.getResource();
        }
        return result;
    }
}
