package nl.siegmann.epublib.viewer;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Wraps a HTMLEditorKit so we can make getParser() public.
 *
 * @author paul.siegmann
 *
 */
class MyHtmlEditorKit extends HTMLEditorKit {
    private final HTMLEditorKit htmlEditorKit;

    public MyHtmlEditorKit(final HTMLEditorKit htmlEditorKit) {
        this.htmlEditorKit = htmlEditorKit;
    }

    @Override
    public Parser getParser() {
        return super.getParser();
    }

    public int hashCode() {
        return this.htmlEditorKit.hashCode();
    }

    @Override
    public Element getCharacterAttributeRun() {
        return this.htmlEditorKit.getCharacterAttributeRun();
    }

    @Override
    public Caret createCaret() {
        return this.htmlEditorKit.createCaret();
    }

    @Override
    public void read(final InputStream in, final Document doc, final int pos)
            throws IOException, BadLocationException {
        this.htmlEditorKit.read(in, doc, pos);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(final Object obj) {
        return this.htmlEditorKit.equals(obj);
    }

    @Override
    public void write(final OutputStream out, final Document doc, final int pos, final int len)
            throws IOException, BadLocationException {
        this.htmlEditorKit.write(out, doc, pos, len);
    }

    @Override
    public String getContentType() {
        return this.htmlEditorKit.getContentType();
    }

    @Override
    public ViewFactory getViewFactory() {
        return this.htmlEditorKit.getViewFactory();
    }

    @Override
    public Document createDefaultDocument() {
        return this.htmlEditorKit.createDefaultDocument();
    }

    @Override
    public void read(final Reader in, final Document doc, final int pos) throws IOException,
                                                                                BadLocationException {
        this.htmlEditorKit.read(in, doc, pos);
    }

    @Override
    public void insertHTML(final HTMLDocument doc, final int offset, final String html,
                           final int popDepth, final int pushDepth, final HTML.Tag insertTag)
            throws BadLocationException, IOException {
        this.htmlEditorKit.insertHTML(doc, offset, html, popDepth, pushDepth,
                                      insertTag);
    }

    public String toString() {
        return this.htmlEditorKit.toString();
    }

    @Override
    public void write(final Writer out, final Document doc, final int pos, final int len)
            throws IOException, BadLocationException {
        this.htmlEditorKit.write(out, doc, pos, len);
    }

    @Override
    public void install(final JEditorPane c) {
        this.htmlEditorKit.install(c);
    }

    @Override
    public void deinstall(final JEditorPane c) {
        this.htmlEditorKit.deinstall(c);
    }

    @Override
    public void setStyleSheet(final StyleSheet s) {
        this.htmlEditorKit.setStyleSheet(s);
    }

    @Override
    public StyleSheet getStyleSheet() {
        return this.htmlEditorKit.getStyleSheet();
    }

    @Override
    public Action[] getActions() {
        return this.htmlEditorKit.getActions();
    }

    @Override
    public MutableAttributeSet getInputAttributes() {
        return this.htmlEditorKit.getInputAttributes();
    }

    @Override
    public void setDefaultCursor(final Cursor cursor) {
        this.htmlEditorKit.setDefaultCursor(cursor);
    }

    @Override
    public Cursor getDefaultCursor() {
        return this.htmlEditorKit.getDefaultCursor();
    }

    @Override
    public void setLinkCursor(final Cursor cursor) {
        this.htmlEditorKit.setLinkCursor(cursor);
    }

    @Override
    public Cursor getLinkCursor() {
        return this.htmlEditorKit.getLinkCursor();
    }

    @Override
    public boolean isAutoFormSubmission() {
        return this.htmlEditorKit.isAutoFormSubmission();
    }

    @Override
    public void setAutoFormSubmission(final boolean isAuto) {
        this.htmlEditorKit.setAutoFormSubmission(isAuto);
    }

    @Override
    public Object clone() {
        return this.htmlEditorKit.clone();
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        return this.htmlEditorKit.getAccessibleContext();
    }

}