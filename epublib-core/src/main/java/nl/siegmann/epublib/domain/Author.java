package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.util.StringUtil;

import java.io.Serializable;

/**
 * Represents one of the authors of the book
 *
 * @author paul
 *
 */
public class Author implements Serializable {

    private static final long serialVersionUID = 6663408501416574200L;

    private String firstname;
    private String lastname;
    private Relator relator = Relator.AUTHOR;

    public Author(final String singleName) {
        this("", singleName);
    }


    public Author(final String firstname, final String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public String toString() {
        return this.lastname + ", " + this.firstname;
    }

    public int hashCode() {
        return StringUtil.hashCode(this.firstname, this.lastname);
    }


    public boolean equals(final Object authorObject) {
        if (!(authorObject instanceof Author)) {
            return false;
        }
        final Author other = (Author) authorObject;
        return StringUtil.equals(this.firstname, other.firstname)
               && StringUtil.equals(this.lastname, other.lastname);
    }

    public Relator setRole(final String code) {
        Relator result = Relator.byCode(code);
        if (result == null) {
            result = Relator.AUTHOR;
        }
        this.relator = result;
        return result;
    }


    public Relator getRelator() {
        return this.relator;
    }


    public void setRelator(final Relator relator) {
        this.relator = relator;
    }
}
