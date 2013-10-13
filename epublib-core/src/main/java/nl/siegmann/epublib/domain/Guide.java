package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The guide is a selection of special pages of the book.
 * Examples of these are the cover, list of illustrations, etc.
 *
 * It is an optional part of an epub, and support for the various types of references varies by reader.
 *
 * The only part of this that is heavily used is the cover page.
 *
 * @author paul
 *
 */
public class Guide implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6256645339915751189L;

    private static final String DEFAULT_COVER_TITLE = GuideReference.COVER;

    private List<GuideReference> references = new ArrayList<GuideReference>();
    private static final int COVERPAGE_NOT_FOUND = -1;
    private static final int COVERPAGE_UNITIALIZED = -2;

    private int coverPageIndex = -1;

    public List<GuideReference> getReferences() {
        return this.references;
    }

    public void setReferences(final List<GuideReference> references) {
        this.references = references;
        uncheckCoverPage();
    }

    private void uncheckCoverPage() {
        this.coverPageIndex = COVERPAGE_UNITIALIZED;
    }

    GuideReference getCoverReference() {
        checkCoverPage();
        if (this.coverPageIndex >= 0) {
            return this.references.get(this.coverPageIndex);
        }
        return null;
    }

    int setCoverReference(final GuideReference guideReference) {
        if (this.coverPageIndex >= 0) {
            this.references.set(this.coverPageIndex, guideReference);
        } else {
            this.references.add(0, guideReference);
            this.coverPageIndex = 0;
        }
        return this.coverPageIndex;
    }

    private void checkCoverPage() {
        if (this.coverPageIndex == COVERPAGE_UNITIALIZED) {
            initCoverPage();
        }
    }


    private void initCoverPage() {
        int result = COVERPAGE_NOT_FOUND;
        for (int i = 0; i < this.references.size(); i++) {
            final GuideReference guideReference = this.references.get(i);
            if (guideReference.getType().equals(GuideReference.COVER)) {
                result = i;
                break;
            }
        }
        this.coverPageIndex = result;
    }

    /**
     * The coverpage of the book.
     *
     * @return The coverpage of the book.
     */
    public Resource getCoverPage() {
        final GuideReference guideReference = getCoverReference();
        if (guideReference == null) {
            return null;
        }
        return guideReference.getResource();
    }

    public void setCoverPage(final Resource coverPage) {
        final GuideReference coverpageGuideReference = new GuideReference(coverPage, GuideReference.COVER,
                                                                          DEFAULT_COVER_TITLE);
        setCoverReference(coverpageGuideReference);
    }


    public ResourceReference addReference(final GuideReference reference) {
        this.references.add(reference);
        uncheckCoverPage();
        return reference;
    }

    /**
     * A list of all GuideReferences that have the given referenceTypeName (ignoring case).
     *
     * @param referenceTypeName
     * @return A list of all GuideReferences that have the given referenceTypeName (ignoring case).
     */
    public List<GuideReference> getGuideReferencesByType(final String referenceTypeName) {
        final List<GuideReference> result = new ArrayList<GuideReference>();
        for (final GuideReference guideReference : this.references) {
            if (referenceTypeName.equalsIgnoreCase(guideReference.getType())) {
                result.add(guideReference);
            }
        }
        return result;
    }
}
