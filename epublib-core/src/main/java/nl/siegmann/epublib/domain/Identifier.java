package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.util.StringUtil;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * A Book's identifier.
 *
 * Defaults to a random UUID and scheme "UUID"
 *
 * @author paul
 *
 */
public class Identifier implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 955949951416391810L;

    public interface Scheme {
        String UUID = "UUID";
        String ISBN = "ISBN";
        String URL = "URL";
        String URI = "URI";
    }

    private boolean bookId;
    private String scheme;
    private String value;

    /**
     * Creates an Identifier with as value a random UUID and scheme "UUID"
     */
    public Identifier() {
        this(Scheme.UUID, UUID.randomUUID().toString());
    }


    public Identifier(final String scheme, final String value) {
        this.scheme = scheme;
        this.value = value;
    }

    /**
     * The first identifier for which the bookId is true is made the bookId identifier.
     * If no identifier has bookId == true then the first bookId identifier is written as the primary.
     *
     * @param identifiers
     * @return The first identifier for which the bookId is true is made the bookId identifier.
     */
    public static Identifier getBookIdIdentifier(final List<Identifier> identifiers) {
        if ((identifiers == null) || identifiers.isEmpty()) {
            return null;
        }

        Identifier result = null;
        for (final Identifier identifier : identifiers) {
            if (identifier.isBookId()) {
                result = identifier;
                break;
            }
        }

        if (result == null) {
            result = identifiers.get(0);
        }

        return result;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }


    public void setBookId(final boolean bookId) {
        this.bookId = bookId;
    }


    /**
     * This bookId property allows the book creator to add multiple ids and tell the epubwriter which one to write
     * out as the bookId.
     *
     * The Dublin Core metadata spec allows multiple identifiers for a Book.
     * The epub spec requires exactly one identifier to be marked as the book id.
     *
     * @return whether this is the unique book id.
     */
    boolean isBookId() {
        return this.bookId;
    }

    public int hashCode() {
        return StringUtil.defaultIfNull(this.scheme).hashCode() ^ StringUtil.defaultIfNull(this.value).hashCode();
    }

    public boolean equals(final Object otherIdentifier) {
        if (!(otherIdentifier instanceof Identifier)) {
            return false;
        }
        return StringUtil.equals(this.scheme, ((Identifier) otherIdentifier).scheme)
               && StringUtil.equals(this.value, ((Identifier) otherIdentifier).value);
    }

    public String toString() {
        if (StringUtil.isBlank(this.scheme)) {
            return "" + this.value;
        }
        return "" + this.scheme + ":" + this.value;
    }
}
