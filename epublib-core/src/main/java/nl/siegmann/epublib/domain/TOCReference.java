package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An item in the Table of Contents.
 *
 * @see nl.siegmann.epublib.domain.TableOfContents
 *
 * @author paul
 *
 */
public class TOCReference extends TitledResourceReference {

    /**
     *
     */
    private static final long serialVersionUID = 5787958246077042456L;
    private List<TOCReference> children;
    private static final Comparator<TOCReference> COMPARATOR_BY_TITLE_IGNORE_CASE = new Comparator<TOCReference>() {

        @Override
        public int compare(final TOCReference tocReference1, final TOCReference tocReference2) {
            return String.CASE_INSENSITIVE_ORDER.compare(tocReference1.getTitle(), tocReference2.getTitle());
        }
    };

    public TOCReference() {
        this(null, null, null);
    }

    public TOCReference(final String name, final Resource resource) {
        this(name, resource, null);
    }

    public TOCReference(final String name, final Resource resource, final String fragmentId) {
        this(name, resource, fragmentId, new ArrayList<TOCReference>());
    }

    private TOCReference(final String title, final Resource resource, final String fragmentId,
                         final List<TOCReference> children) {
        super(resource, title, fragmentId);
        this.children = children;
    }

    public static Comparator<TOCReference> getComparatorByTitleIgnoreCase() {
        return COMPARATOR_BY_TITLE_IGNORE_CASE;
    }

    public List<TOCReference> getChildren() {
        return this.children;
    }

    public TOCReference addChildSection(final TOCReference childSection) {
        this.children.add(childSection);
        return childSection;
    }

    public void setChildren(final List<TOCReference> children) {
        this.children = children;
    }
}
