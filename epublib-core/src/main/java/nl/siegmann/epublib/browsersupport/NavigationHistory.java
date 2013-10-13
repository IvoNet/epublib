package nl.siegmann.epublib.browsersupport;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import java.util.ArrayList;
import java.util.List;


/**
 * A history of the user's locations with the epub.
 *
 * @author paul.siegmann
 *
 */
public class NavigationHistory implements NavigationEventListener {

    private static final int DEFAULT_MAX_HISTORY_SIZE = 1000;
    private static final long DEFAULT_HISTORY_WAIT_TIME = 1000;

    private static class Location {
        private String href;

        public Location(final String href) {
            this.href = href;
        }

        @SuppressWarnings("unused")
        public void setHref(final String href) {
            this.href = href;
        }

        public String getHref() {
            return this.href;
        }
    }

    private long lastUpdateTime;
    private List<Location> locations = new ArrayList<Location>();
    private final Navigator navigator;
    private int currentPos = -1;
    private int currentSize;
    private int maxHistorySize = DEFAULT_MAX_HISTORY_SIZE;
    private long historyWaitTime = DEFAULT_HISTORY_WAIT_TIME;

    public NavigationHistory(final Navigator navigator) {
        this.navigator = navigator;
        navigator.addNavigationEventListener(this);
        initBook(navigator.getBook());
    }

    public int getCurrentPos() {
        return this.currentPos;
    }


    public int getCurrentSize() {
        return this.currentSize;
    }

    void initBook(final Book book) {
        if (book == null) {
            return;
        }
        this.locations = new ArrayList<Location>();
        this.currentPos = -1;
        this.currentSize = 0;
        if (this.navigator.getCurrentResource() != null) {
            addLocation(this.navigator.getCurrentResource().getHref());
        }
    }

    /**
     * If the time between a navigation event is less than the historyWaitTime then the new location is not added to
     * the history.
     * When a user is rapidly viewing many pages using the slider we do not want all of them to be added to the history.
     *
     * @return the time we wait before adding the page to the history
     */
    public long getHistoryWaitTime() {
        return this.historyWaitTime;
    }

    public void setHistoryWaitTime(final long historyWaitTime) {
        this.historyWaitTime = historyWaitTime;
    }

    void addLocation(final Resource resource) {
        if (resource == null) {
            return;
        }
        addLocation(resource.getHref());
    }

    /**
     * Adds the location after the current position.
     * If the currentposition is not the end of the list then the elements between the current element and the end of
     * the list will be discarded.
     * Does nothing if the new location matches the current location.
     * <br/>
     * If this nr of locations becomes larger then the historySize then the first item(s) will be removed.
     *
     * @param location
     */
    void addLocation(final Location location) {
        // do nothing if the new location matches the current location
        if (!(this.locations.isEmpty()) &&
            location.getHref().equals(this.locations.get(this.currentPos).getHref())) {
            return;
        }
        this.currentPos++;
        if (this.currentPos == this.currentSize) {
            this.locations.add(location);
            checkHistorySize();
        } else {
            this.locations.set(this.currentPos, location);
        }
        this.currentSize = this.currentPos + 1;
    }

    /**
     * Removes all elements that are too much for the maxHistorySize out of the history.
     *
     */
    private void checkHistorySize() {
        while (this.locations.size() > this.maxHistorySize) {
            this.locations.remove(0);
            this.currentSize--;
            this.currentPos--;
        }
    }

    public void addLocation(final String href) {
        addLocation(new Location(href));
    }

    private String getLocationHref(final int pos) {
        if ((pos < 0) || (pos >= this.locations.size())) {
            return null;
        }
        return this.locations.get(this.currentPos).getHref();
    }

    /**
     * Moves the current positions delta positions.
     *
     * move(-1) to go one position back in history.<br/>
     * move(1) to go one position forward.<br/>
     *
     * @param delta
     *
     * @return Whether we actually moved. If the requested value is illegal it will return false, true otherwise.
     */
    public boolean move(final int delta) {
        if (((this.currentPos + delta) < 0)
            || ((this.currentPos + delta) >= this.currentSize)) {
            return false;
        }
        this.currentPos += delta;
        this.navigator.gotoResource(getLocationHref(this.currentPos), this);
        return true;
    }


    /**
     * If this is not the source of the navigationEvent then the addLocation will be called with the href of the
     * currentResource in the navigationEvent.
     */
    @Override
    public void navigationPerformed(final NavigationEvent navigationEvent) {
        if (this == navigationEvent.getSource()) {
            return;
        }
        if (navigationEvent.getCurrentResource() == null) {
            return;
        }

        if ((System.currentTimeMillis() - this.lastUpdateTime) > this.historyWaitTime) {
            // if the user scrolled rapidly through the pages then the last page will not be added to the history. We
            // fix that here:
            addLocation(navigationEvent.getOldResource());

            addLocation(navigationEvent.getCurrentResource().getHref());
        }
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public String getCurrentHref() {
        if ((this.currentPos < 0) || (this.currentPos >= this.locations.size())) {
            return null;
        }
        return this.locations.get(this.currentPos).getHref();
    }

    public void setMaxHistorySize(final int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }

    public int getMaxHistorySize() {
        return this.maxHistorySize;
    }
}
