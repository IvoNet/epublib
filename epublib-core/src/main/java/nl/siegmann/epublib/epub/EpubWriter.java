package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Generates an epub file. Not thread-safe, single use object.
 *
 * @author paul
 *
 */
public class EpubWriter {

    private static final Logger log = LoggerFactory.getLogger(EpubWriter.class);

    // package
    static final String EMPTY_NAMESPACE_PREFIX = "";

    private BookProcessor bookProcessor = BookProcessor.IDENTITY_BOOKPROCESSOR;

    public EpubWriter() {
        this(BookProcessor.IDENTITY_BOOKPROCESSOR);
    }


    public EpubWriter(final BookProcessor bookProcessor) {
        this.bookProcessor = bookProcessor;
    }


    public void write(Book book, final OutputStream out) throws IOException {
        book = processBook(book);
        final ZipOutputStream resultStream = new ZipOutputStream(out);
        writeMimeType(resultStream);
        writeContainer(resultStream);
        initTOCResource(book);
        writeResources(book, resultStream);
        writePackageDocument(book, resultStream);
        resultStream.close();
    }

    private Book processBook(Book book) {
        if (this.bookProcessor != null) {
            book = this.bookProcessor.processBook(book);
        }
        return book;
    }

    private void initTOCResource(final Book book) {
        final Resource tocResource;
        try {
            tocResource = NCXDocument.createNCXResource(book);
            final Resource currentTocResource = book.getSpine().getTocResource();
            if (currentTocResource != null) {
                book.getResources().remove(currentTocResource.getHref());
            }
            book.getSpine().setTocResource(tocResource);
            book.getResources().add(tocResource);
        } catch (Exception e) {
            log.error("Error writing table of contents: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    private void writeResources(final Book book, final ZipOutputStream resultStream) throws IOException {
        for (final Resource resource : book.getResources().getAll()) {
            writeResource(resource, resultStream);
        }
    }

    /**
     * Writes the resource to the resultStream.
     *
     * @param resource
     * @param resultStream
     * @throws IOException
     */
    private void writeResource(final Resource resource, final ZipOutputStream resultStream)
            throws IOException {
        if (resource == null) {
            return;
        }
        try {
            resultStream.putNextEntry(new ZipEntry("OEBPS/" + resource.getHref()));
            final InputStream inputStream = resource.getInputStream();
            IOUtil.copy(inputStream, resultStream);
            inputStream.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private void writePackageDocument(final Book book, final ZipOutputStream resultStream) throws IOException {
        resultStream.putNextEntry(new ZipEntry("OEBPS/content.opf"));
        final XmlSerializer xmlSerializer = EpubProcessorSupport.createXmlSerializer(resultStream);
        PackageDocumentWriter.write(this, xmlSerializer, book);
        xmlSerializer.flush();
//		String resultAsString = result.toString();
//		resultStream.write(resultAsString.getBytes(Constants.ENCODING));
    }

    /**
     * Writes the META-INF/container.xml file.
     *
     * @param resultStream
     * @throws IOException
     */
    private void writeContainer(final ZipOutputStream resultStream) throws IOException {
        resultStream.putNextEntry(new ZipEntry("META-INF/container.xml"));
        final Writer out = new OutputStreamWriter(resultStream);
        out.write("<?xml version=\"1.0\"?>\n");
        out.write("<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">\n");
        out.write("\t<rootfiles>\n");
        out.write("\t\t<rootfile full-path=\"OEBPS/content.opf\" media-type=\"application/oebps-package+xml\"/>\n");
        out.write("\t</rootfiles>\n");
        out.write("</container>");
        out.flush();
    }

    /**
     * Stores the mimetype as an uncompressed file in the ZipOutputStream.
     *
     * @param resultStream
     * @throws IOException
     */
    private void writeMimeType(final ZipOutputStream resultStream) throws IOException {
        final ZipEntry mimetypeZipEntry = new ZipEntry("mimetype");
        mimetypeZipEntry.setMethod(ZipEntry.STORED);
        final byte[] mimetypeBytes = MediatypeService.EPUB.getName().getBytes();
        mimetypeZipEntry.setSize(mimetypeBytes.length);
        mimetypeZipEntry.setCrc(calculateCrc(mimetypeBytes));
        resultStream.putNextEntry(mimetypeZipEntry);
        resultStream.write(mimetypeBytes);
    }

    private long calculateCrc(final byte[] data) {
        final CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }

    String getNcxId() {
        return "ncx";
    }

    String getNcxHref() {
        return "toc.ncx";
    }

    String getNcxMediaType() {
        return "application/x-dtbncx+xml";
    }

    public BookProcessor getBookProcessor() {
        return this.bookProcessor;
    }


    public void setBookProcessor(final BookProcessor bookProcessor) {
        this.bookProcessor = bookProcessor;
    }

}
