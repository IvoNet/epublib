package nl.siegmann.epublib.viewer;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.util.DesktopUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Displays a page
 *
 */
public class ContentPane extends JPanel implements NavigationEventListener,
                                                   HyperlinkListener {

    private static final long serialVersionUID = -5322988066178102320L;

    private static final Logger log = LoggerFactory
            .getLogger(ContentPane.class);
    private final Navigator navigator;
    private Resource currentResource;
    private final JEditorPane editorPane;
    private final JScrollPane scrollPane;
    private final HTMLDocumentFactory htmlDocumentFactory;

    public ContentPane(final Navigator navigator) {
        super(new GridLayout(1, 0));
        this.scrollPane = (JScrollPane) add(new JScrollPane());
        this.scrollPane.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(final KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyReleased(final KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    final Point viewPosition = ContentPane.this.scrollPane.getViewport().getViewPosition();
                    final int newY = (int) (viewPosition.getY() + 10);
                    ContentPane.this.scrollPane.getViewport()
                            .setViewPosition(new Point((int) viewPosition.getX(), newY));
                }
            }
        });
        this.scrollPane.addMouseWheelListener(new MouseWheelListener() {

            private boolean gotoNextPage;
            private boolean gotoPreviousPage;

            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {
                final int notches = e.getWheelRotation();
                final int increment = ContentPane.this.scrollPane.getVerticalScrollBar().getUnitIncrement(1);
                if (notches < 0) {
                    final Point viewPosition = ContentPane.this.scrollPane.getViewport().getViewPosition();
                    if ((viewPosition.getY() - increment) < 0) {
                        if (gotoPreviousPage) {
                            gotoPreviousPage = false;
                            ContentPane.this.navigator.gotoPreviousSpineSection(-1, ContentPane.this);
                        } else {
                            gotoPreviousPage = true;
                            ContentPane.this.scrollPane.getViewport()
                                    .setViewPosition(new Point((int) viewPosition.getX(), 0));
                        }
                    }
                } else {
                    // only move to the next page if we are exactly at the bottom of the current page
                    final Point viewPosition = ContentPane.this.scrollPane.getViewport().getViewPosition();
                    final int viewportHeight = ContentPane.this.scrollPane.getViewport().getHeight();
                    final int scrollMax = ContentPane.this.scrollPane.getVerticalScrollBar().getMaximum();
                    if ((viewPosition.getY() + viewportHeight + increment) > scrollMax) {
                        if (gotoNextPage) {
                            gotoNextPage = false;
                            ContentPane.this.navigator.gotoNextSpineSection(ContentPane.this);
                        } else {
                            gotoNextPage = true;
                            final int newY = scrollMax - viewportHeight;
                            ContentPane.this.scrollPane.getViewport()
                                    .setViewPosition(new Point((int) viewPosition.getX(), newY));
                        }
                    }
                }
            }
        });
        this.navigator = navigator;
        navigator.addNavigationEventListener(this);
        this.editorPane = createJEditorPane();
        this.scrollPane.getViewport().add(this.editorPane);
        this.htmlDocumentFactory = new HTMLDocumentFactory(navigator, this.editorPane.getEditorKit());
        initBook(navigator.getBook());
    }

    private void initBook(final Book book) {
        if (book == null) {
            return;
        }
        this.htmlDocumentFactory.init(book);
        displayPage(book.getCoverPage());
    }


    /**
     * Whether the given searchString matches any of the possibleValues.
     *
     * @param searchString
     * @param possibleValues
     * @return Whether the given searchString matches any of the possibleValues.
     */
    private static boolean matchesAny(final String searchString, final String... possibleValues) {
        for (final String attributeValue : possibleValues) {
            if (StringUtils.isNotBlank(attributeValue) && (attributeValue.equals(searchString))) {
                return true;
            }
        }
        return false;
    }


    /**
     * Scrolls the editorPane to the startOffset of the current element in the elementIterator
     *
     * @param requestFragmentId
     * @param attributeValue
     * @param editorPane
     * @param elementIterator
     *
     * @return whether it was a match and we jumped there.
     */
    private static void scrollToElement(final JEditorPane editorPane, final HTMLDocument.Iterator elementIterator) {
        try {
            final Rectangle rectangle = editorPane.modelToView(elementIterator.getStartOffset());
            if (rectangle == null) {
                return;
            }
            // the view is visible, scroll it to the
            // center of the current visible area.
            final Rectangle visibleRectangle = editorPane.getVisibleRect();
            // r.y -= (vis.height / 2);
            rectangle.height = visibleRectangle.height;
            editorPane.scrollRectToVisible(rectangle);
        } catch (BadLocationException e) {
            log.error(e.getMessage());
        }
    }


    /**
     * Scrolls the editorPane to the first anchor element whose id or name matches the given fragmentId.
     *
     * @param fragmentId
     */
    private void scrollToNamedAnchor(final String fragmentId) {
        final HTMLDocument doc = (HTMLDocument) this.editorPane.getDocument();
        for (HTMLDocument.Iterator iter = doc.getIterator(HTML.Tag.A); iter.isValid(); iter.next()) {
            final AttributeSet attributes = iter.getAttributes();
            if (matchesAny(fragmentId, (String) attributes.getAttribute(HTML.Attribute.NAME),
                           (String) attributes.getAttribute(HTML.Attribute.ID))) {
                scrollToElement(this.editorPane, iter);
                break;
            }
        }
    }

    private JEditorPane createJEditorPane() {
        final JEditorPane editorPane = new JEditorPane();
        editorPane.setBackground(Color.white);
        editorPane.setEditable(false);
        final HTMLEditorKit htmlKit = new HTMLEditorKit();
        // StyleSheet myStyleSheet = new StyleSheet();
        // String normalTextStyle = "font-size: 12px, font-family: georgia";
        // myStyleSheet.addRule("body {" + normalTextStyle + "}");
        // myStyleSheet.addRule("p {" + normalTextStyle + "}");
        // myStyleSheet.addRule("div {" + normalTextStyle + "}");
        // htmlKit.setStyleSheet(myStyleSheet);
        editorPane.setEditorKit(htmlKit);
        editorPane.addHyperlinkListener(this);
        editorPane.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(final KeyEvent keyEvent) {
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyPressed(final KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
                    ContentPane.this.navigator.gotoNextSpineSection(ContentPane.this);
                } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
                    ContentPane.this.navigator.gotoPreviousSpineSection(ContentPane.this);
//				} else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
//					ContentPane.this.gotoPreviousPage();
                } else if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
//					|| (keyEvent.getKeyCode() == KeyEvent.VK_DOWN)) {
                    ContentPane.this.gotoNextPage();
                }
            }
        });
        return editorPane;
    }

    void displayPage(final Resource resource) {
        displayPage(resource, 0);
    }

    void displayPage(final Resource resource, final int sectionPos) {
        if (resource == null) {
            return;
        }
        try {
            final HTMLDocument document = this.htmlDocumentFactory.getDocument(resource);
            if (document == null) {
                return;
            }
            this.currentResource = resource;
            this.editorPane.setDocument(document);
            scrollToCurrentPosition(sectionPos);
        } catch (Exception e) {
            log.error("When reading resource " + resource.getId() + "("
                      + resource.getHref() + ") :" + e.getMessage(), e);
        }
    }

    private void scrollToCurrentPosition(final int sectionPos) {
        if (sectionPos < 0) {
            this.editorPane.setCaretPosition(this.editorPane.getDocument().getLength());
        } else {
            this.editorPane.setCaretPosition(sectionPos);
        }
        if (sectionPos == 0) {
            this.scrollPane.getViewport().setViewPosition(new Point(0, 0));
        } else if (sectionPos < 0) {
            final int viewportHeight = this.scrollPane.getViewport().getHeight();
            final int scrollMax = this.scrollPane.getVerticalScrollBar().getMaximum();
            this.scrollPane.getViewport().setViewPosition(new Point(0, scrollMax - viewportHeight));
        }
    }

    @Override
    public void hyperlinkUpdate(final HyperlinkEvent event) {
        if (event.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
            return;
        }
        final URL url = event.getURL();
        if (url.getProtocol().toLowerCase().startsWith("http") && !"".equals(url.getHost())) {
            try {
                DesktopUtil.launchBrowser(event.getURL());
                return;
            } catch (DesktopUtil.BrowserLaunchException ex) {
                log.warn("Couldn't launch system web browser.", ex);
            }
        }
        final String resourceHref = calculateTargetHref(event.getURL());
        if (resourceHref.startsWith("#")) {
            scrollToNamedAnchor(resourceHref.substring(1));
            return;
        }

        final Resource resource = this.navigator.getBook().getResources().getByHref(resourceHref);
        if (resource == null) {
            log.error("Resource with url " + resourceHref + " not found");
        } else {
            this.navigator.gotoResource(resource, this);
        }
    }

    public void gotoPreviousPage() {
        final Point viewPosition = this.scrollPane.getViewport().getViewPosition();
        if (viewPosition.getY() <= 0) {
            this.navigator.gotoPreviousSpineSection(this);
            return;
        }
        final int viewportHeight = this.scrollPane.getViewport().getHeight();
        int newY = (int) viewPosition.getY();
        newY -= viewportHeight;
        newY = Math.max(0, newY - viewportHeight);
        this.scrollPane.getViewport().setViewPosition(
                new Point((int) viewPosition.getX(), newY));
    }

    public void gotoNextPage() {
        final Point viewPosition = this.scrollPane.getViewport().getViewPosition();
        final int viewportHeight = this.scrollPane.getViewport().getHeight();
        final int scrollMax = this.scrollPane.getVerticalScrollBar().getMaximum();
        if ((viewPosition.getY() + viewportHeight) >= scrollMax) {
            this.navigator.gotoNextSpineSection(this);
            return;
        }
        final int newY = ((int) viewPosition.getY()) + viewportHeight;
        this.scrollPane.getViewport().setViewPosition(
                new Point((int) viewPosition.getX(), newY));
    }


    /**
     * Transforms a link generated by a click on a link in a document to a resource href.
     * Property handles http encoded spaces and such.
     *
     * @param clickUrl
     * @return a link generated by a click on a link transformed into a document to a resource href.
     */
    private String calculateTargetHref(final URL clickUrl) {
        String resourceHref = clickUrl.toString();
        try {
            resourceHref = URLDecoder.decode(resourceHref,
                                             Constants.CHARACTER_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
        resourceHref = resourceHref.substring(ImageLoaderCache.IMAGE_URL_PREFIX
                                                      .length());

        if (resourceHref.startsWith("#")) {
            return resourceHref;
        }
        if ((this.currentResource != null)
            && StringUtils.isNotBlank(this.currentResource.getHref())) {
            final int lastSlashPos = this.currentResource.getHref().lastIndexOf('/');
            if (lastSlashPos >= 0) {
                resourceHref = this.currentResource.getHref().substring(0,
                                                                        lastSlashPos + 1)
                               + resourceHref;
            }
        }
        return resourceHref;
    }


    @Override
    public void navigationPerformed(final NavigationEvent navigationEvent) {
        if (navigationEvent.isBookChanged()) {
            initBook(navigationEvent.getCurrentBook());
        } else {
            if (navigationEvent.isResourceChanged()) {
                displayPage(navigationEvent.getCurrentResource(),
                            navigationEvent.getCurrentSectionPos());
            } else if (navigationEvent.isSectionPosChanged()) {
                this.editorPane.setCaretPosition(navigationEvent.getCurrentSectionPos());
            }
            if (StringUtils.isNotBlank(navigationEvent.getCurrentFragmentId())) {
                scrollToNamedAnchor(navigationEvent.getCurrentFragmentId());
            }
        }
    }


}
