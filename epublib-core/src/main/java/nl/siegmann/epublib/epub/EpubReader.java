package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Reads an epub file.
 *
 * @author paul
 */
public class EpubReader {

    private static final Logger log = LoggerFactory.getLogger(EpubReader.class);
    private final BookProcessor bookProcessor = BookProcessor.IDENTITY_BOOKPROCESSOR;

    public Book readEpub(final InputStream in) throws IOException {
        return readEpub(in, Constants.CHARACTER_ENCODING);
    }

    public Book readEpub(final ZipInputStream in) throws IOException {
        return readEpub(in, Constants.CHARACTER_ENCODING);
    }

    public Book readEpub(final ZipFile zipfile) throws IOException {
        return readEpub(zipfile, Constants.CHARACTER_ENCODING);
    }

    /**
     * Read epub from inputstream
     *
     * @param in       the inputstream from which to read the epub
     * @param encoding the encoding to use for the html files within the epub
     * @return the Book as read from the inputstream
     */
    public Book readEpub(final InputStream in, final String encoding) throws IOException {
        return readEpub(new ZipInputStream(in), encoding);
    }

    /**
     * Reads this EPUB without loading all resources into memory.
     *
     * @param fileName        the file to load
     * @param encoding        the encoding for XHTML files
     * @param lazyLoadedTypes a list of the MediaType to load lazily
     * @return this Book without loading all resources into memory.
     */
    Book readEpubLazy(final String fileName, final String encoding, final List<MediaType> lazyLoadedTypes)
            throws IOException {
        Book result = new Book();
        final Resources resources = readLazyResources(fileName, encoding, lazyLoadedTypes);
        handleMimeType(result, resources);
        final String packageResourceHref = getPackageResourceHref(resources);
        final Resource packageResource = processPackageResource(packageResourceHref, result, resources);
        result.setOpfResource(packageResource);
        final Resource ncxResource = processNcxResource(packageResource, result);
        result.setNcxResource(ncxResource);
        result = postProcessBook(result);
        return result;
    }


    /**
     * Reads this EPUB without loading any resources into memory.
     *
     * @param fileName the file to load
     * @param encoding the encoding for XHTML files
     * @return this Book without loading all resources into memory.
     */
    public Book readEpubLazy(final String fileName, final String encoding) throws IOException {
        return readEpubLazy(fileName, encoding, Arrays.asList(MediatypeService.mediatypes));
    }

    Book readEpub(final ZipInputStream in, final String encoding) throws IOException {
        return readEpubResources(readResources(in, encoding));
    }

    Book readEpub(final ZipFile in, final String encoding) throws IOException {
        return readEpubResources(readResources(in, encoding));
    }

    Book readEpubResources(final Resources resources) throws IOException {
        Book result = new Book();
        handleMimeType(result, resources);
        final String packageResourceHref = getPackageResourceHref(resources);
        final Resource packageResource = processPackageResource(packageResourceHref, result, resources);
        result.setOpfResource(packageResource);
        final Resource ncxResource = processNcxResource(packageResource, result);
        result.setNcxResource(ncxResource);
        result = postProcessBook(result);
        return result;
    }

    private Book postProcessBook(Book book) throws IOException {
        if (this.bookProcessor != null) {
            book = this.bookProcessor.processBook(book);
        }
        return book;
    }

    private Resource processNcxResource(final Resource packageResource, final Book book) {
        return NCXDocument.read(book, this);
    }

    private Resource processPackageResource(final String packageResourceHref, final Book book,
                                            final Resources resources) throws IOException {
        final Resource packageResource = resources.remove(packageResourceHref);
        try {
            PackageDocumentReader.read(packageResource, this, book, resources);
        } catch (Exception e) {
            throw new IOException("Read error", e);
        }
        return packageResource;
    }

    private String getPackageResourceHref(final Resources resources) {
        final String defaultResult = "OEBPS/content.opf";
        String result = defaultResult;

        final Resource containerResource = resources.remove("META-INF/container.xml");
        if (containerResource == null) {
            return result;
        }
        try {
            final Document document = ResourceUtil.getAsDocument(containerResource);
            final Element rootFileElement = (Element) ((Element) document.getDocumentElement()
                                                                         .getElementsByTagName("rootfiles")
                                                                         .item(0)).getElementsByTagName("rootfile")
                                                                                  .item(0);
            result = rootFileElement.getAttribute("full-path");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (StringUtil.isBlank(result)) {
            result = defaultResult;
        }
        return result;
    }

    private void handleMimeType(final Book result, final Resources resources) {
        resources.remove("mimetype");
    }

    private Resources readLazyResources(final String fileName, final String defaultHtmlEncoding,
                                        final List<MediaType> lazyLoadedTypes) throws IOException {

        final ZipInputStream in = new ZipInputStream(new FileInputStream(fileName));

        final Resources result = new Resources();
        for (ZipEntry zipEntry = in.getNextEntry(); zipEntry != null; zipEntry = in.getNextEntry()) {
            if (zipEntry.isDirectory()) {
                continue;
            }

            final String href = zipEntry.getName();
            final MediaType mediaType = MediatypeService.determineMediaType(href);

            final Resource resource;

            resource = lazyLoadedTypes.contains(mediaType) ? new Resource(fileName, zipEntry.getSize(), href) :
                       new Resource(in, fileName, (int) zipEntry.getSize(), href);

            if (resource.getMediaType() == MediatypeService.XHTML) {
                resource.setInputEncoding(defaultHtmlEncoding);
            }
            result.add(resource);
        }

        return result;
    }

    private Resources readResources(final ZipInputStream in, final String defaultHtmlEncoding) throws IOException {
        final Resources result = new Resources();
        for (ZipEntry zipEntry = in.getNextEntry(); zipEntry != null; zipEntry = in.getNextEntry()) {
            if (zipEntry.isDirectory()) {
                continue;
            }
            final Resource resource = ResourceUtil.createResource(zipEntry, in);
            if (resource.getMediaType() == MediatypeService.XHTML) {
                resource.setInputEncoding(defaultHtmlEncoding);
            }
            result.add(resource);
        }
        return result;
    }

    private Resources readResources(final ZipFile zipFile, final String defaultHtmlEncoding) throws IOException {
        final Resources result = new Resources();
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            final ZipEntry zipEntry = entries.nextElement();
            if ((zipEntry != null) && !zipEntry.isDirectory()) {
                final Resource resource = ResourceUtil.createResource(zipEntry, zipFile.getInputStream(zipEntry));
                if (resource.getMediaType() == MediatypeService.XHTML) {
                    resource.setInputEncoding(defaultHtmlEncoding);
                }
                result.add(resource);
            }
        }

        return result;
    }
}
