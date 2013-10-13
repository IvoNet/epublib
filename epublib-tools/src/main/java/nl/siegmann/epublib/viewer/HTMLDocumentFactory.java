package nl.siegmann.epublib.viewer;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Creates swing HTML documents from resources.
 *
 * Between books the init(Book) function needs to be called in order for images to appear correctly.
 *
 * @author paul.siegmann
 *
 */
public class HTMLDocumentFactory implements NavigationEventListener {

    private static final Logger log = LoggerFactory.getLogger(HTMLDocumentFactory.class);

    // After opening the book we wait a while before we starting indexing the rest of the pages.
    // This way the book opens, everything settles down, and while the user looks at the cover page
    // the rest of the book is indexed.
    private static final int DOCUMENT_CACHE_INDEXER_WAIT_TIME = 500;

    private final ImageLoaderCache imageLoaderCache;
    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private final Lock cacheReadLock = this.cacheLock.readLock();
    private final Lock cacheWriteLock = this.cacheLock.writeLock();
    private final Map<String, HTMLDocument> documentCache = new HashMap<String, HTMLDocument>();
    private final MyHtmlEditorKit editorKit;

    public HTMLDocumentFactory(final Navigator navigator, final EditorKit editorKit) {
        this.editorKit = new MyHtmlEditorKit((HTMLEditorKit) editorKit);
        this.imageLoaderCache = new ImageLoaderCache(navigator);
        init(navigator.getBook());
        navigator.addNavigationEventListener(this);
    }

    public void init(final Book book) {
        if (book == null) {
            return;
        }
        this.imageLoaderCache.initBook(book);
        initDocumentCache(book);
    }

    private void putDocument(final Resource resource, final HTMLDocument document) {
        if (document == null) {
            return;
        }
        this.cacheWriteLock.lock();
        try {
            this.documentCache.put(resource.getHref(), document);
        } finally {
            this.cacheWriteLock.unlock();
        }
    }


    /**
     * Get the HTMLDocument representation of the resource.
     * If the resource is not an XHTML resource then it returns null.
     * It first tries to get the document from the cache.
     * If the document is not in the cache it creates a document from
     * the resource and adds it to the cache.
     *
     * @param resource
     * @return the HTMLDocument representation of the resource.
     */
    public HTMLDocument getDocument(final Resource resource) {
        HTMLDocument document = null;

        // try to get the document from  the cache
        this.cacheReadLock.lock();
        try {
            document = this.documentCache.get(resource.getHref());
        } finally {
            this.cacheReadLock.unlock();
        }

        // document was not in the cache, try to create it and add it to the cache
        if (document == null) {
            document = createDocument(resource);
            putDocument(resource, document);
        }

        // initialize the imageLoader for the specific document
        if (document != null) {
            this.imageLoaderCache.initImageLoader(document);
        }

        return document;
    }

    private String stripHtml(final String input) {
        final String result = removeControlTags(input);
//		result = result.replaceAll("<meta\\s+[^>]*http-equiv=\"Content-Type\"[^>]*>", "");
        return result;
    }

    /**
     * Quick and dirty stripper of all &lt;?...&gt; and &lt;!...&gt; tags as
     * these confuse the html viewer.
     *
     * @param input
     * @return the input stripped of control characters
     */
    private static String removeControlTags(final String input) {
        final StringBuilder result = new StringBuilder();
        boolean inControlTag = false;
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (inControlTag) {
                if (c == '>') {
                    inControlTag = false;
                }
            } else if ((c == '<')
                       && (i < (input.length() - 1))
                       && ((input.charAt(i + 1) == '!') || (input.charAt(i + 1) == '?'))) {
                inControlTag = true;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Creates a swing HTMLDocument from the given resource.
     *
     * If the resources is not of type XHTML then null is returned.
     *
     * @param resource
     * @return a swing HTMLDocument created from the given resource.
     */
    private HTMLDocument createDocument(final Resource resource) {
        HTMLDocument result = null;
        if (resource.getMediaType() != MediatypeService.XHTML) {
            return result;
        }
        try {
            final HTMLDocument document = (HTMLDocument) this.editorKit.createDefaultDocument();
            final MyParserCallback parserCallback = new MyParserCallback(document.getReader(0));
            final HTMLEditorKit.Parser parser = this.editorKit.getParser();
            String pageContent = IOUtils.toString(resource.getReader());
            pageContent = stripHtml(pageContent);
            document.remove(0, document.getLength());
            final Reader contentReader = new StringReader(pageContent);
            parser.parse(contentReader, parserCallback, true);
            parserCallback.flush();
            result = document;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    private void initDocumentCache(final Book book) {
        if (book == null) {
            return;
        }
        this.documentCache.clear();
        final Thread documentIndexerThread = new Thread(new DocumentIndexer(book), "DocumentIndexer");
        documentIndexerThread.setPriority(Thread.MIN_PRIORITY);
        documentIndexerThread.start();

//		addAllDocumentsToCache(book);
    }


    private class DocumentIndexer implements Runnable {
        private final Book book;

        public DocumentIndexer(final Book book) {
            this.book = book;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(DOCUMENT_CACHE_INDEXER_WAIT_TIME);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
            addAllDocumentsToCache(this.book);
        }

        private void addAllDocumentsToCache(final Book book) {
            for (final Resource resource : book.getResources().getAll()) {
                getDocument(resource);
            }
        }
    }


    @Override
    public void navigationPerformed(final NavigationEvent navigationEvent) {
        if (navigationEvent.isBookChanged() || navigationEvent.isResourceChanged()) {
            this.imageLoaderCache.clear();
        }
    }
}
