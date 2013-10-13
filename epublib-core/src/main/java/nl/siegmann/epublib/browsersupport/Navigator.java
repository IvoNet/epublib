package nl.siegmann.epublib.browsersupport;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A helper class for epub browser applications.
 *
 * It helps moving from one resource to the other, from one resource to the other and keeping other
 * elements of the application up-to-date by calling the NavigationEventListeners.
 *
 * @author paul
 *
 */
public class Navigator implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1076126986424925474L;
    private Book book;
    private int currentSpinePos;
    private Resource currentResource;
    private int currentPagePos;
    private String currentFragmentId;

    private final List<NavigationEventListener> eventListeners = new ArrayList<NavigationEventListener>();

    public Navigator() {
        this(null);
    }

    public Navigator(final Book book) {
        this.book = book;
        this.currentSpinePos = 0;
        if (book != null) {
            this.currentResource = book.getCoverPage();
        }
        this.currentPagePos = 0;
    }

    private synchronized void handleEventListeners(final NavigationEvent navigationEvent) {
        for (final NavigationEventListener navigationEventListener : this.eventListeners) {
            navigationEventListener.navigationPerformed(navigationEvent);
        }
    }

    public boolean addNavigationEventListener(final NavigationEventListener navigationEventListener) {
        return this.eventListeners.add(navigationEventListener);
    }


    public boolean removeNavigationEventListener(final NavigationEventListener navigationEventListener) {
        return this.eventListeners.remove(navigationEventListener);
    }

    public int gotoFirstSpineSection(final Object source) {
        return gotoSpineSection(0, source);
    }

    public int gotoPreviousSpineSection(final Object source) {
        return gotoPreviousSpineSection(0, source);
    }

    public int gotoPreviousSpineSection(final int pagePos, final Object source) {
        return (this.currentSpinePos < 0) ? gotoSpineSection(0, pagePos, source) :
               gotoSpineSection(this.currentSpinePos - 1, pagePos, source);
    }

    public boolean hasNextSpineSection() {
        return (this.currentSpinePos < (this.book.getSpine().size() - 1));
    }

    public boolean hasPreviousSpineSection() {
        return (this.currentSpinePos > 0);
    }

    public int gotoNextSpineSection(final Object source) {
        return (this.currentSpinePos < 0) ? gotoSpineSection(0, source) :
               gotoSpineSection(this.currentSpinePos + 1, source);
    }

    public int gotoResource(final String resourceHref, final Object source) {
        final Resource resource = this.book.getResources().getByHref(resourceHref);
        return gotoResource(resource, source);
    }


    public int gotoResource(final Resource resource, final Object source) {
        return gotoResource(resource, 0, null, source);
    }

    public int gotoResource(final Resource resource, final String fragmentId, final Object source) {
        return gotoResource(resource, 0, fragmentId, source);
    }

    public int gotoResource(final Resource resource, final int pagePos, final Object source) {
        return gotoResource(resource, pagePos, null, source);
    }

    int gotoResource(final Resource resource, final int pagePos, final String fragmentId, final Object source) {
        if (resource == null) {
            return -1;
        }
        final NavigationEvent navigationEvent = new NavigationEvent(source, this);
        this.currentResource = resource;
        this.currentSpinePos = this.book.getSpine().getResourceIndex(this.currentResource);
        this.currentPagePos = pagePos;
        this.currentFragmentId = fragmentId;
        handleEventListeners(navigationEvent);

        return this.currentSpinePos;
    }

    public int gotoResourceId(final String resourceId, final Object source) {
        return gotoSpineSection(this.book.getSpine().findFirstResourceById(resourceId), source);
    }

    public int gotoSpineSection(final int newSpinePos, final Object source) {
        return gotoSpineSection(newSpinePos, 0, source);
    }

    /**
     * Go to a specific section.
     * Illegal spine positions are silently ignored.
     *
     * @param newSpinePos
     * @param source
     * @return The current position within the spine
     */
    int gotoSpineSection(final int newSpinePos, final int newPagePos, final Object source) {
        if (newSpinePos == this.currentSpinePos) {
            return this.currentSpinePos;
        }
        if ((newSpinePos < 0) || (newSpinePos >= this.book.getSpine().size())) {
            return this.currentSpinePos;
        }
        final NavigationEvent navigationEvent = new NavigationEvent(source, this);
        this.currentSpinePos = newSpinePos;
        this.currentPagePos = newPagePos;
        this.currentResource = this.book.getSpine().getResource(this.currentSpinePos);
        handleEventListeners(navigationEvent);
        return this.currentSpinePos;
    }

    public int gotoLastSpineSection(final Object source) {
        return gotoSpineSection(this.book.getSpine().size() - 1, source);
    }

    public void gotoBook(final Book book, final Object source) {
        final NavigationEvent navigationEvent = new NavigationEvent(source, this);
        this.book = book;
        this.currentFragmentId = null;
        this.currentPagePos = 0;
        this.currentResource = null;
        this.currentSpinePos = book.getSpine().getResourceIndex(this.currentResource);
        handleEventListeners(navigationEvent);
    }


    /**
     * The current position within the spine.
     *
     * @return something < 0 if the current position is not within the spine.
     */
    public int getCurrentSpinePos() {
        return this.currentSpinePos;
    }

    public Resource getCurrentResource() {
        return this.currentResource;
    }

    /**
     * Sets the current index and resource without calling the eventlisteners.
     *
     * If you want the eventListeners called use gotoSection(index);
     *
     * @param currentIndex
     */
    public void setCurrentSpinePos(final int currentIndex) {
        this.currentSpinePos = currentIndex;
        this.currentResource = this.book.getSpine().getResource(currentIndex);
    }

    public Book getBook() {
        return this.book;
    }

    /**
     * Sets the current index and resource without calling the eventlisteners.
     *
     * If you want the eventListeners called use gotoSection(index);
     *
     */
    public int setCurrentResource(final Resource currentResource) {
        this.currentSpinePos = this.book.getSpine().getResourceIndex(currentResource);
        this.currentResource = currentResource;
        return this.currentSpinePos;
    }

    public String getCurrentFragmentId() {
        return this.currentFragmentId;
    }

    public int getCurrentSectionPos() {
        return this.currentPagePos;
    }
}
