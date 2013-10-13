package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.IOUtil;
import nl.siegmann.epublib.util.StringUtil;
import nl.siegmann.epublib.util.commons.io.XmlStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Represents a resource that is part of the epub.
 * A resource can be a html file, image, xml, etc.
 *
 * @author paul
 *
 */
public class Resource implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1043946707835004037L;
    private String id;
    private String title;
    private String href;
    private final String originalHref;
    private MediaType mediaType;
    private String inputEncoding = Constants.CHARACTER_ENCODING;
    private byte[] data;

    private String fileName;
    private long cachedSize;

    private static final Logger LOG = LoggerFactory.getLogger(Resource.class);

    /**
     * Creates an empty Resource with the given href.
     *
     * Assumes that if the data is of a text type (html/css/etc) then the encoding will be UTF-8
     *
     * @param href The location of the resource within the epub. Example: "chapter1.html".
     */
    public Resource(final String href) {
        this(null, new byte[0], href, MediatypeService.determineMediaType(href));
    }

    /**
     * Creates a Resource with the given data and MediaType.
     * The href will be automatically generated.
     *
     * Assumes that if the data is of a text type (html/css/etc) then the encoding will be UTF-8
     *
     * @param data The Resource's contents
     * @param mediaType The MediaType of the Resource
     */
    public Resource(final byte[] data, final MediaType mediaType) {
        this(null, data, null, mediaType);
    }

    /**
     * Creates a resource with the given data at the specified href.
     * The MediaType will be determined based on the href extension.
     *
     * Assumes that if the data is of a text type (html/css/etc) then the encoding will be UTF-8
     *
     * @see nl.siegmann.epublib.service.MediatypeService#determineMediaType(String)
     *
     * @param data The Resource's contents
     * @param href The location of the resource within the epub. Example: "chapter1.html".
     */
    public Resource(final byte[] data, final String href) {
        this(null, data, href, MediatypeService.determineMediaType(href), Constants.CHARACTER_ENCODING);
    }

    /**
     * Creates a resource with the data from the given Reader at the specified href.
     * The MediaType will be determined based on the href extension.
     *
     * @see nl.siegmann.epublib.service.MediatypeService#determineMediaType(String)
     *
     * @param in The Resource's contents
     * @param href The location of the resource within the epub. Example: "cover.jpg".
     */
    public Resource(final Reader in, final String href) throws IOException {
        this(null, IOUtil.toByteArray(in, Constants.CHARACTER_ENCODING), href, MediatypeService
                .determineMediaType(href), Constants.CHARACTER_ENCODING);
    }

    /**
     * Creates a resource with the data from the given InputStream at the specified href.
     * The MediaType will be determined based on the href extension.
     *
     * @see nl.siegmann.epublib.service.MediatypeService#determineMediaType(String)
     *
     * Assumes that if the data is of a text type (html/css/etc) then the encoding will be UTF-8
     *
     * It is recommended to us the {@link #Resource(Reader, String)} method for creating textual
     * (html/css/etc) resources to prevent encoding problems.
     * Use this method only for binary Resources like images, fonts, etc.
     *
     *
     * @param in The Resource's contents
     * @param href The location of the resource within the epub. Example: "cover.jpg".
     */
    public Resource(final InputStream in, final String href) throws IOException {
        this(null, IOUtil.toByteArray(in), href, MediatypeService.determineMediaType(href));
    }

    /**
     * Creates a Resource that tries to load the data, but falls back to lazy loading.
     *
     * If the size of the resource is known ahead of time we can use that to allocate
     * a matching byte[]. If this succeeds we can safely load the data.
     *
     * If it fails we leave the data null for now and it will be lazy-loaded when
     * it is accessed.
     *
     * @param in
     * @param fileName
     * @param length
     * @param href
     * @throws IOException
     */
    public Resource(final InputStream in, final String fileName, final int length, final String href)
            throws IOException {
        this(null, IOUtil.toByteArray(in, length), href, MediatypeService.determineMediaType(href));
        this.fileName = fileName;
        this.cachedSize = length;
    }

    /**

     /**
     * Creates a Lazy resource, by not actually loading the data for this entry.
     *
     * The data will be loaded on the first call to getData()
     *
     * @param fileName the fileName for the epub we're created from.
     * @param size the size of this resource.
     * @param href The resource's href within the epub.
     */
    public Resource(final String fileName, final long size, final String href) {
        this(null, null, href, MediatypeService.determineMediaType(href));
        this.fileName = fileName;
        this.cachedSize = size;
    }

    /**
     * Creates a resource with the given id, data, mediatype at the specified href.
     * Assumes that if the data is of a text type (html/css/etc) then the encoding will be UTF-8
     *
     * @param id The id of the Resource. Internal use only. Will be auto-generated if it has a null-value.
     * @param data The Resource's contents
     * @param href The location of the resource within the epub. Example: "chapter1.html".
     * @param mediaType The resources MediaType
     */
    public Resource(final String id, final byte[] data, final String href, final MediaType mediaType) {
        this(id, data, href, mediaType, Constants.CHARACTER_ENCODING);
    }


    /**
     * Creates a resource with the given id, data, mediatype at the specified href.
     * If the data is of a text type (html/css/etc) then it will use the given inputEncoding.
     *
     * @param id The id of the Resource. Internal use only. Will be auto-generated if it has a null-value.
     * @param data The Resource's contents
     * @param href The location of the resource within the epub. Example: "chapter1.html".
     * @param mediaType The resources MediaType
     * @param inputEncoding If the data is of a text type (html/css/etc) then it will use the given inputEncoding.
     */
    public Resource(final String id, final byte[] data, final String href, final MediaType mediaType,
                    final String inputEncoding) {
        this.id = id;
        this.href = href;
        this.originalHref = href;
        this.mediaType = mediaType;
        this.inputEncoding = inputEncoding;
        this.data = data;
    }

    /**
     * Gets the contents of the Resource as an InputStream.
     *
     * @return The contents of the Resource.
     *
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException {
        return isInitialized() ? new ByteArrayInputStream(getData()) : getResourceStream();
    }

    /**
     * Initializes the resource by loading its data into memory.
     *
     * @throws IOException
     */
    public void initialize() throws IOException {
        getData();
    }

    /**
     * The contents of the resource as a byte[]
     *
     * If this resource was lazy-loaded and the data was not yet loaded,
     * it will be loaded into memory at this point.
     *  This included opening the zip file, so expect a first load to be slow.
     *
     * @return The contents of the resource
     */
    byte[] getData() throws IOException {

        if (this.data == null) {

            LOG.info("Initializing lazy resource " + this.fileName + "#" + this.href);

            final InputStream in = getResourceStream();
            final byte[] readData = IOUtil.toByteArray(in, (int) this.cachedSize);
            if (readData == null) {
                throw new IOException("Could not lazy-load data.");
            }
            this.data = readData;
            this.data = IOUtil.toByteArray(in);

            in.close();
        }

        return this.data;
    }

    private InputStream getResourceStream() throws
                                            IOException {
        final ZipFile zipResource = new ZipFile(this.fileName);
        final ZipEntry zipEntry = zipResource.getEntry(this.originalHref);
        if (zipEntry == null) {
            zipResource.close();
            throw new IllegalStateException("Cannot find resources href in the epub file");
        }
        return new ResourceInputStream(zipResource.getInputStream(zipEntry), zipResource);
    }

    /**
     * Tells this resource to release its cached data.
     *
     * If this resource was not lazy-loaded, this is a no-op.
     */
    public void close() {
        if (this.fileName != null) {
            this.data = null;
        }
    }

    /**
     * Sets the data of the Resource.
     * If the data is a of a different type then the original data then make sure to change the MediaType.
     *
     * @param data
     */
    public void setData(final byte[] data) {
        this.data = data;
    }

    /**
     * Returns if the data for this resource has been loaded into memory.
     *
     * @return true if data was loaded.
     */
    boolean isInitialized() {
        return this.data != null;
    }

    /**
     * Returns the size of this resource in bytes.
     *
     * @return the size.
     */
    public long getSize() {
        if (this.data != null) {
            return this.data.length;
        }

        return this.cachedSize;
    }

    /**
     * If the title is found by scanning the underlying html document then it is cached here.
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the Resource's id: Make sure it is unique and a valid identifier.
     *
     * @param id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * The resources Id.
     *
     * Must be both unique within all the resources of this book and a valid identifier.
     * @return The resources Id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * The location of the resource within the contents folder of the epub file.
     *
     * Example:<br/>
     * images/cover.jpg<br/>
     * content/chapter1.xhtml<br/>
     *
     * @return The location of the resource within the contents folder of the epub file.
     */
    public String getHref() {
        return this.href;
    }

    /**
     * Sets the Resource's href.
     *
     * @param href
     */
    public void setHref(final String href) {
        this.href = href;
    }

    /**
     * The character encoding of the resource.
     * Is allowed to be null for non-text resources like images.
     *
     * @return The character encoding of the resource.
     */
    String getInputEncoding() {
        return this.inputEncoding;
    }

    /**
     * Sets the Resource's input character encoding.
     *
     * @param encoding
     */
    public void setInputEncoding(final String encoding) {
        this.inputEncoding = encoding;
    }

    /**
     * Gets the contents of the Resource as Reader.
     *
     * Does all sorts of smart things (courtesy of apache commons io XMLStreamREader) to handle encodings,
     * byte order markers, etc.
     *
     * @return the contents of the Resource as Reader.
     * @throws IOException
     */
    public Reader getReader() throws IOException {
        return new XmlStreamReader(new ByteArrayInputStream(getData()), getInputEncoding());
    }

    /**
     * Gets the hashCode of the Resource's href.
     *
     */
    public int hashCode() {
        return this.href.hashCode();
    }

    /**
     * Checks to see of the given resourceObject is a resource and whether its href is equal to this one.
     *
     * @return whether the given resourceObject is a resource and whether its href is equal to this one.
     */
    public boolean equals(final Object resourceObject) {
        if (!(resourceObject instanceof Resource)) {
            return false;
        }
        return this.href.equals(((Resource) resourceObject).getHref());
    }

    /**
     * This resource's mediaType.
     *
     * @return This resource's mediaType.
     */
    public MediaType getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(final MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String toString() {
        return StringUtil.toString("id", this.id,
                                   "title", this.title,
                                   "encoding", this.inputEncoding,
                                   "mediaType", this.mediaType,
                                   "href", this.href,
                                   "size", ((this.data == null) ? 0 : this.data.length));
    }
}
