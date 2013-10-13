package nl.siegmann.epublib.browsersupport;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.util.StringUtil;

import java.util.EventObject;

/**
 * Used to tell NavigationEventListener just what kind of navigation action the user just did.
 *
 * @author paul
 *
 */
public class NavigationEvent extends EventObject {

    private static final long serialVersionUID = -6346750144308952762L;

    private Resource oldResource;
    private int oldSpinePos;
    private Navigator navigator;
    private Book oldBook;
    private int oldSectionPos;
    private String oldFragmentId;

    public NavigationEvent(final Object source) {
        super(source);
    }

    public NavigationEvent(final Object source, final Navigator navigator) {
        super(source);
        this.navigator = navigator;
        this.oldBook = navigator.getBook();
        this.oldFragmentId = navigator.getCurrentFragmentId();
        this.oldSectionPos = navigator.getCurrentSectionPos();
        this.oldResource = navigator.getCurrentResource();
        this.oldSpinePos = navigator.getCurrentSpinePos();
    }

    /**
     * The previous position within the section.
     *
     * @return The previous position within the section.
     */
    public int getOldSectionPos() {
        return this.oldSectionPos;
    }

    Navigator getNavigator() {
        return this.navigator;
    }

    String getOldFragmentId() {
        return this.oldFragmentId;
    }

    // package
    void setOldFragmentId(final String oldFragmentId) {
        this.oldFragmentId = oldFragmentId;
    }

    public Book getOldBook() {
        return this.oldBook;
    }

    // package
    void setOldPagePos(final int oldPagePos) {
        this.oldSectionPos = oldPagePos;
    }

    public int getCurrentSectionPos() {
        return this.navigator.getCurrentSectionPos();
    }

    public int getOldSpinePos() {
        return this.oldSpinePos;
    }

    public int getCurrentSpinePos() {
        return this.navigator.getCurrentSpinePos();
    }

    public String getCurrentFragmentId() {
        return this.navigator.getCurrentFragmentId();
    }

    public boolean isBookChanged() {
        if (this.oldBook == null) {
            return true;
        }
        return this.oldBook != this.navigator.getBook();
    }

    public boolean isSpinePosChanged() {
        return getOldSpinePos() != getCurrentSpinePos();
    }

    public boolean isFragmentChanged() {
        return StringUtil.equals(getOldFragmentId(), getCurrentFragmentId());
    }

    public Resource getOldResource() {
        return this.oldResource;
    }

    public Resource getCurrentResource() {
        return this.navigator.getCurrentResource();
    }

    public void setOldResource(final Resource oldResource) {
        this.oldResource = oldResource;
    }


    public void setOldSpinePos(final int oldSpinePos) {
        this.oldSpinePos = oldSpinePos;
    }


    public void setNavigator(final Navigator navigator) {
        this.navigator = navigator;
    }


    public void setOldBook(final Book oldBook) {
        this.oldBook = oldBook;
    }

    public Book getCurrentBook() {
        return getNavigator().getBook();
    }

    public boolean isResourceChanged() {
        return this.oldResource != getCurrentResource();
    }

    public String toString() {
        return StringUtil.toString(
                "oldSectionPos", this.oldSectionPos,
                "oldResource", this.oldResource,
                "oldBook", this.oldBook,
                "oldFragmentId", this.oldFragmentId,
                "oldSpinePos", this.oldSpinePos,
                "currentPagePos", getCurrentSectionPos(),
                "currentResource", getCurrentResource(),
                "currentBook", getCurrentBook(),
                "currentFragmentId", getCurrentFragmentId(),
                "currentSpinePos", getCurrentSpinePos()
        );
    }

    public boolean isSectionPosChanged() {
        return this.oldSectionPos != getCurrentSectionPos();
    }
}
