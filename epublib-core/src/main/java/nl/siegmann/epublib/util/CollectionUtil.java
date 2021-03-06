package nl.siegmann.epublib.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class CollectionUtil {

    /**
     * Wraps an Enumeration around an Iterator
     * @author paul.siegmann
     *
     * @param <T>
     */
    private static class IteratorEnumerationAdapter<T> implements Enumeration<T> {
        private final Iterator<T> iterator;

        public IteratorEnumerationAdapter(final Iterator<T> iter) {
            this.iterator = iter;
        }

        @Override
        public boolean hasMoreElements() {
            return this.iterator.hasNext();
        }

        @Override
        public T nextElement() {
            return this.iterator.next();
        }
    }

    /**
     * Creates an Enumeration out of the given Iterator.
     * @param <T>
     * @param it
     * @return an Enumeration created out of the given Iterator.
     */
    public static <T> Enumeration<T> createEnumerationFromIterator(final Iterator<T> it) {
        return new IteratorEnumerationAdapter<T>(it);
    }


    /**
     * Returns the first element of the list, null if the list is null or empty.
     *
     * @param <T>
     * @param list
     * @return the first element of the list, null if the list is null or empty.
     */
    public static <T> T first(final List<T> list) {
        if ((list == null) || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
