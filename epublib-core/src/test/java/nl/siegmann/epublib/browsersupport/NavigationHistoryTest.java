package nl.siegmann.epublib.browsersupport;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import java.util.HashMap;
import java.util.Map;

public class NavigationHistoryTest extends TestCase {

    private static final Resource mockResource = new Resource("mockResource.html");

    private static class MockBook extends Book {
        @Override
        public Resource getCoverPage() {
            return mockResource;
        }
    }


    private static class MockSectionWalker extends Navigator {

        private final Map<String, Resource> resourcesByHref = new HashMap<String, Resource>();

        public MockSectionWalker(final Book book) {
            super(book);
            this.resourcesByHref.put(mockResource.getHref(), mockResource);
        }

        @Override
        public int gotoFirstSpineSection(final Object source) {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        @Override
        public int gotoPreviousSpineSection(final Object source) {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        @Override
        public boolean hasNextSpineSection() {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        @Override
        public boolean hasPreviousSpineSection() {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        @Override
        public int gotoNextSpineSection(final Object source) {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        @Override
        public int gotoResource(final String resourceHref, final Object source) {
            return -1;
        }

        @Override
        public int gotoResource(final Resource resource, final Object source) {
            return -1;
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        public boolean equals(final Object obj) {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        @Override
        public int gotoResourceId(final String resourceId, final Object source) {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        @Override
        public int gotoSpineSection(final int newIndex, final Object source) {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        @Override
        public int gotoLastSpineSection(final Object source) {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        @Override
        public int getCurrentSpinePos() {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        @Override
        public Resource getCurrentResource() {
            return this.resourcesByHref.values().iterator().next();
        }

        @Override
        public void setCurrentSpinePos(final int currentIndex) {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        @Override
        public int setCurrentResource(final Resource currentResource) {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        public String toString() {
            throw new UnsupportedOperationException("Method not supported in mock implementation");
        }

        public Resource getMockResource() {
            return mockResource;
        }
    }

    public void test1() {
        final MockSectionWalker navigator = new MockSectionWalker(new MockBook());
        final NavigationHistory browserHistory = new NavigationHistory(navigator);

        assertEquals(navigator.getCurrentResource().getHref(), browserHistory.getCurrentHref());
        assertEquals(0, browserHistory.getCurrentPos());
        assertEquals(1, browserHistory.getCurrentSize());

        browserHistory.addLocation(navigator.getMockResource().getHref());
        assertEquals(0, browserHistory.getCurrentPos());
        assertEquals(1, browserHistory.getCurrentSize());

        browserHistory.addLocation("bar");
        assertEquals(1, browserHistory.getCurrentPos());
        assertEquals(2, browserHistory.getCurrentSize());

        browserHistory.addLocation("bar");
        assertEquals(1, browserHistory.getCurrentPos());
        assertEquals(2, browserHistory.getCurrentSize());

        browserHistory.move(1);
        assertEquals(1, browserHistory.getCurrentPos());
        assertEquals(2, browserHistory.getCurrentSize());

        browserHistory.addLocation("bar");
        assertEquals(1, browserHistory.getCurrentPos());
        assertEquals(2, browserHistory.getCurrentSize());

        browserHistory.move(-1);
        assertEquals(0, browserHistory.getCurrentPos());
        assertEquals(2, browserHistory.getCurrentSize());

        browserHistory.move(0);
        assertEquals(0, browserHistory.getCurrentPos());
        assertEquals(2, browserHistory.getCurrentSize());

        browserHistory.move(-1);
        assertEquals(0, browserHistory.getCurrentPos());
        assertEquals(2, browserHistory.getCurrentSize());

        browserHistory.move(1);
        assertEquals(1, browserHistory.getCurrentPos());
        assertEquals(2, browserHistory.getCurrentSize());

        browserHistory.move(1);
        assertEquals(1, browserHistory.getCurrentPos());
        assertEquals(2, browserHistory.getCurrentSize());
    }


    public void test2() {
        final MockSectionWalker navigator = new MockSectionWalker(new MockBook());
        final NavigationHistory browserHistory = new NavigationHistory(navigator);

        assertEquals(0, browserHistory.getCurrentPos());
        assertEquals(1, browserHistory.getCurrentSize());

        browserHistory.addLocation("green");
        assertEquals(1, browserHistory.getCurrentPos());
        assertEquals(2, browserHistory.getCurrentSize());

        browserHistory.addLocation("blue");
        assertEquals(2, browserHistory.getCurrentPos());
        assertEquals(3, browserHistory.getCurrentSize());

        browserHistory.addLocation("yellow");
        assertEquals(3, browserHistory.getCurrentPos());
        assertEquals(4, browserHistory.getCurrentSize());

        browserHistory.addLocation("orange");
        assertEquals(4, browserHistory.getCurrentPos());
        assertEquals(5, browserHistory.getCurrentSize());

        browserHistory.move(-1);
        assertEquals(3, browserHistory.getCurrentPos());
        assertEquals(5, browserHistory.getCurrentSize());

        browserHistory.move(-1);
        assertEquals(2, browserHistory.getCurrentPos());
        assertEquals(5, browserHistory.getCurrentSize());

        browserHistory.addLocation("taupe");
        assertEquals(3, browserHistory.getCurrentPos());
        assertEquals(4, browserHistory.getCurrentSize());

    }

    public void test3() {
        final MockSectionWalker navigator = new MockSectionWalker(new MockBook());
        final NavigationHistory browserHistory = new NavigationHistory(navigator);

        assertEquals(0, browserHistory.getCurrentPos());
        assertEquals(1, browserHistory.getCurrentSize());

        browserHistory.addLocation("red");
        browserHistory.addLocation("green");
        browserHistory.addLocation("blue");

        assertEquals(3, browserHistory.getCurrentPos());
        assertEquals(4, browserHistory.getCurrentSize());

        browserHistory.move(-1);
        assertEquals(2, browserHistory.getCurrentPos());
        assertEquals(4, browserHistory.getCurrentSize());

        browserHistory.move(-1);
        assertEquals(1, browserHistory.getCurrentPos());
        assertEquals(4, browserHistory.getCurrentSize());

        browserHistory.move(-1);
        assertEquals(0, browserHistory.getCurrentPos());
        assertEquals(4, browserHistory.getCurrentSize());

        browserHistory.move(-1);
        assertEquals(0, browserHistory.getCurrentPos());
        assertEquals(4, browserHistory.getCurrentSize());

        browserHistory.addLocation("taupe");
        assertEquals(1, browserHistory.getCurrentPos());
        assertEquals(2, browserHistory.getCurrentSize());
    }
}
