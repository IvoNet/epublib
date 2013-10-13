package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.util.StringUtil;


/**
 * These are references to elements of the book's guide.
 *
 * @see nl.siegmann.epublib.domain.Guide
 *
 * @author paul
 *
 */
public class GuideReference extends TitledResourceReference {

    /**
     *
     */
    private static final long serialVersionUID = -316179702440631834L;

    /**
     * the book cover(s), jacket information, etc.
     */
    public static final String COVER = "cover";

    /**
     * human-readable page with title, author, publisher, and other metadata
     */
    public static final String TITLE_PAGE = "title-page";

    /**
     * Human-readable table of contents.
     * Not to be confused the epub file table of contents
     *
     */
    public static final String TOC = "toc";

    /**
     * back-of-book style index
     */
    public static final String INDEX = "index";
    public static final String GLOSSARY = "glossary";
    public static final String ACKNOWLEDGEMENTS = "acknowledgements";
    public static final String BIBLIOGRAPHY = "bibliography";
    public static final String COLOPHON = "colophon";
    public static final String COPYRIGHT_PAGE = "copyright-page";
    public static final String DEDICATION = "dedication";

    /**
     *  an epigraph is a phrase, quotation, or poem that is set at the beginning of a document or component.
     *  source: http://en.wikipedia.org/wiki/Epigraph_%28literature%29
     */
    public static final String EPIGRAPH = "epigraph";

    public static final String FOREWORD = "foreword";

    /**
     * list of illustrations
     */
    public static final String LOI = "loi";

    /**
     * list of tables
     */
    public static final String LOT = "lot";
    public static final String NOTES = "notes";
    public static final String PREFACE = "preface";

    /**
     * A page of content (e.g. "Chapter 1")
     */
    public static final String TEXT = "text";

    private String type;

    public GuideReference(final Resource resource) {
        this(resource, null);
    }

    private GuideReference(final Resource resource, final String title) {
        super(resource, title);
    }

    public GuideReference(final Resource resource, final String type, final String title) {
        this(resource, type, title, null);
    }

    public GuideReference(final Resource resource, final String type, final String title, final String fragmentId) {
        super(resource, title, fragmentId);
        this.type = StringUtil.isNotBlank(type) ? type.toLowerCase() : null;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
