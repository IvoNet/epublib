package nl.siegmann.epublib.viewer;

import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.util.CollectionUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a trick to get the JEditorKit to load its images from the epub file instead of from the given url.
 *
 * This class is installed as the JEditorPane's image cache.
 * Whenever it is requested an image it will try to load that image from the epub.
 *
 * Can be shared by multiple documents but can only be <em>used</em> by one document at the time because of the
 * currentFolder issue.
 *
 * @author paul
 *
 */
class ImageLoaderCache extends Dictionary<String, Image> {

    public static final String IMAGE_URL_PREFIX = "http:/";

    private static final Logger log = LoggerFactory.getLogger(ImageLoaderCache.class);

    private final Map<String, Image> cache = new HashMap<String, Image>();
    private Book book;
    private String currentFolder = "";
    private final Navigator navigator;

    public ImageLoaderCache(final Navigator navigator) {
        this.navigator = navigator;
        initBook(navigator.getBook());
    }

    public void initBook(final Book book) {
        if (book == null) {
            return;
        }
        this.book = book;
        this.cache.clear();
        this.currentFolder = "";
    }

    void setContextResource(final Resource resource) {
        if (resource == null) {
            return;
        }
        if (StringUtils.isNotBlank(resource.getHref())) {
            final int lastSlashPos = resource.getHref().lastIndexOf('/');
            if (lastSlashPos >= 0) {
                this.currentFolder = resource.getHref().substring(0, lastSlashPos + 1);
            }
        }
    }

    public void initImageLoader(final HTMLDocument document) {
        try {
            document.setBase(new URL(IMAGE_URL_PREFIX));
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        }
        setContextResource(this.navigator.getCurrentResource());
        document.getDocumentProperties().put("imageCache", this);
    }


    private String getResourceHref(final String requestUrl) {
        String resourceHref = requestUrl.substring(IMAGE_URL_PREFIX.length());
        resourceHref = this.currentFolder + resourceHref;
        resourceHref = FilenameUtils.normalize(resourceHref);
        // normalize uses the SYSTEM_SEPARATOR, which on windows is a '\'
        // replace with '/' to make it href '/'
        resourceHref = resourceHref.replaceAll("\\\\", "/");
        return resourceHref;
    }

    /**
     * Create an Image from the data of the given resource.
     *
     * @param imageResource
     * @return
     */
    private Image createImage(final Resource imageResource) {
        Image result = null;
        try {
            result = ImageIO.read(imageResource.getInputStream());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return result;
    }

    @Override
    public Image get(final Object key) {
        if (this.book == null) {
            return null;
        }

        final String imageURL = key.toString();

        // see if the image is already in the cache
        Image result = this.cache.get(imageURL);
        if (result != null) {
            return result;
        }

        // get the image resource href
        final String resourceHref = getResourceHref(imageURL);

        // find the image resource in the book resources
        final Resource imageResource = this.book.getResources().getByHref(resourceHref);
        if (imageResource == null) {
            return result;
        }

        // create an image from the resource and add it to the cache
        result = createImage(imageResource);
        if (result != null) {
            this.cache.put(imageURL, result);
        }

        return result;
    }

    @Override
    public int size() {
        return this.cache.size();
    }

    @Override
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }

    @Override
    public Enumeration<String> keys() {
        return CollectionUtil.createEnumerationFromIterator(this.cache.keySet().iterator());
    }

    @Override
    public Enumeration<Image> elements() {
        return CollectionUtil.createEnumerationFromIterator(this.cache.values().iterator());
    }

    @Override
    public Image put(final String key, final Image value) {
        return this.cache.put(key, value);
    }

    @Override
    public Image remove(final Object key) {
        return this.cache.remove(key);
    }

    /**
     * Clears the image cache.
     */
    public void clear() {
        this.cache.clear();
    }

    public String toString() {
        return this.cache.toString();
    }
}