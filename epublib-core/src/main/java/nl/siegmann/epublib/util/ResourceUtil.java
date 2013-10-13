package nl.siegmann.epublib.util;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubProcessorSupport;
import nl.siegmann.epublib.service.MediatypeService;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Various resource utility methods
 *
 * @author paul
 *
 */
public class ResourceUtil {

    public static Resource createResource(final File file) throws IOException {
        if (file == null) {
            return null;
        }
        final MediaType mediaType = MediatypeService.determineMediaType(file.getName());
        final byte[] data = IOUtil.toByteArray(new FileInputStream(file));
        final Resource result = new Resource(data, mediaType);
        return result;
    }


    /**
     * Creates a resource with as contents a html page with the given title.
     *
     * @param title
     * @param href
     * @return a resource with as contents a html page with the given title.
     */
    public static Resource createResource(final String title, final String href) {
        final String content =
                "<html><head><title>" + title + "</title></head><body><h1>" + title + "</h1></body></html>";
        return new Resource(null, content.getBytes(), href, MediatypeService.XHTML, Constants.CHARACTER_ENCODING);
    }

    /**
     * Creates a resource out of the given zipEntry and zipInputStream.
     *
     * @param zipEntry
     * @param zipInputStream
     * @return a resource created out of the given zipEntry and zipInputStream.
     * @throws IOException
     */
    public static Resource createResource(final ZipEntry zipEntry, final ZipInputStream zipInputStream)
            throws IOException {
        return new Resource(zipInputStream, zipEntry.getName());

    }

    public static Resource createResource(final ZipEntry zipEntry, final InputStream zipInputStream)
            throws IOException {
        return new Resource(zipInputStream, zipEntry.getName());

    }

    /**
     * Converts a given string from given input character encoding to the requested output character encoding.
     *
     * @param inputEncoding
     * @param outputEncoding
     * @param input
     * @return the string from given input character encoding converted to the requested output character encoding.
     * @throws UnsupportedEncodingException
     */
    public static byte[] recode(final String inputEncoding, final String outputEncoding, final byte[] input)
            throws UnsupportedEncodingException {
        return new String(input, inputEncoding).getBytes(outputEncoding);
    }

    /**
     * Gets the contents of the Resource as an InputSource in a null-safe manner.
     *
     */
    private static InputSource getInputSource(final Resource resource) throws IOException {
        if (resource == null) {
            return null;
        }
        final Reader reader = resource.getReader();
        if (reader == null) {
            return null;
        }
        final InputSource inputSource = new InputSource(reader);
        return inputSource;
    }


    /**
     * Reads parses the xml therein and returns the result as a Document
     */
    public static Document getAsDocument(final Resource resource)
            throws SAXException, IOException, ParserConfigurationException {
        return getAsDocument(resource, EpubProcessorSupport.createDocumentBuilder());
    }


    /**
     * Reads the given resources inputstream, parses the xml therein and returns the result as a Document
     *
     * @param resource
     * @param documentBuilder
     * @return the document created from the given resource
     * @throws UnsupportedEncodingException
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private static Document getAsDocument(final Resource resource, final DocumentBuilder documentBuilder) throws
                                                                                                          SAXException,
                                                                                                          IOException,
                                                                                                          ParserConfigurationException {
        final InputSource inputSource = getInputSource(resource);
        if (inputSource == null) {
            return null;
        }
        final Document result = documentBuilder.parse(inputSource);
        return result;
    }
}
