package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.epub.PackageDocumentBase;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * A Date used by the book's metadata.
 *
 * Examples: creation-date, modification-date, etc
 *
 * @author paul
 *
 */
public class Date implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7533866830395120136L;

    public enum Event {
        PUBLICATION("publication"),
        MODIFICATION("modification"),
        CREATION("creation");

        private final String value;

        Event(final String v) {
            this.value = v;
        }

        public static Event fromValue(final String v) {
            for (final Event c : Event.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            return null;
        }

        public String toString() {
            return this.value;
        }
    }

    private Event event;
    private String dateString;

    public Date(final java.util.Date date) {
        this(date, (Event) null);
    }

    public Date(final String dateString) {
        this(dateString, (Event) null);
    }

    private Date(final java.util.Date date, final Event event) {
        this((new SimpleDateFormat(PackageDocumentBase.dateFormat)).format(date), event);
    }

    private Date(final String dateString, final Event event) {
        this.dateString = dateString;
        this.event = event;
    }

    public Date(final java.util.Date date, final String event) {
        this((new SimpleDateFormat(PackageDocumentBase.dateFormat)).format(date), event);
    }

    public Date(final String dateString, final String event) {
        this(checkDate(dateString), Event.fromValue(event));
        this.dateString = dateString;
    }

    private static String checkDate(final String dateString) {
        if (dateString == null) {
            throw new IllegalArgumentException("Cannot create a date from a blank string");
        }
        return dateString;
    }

    public String getValue() {
        return this.dateString;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(final Event event) {
        this.event = event;
    }

    public String toString() {
        if (this.event == null) {
            return this.dateString;
        }
        return "" + this.event + ":" + this.dateString;
    }
}

