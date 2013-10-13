package nl.siegmann.epublib.viewer;

import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.util.ArrayList;
import java.util.List;

class MyParserCallback extends HTMLEditorKit.ParserCallback {
    private final HTMLEditorKit.ParserCallback parserCallback;
    private List<String> stylesheetHrefs = new ArrayList<String>();

    public MyParserCallback(final HTMLEditorKit.ParserCallback parserCallback) {
        this.parserCallback = parserCallback;
    }

    public List<String> getStylesheetHrefs() {
        return this.stylesheetHrefs;
    }

    public void setStylesheetHrefs(final List<String> stylesheetHrefs) {
        this.stylesheetHrefs = stylesheetHrefs;
    }

    private boolean isStylesheetLink(final HTML.Tag tag, final MutableAttributeSet attributes) {
        return ((tag == HTML.Tag.LINK)
                && (attributes.containsAttribute(HTML.Attribute.REL, "stylesheet"))
                && (attributes.containsAttribute(HTML.Attribute.TYPE, "text/css")));
    }


    private void handleStylesheet(final HTML.Tag tag, final MutableAttributeSet attributes) {
        if (isStylesheetLink(tag, attributes)) {
            this.stylesheetHrefs.add(attributes.getAttribute(HTML.Attribute.HREF).toString());
        }
    }

    public int hashCode() {
        return this.parserCallback.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(final Object obj) {
        return this.parserCallback.equals(obj);
    }

    public String toString() {
        return this.parserCallback.toString();
    }

    @Override
    public void flush() throws BadLocationException {
        this.parserCallback.flush();
    }

    @Override
    public void handleText(final char[] data, final int pos) {
        this.parserCallback.handleText(data, pos);
    }

    @Override
    public void handleComment(final char[] data, final int pos) {
        this.parserCallback.handleComment(data, pos);
    }

    @Override
    public void handleStartTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
        handleStylesheet(t, a);
        this.parserCallback.handleStartTag(t, a, pos);
    }

    @Override
    public void handleEndTag(final HTML.Tag t, final int pos) {
        this.parserCallback.handleEndTag(t, pos);
    }

    @Override
    public void handleSimpleTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
        handleStylesheet(t, a);
        this.parserCallback.handleSimpleTag(t, a, pos);
    }

    @Override
    public void handleError(final String errorMsg, final int pos) {
        this.parserCallback.handleError(errorMsg, pos);
    }

    @Override
    public void handleEndOfLineString(final String eol) {
        this.parserCallback.handleEndOfLineString(eol);
    }
}