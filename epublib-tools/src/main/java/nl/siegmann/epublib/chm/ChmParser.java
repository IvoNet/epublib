package nl.siegmann.epublib.chm;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Reads the files that are extracted from a windows help ('.chm') file and creates a epublib Book out of it.
 *
 * @author paul
 *
 */
public class ChmParser {

    private static final String DEFAULT_CHM_HTML_INPUT_ENCODING = "windows-1252";
    private static final int MINIMAL_SYSTEM_TITLE_LENGTH = 4;

    public static Book parseChm(final FileObject chmRootDir)
            throws XPathExpressionException, IOException, ParserConfigurationException {
        return parseChm(chmRootDir, DEFAULT_CHM_HTML_INPUT_ENCODING);
    }

    public static Book parseChm(final FileObject chmRootDir, String inputHtmlEncoding)
            throws IOException, ParserConfigurationException,
                   XPathExpressionException {
        final Book result = new Book();
        result.getMetadata().addTitle(findTitle(chmRootDir));
        final FileObject hhcFileObject = findHhcFileObject(chmRootDir);
        if (hhcFileObject == null) {
            throw new IllegalArgumentException("No index file found in directory " + chmRootDir
                                               + ". (Looked for file ending with extension '.hhc'");
        }
        if (inputHtmlEncoding == null) {
            inputHtmlEncoding = DEFAULT_CHM_HTML_INPUT_ENCODING;
        }
        final Resources resources = findResources(chmRootDir, inputHtmlEncoding);
        final List<TOCReference> tocReferences = HHCParser
                .parseHhc(hhcFileObject.getContent().getInputStream(), resources);
        result.setTableOfContents(new TableOfContents(tocReferences));
        result.setResources(resources);
        result.generateSpineFromTableOfContents();
        return result;
    }


    /**
     * Finds in the '#SYSTEM' file the 3rd set of characters that have ascii value &gt;= 32 and &gt;= 126 and is more
     * than 3 characters long.
     * Assumes that that is then the title of the book.
     *
     * @param chmRootDir
     * @return Finds in the '#SYSTEM' file the 3rd set of characters that have ascii value &gt;= 32 and &gt;= 126 and
     * is more than 3 characters long.
     * @throws IOException
     */
    private static String findTitle(final FileObject chmRootDir) throws IOException {
        final FileObject systemFileObject = chmRootDir.resolveFile("#SYSTEM");
        final InputStream in = systemFileObject.getContent().getInputStream();
        boolean inText = false;
        int lineCounter = 0;
        StringBuilder line = new StringBuilder();
        for (int c = in.read(); c >= 0; c = in.read()) {
            if ((c >= 32) && (c <= 126)) {
                line.append((char) c);
                inText = true;
            } else {
                if (inText) {
                    if (line.length() >= 3) {
                        lineCounter++;
                        if (lineCounter >= MINIMAL_SYSTEM_TITLE_LENGTH) {
                            return line.toString();
                        }
                    }
                    line = new StringBuilder();
                }
                inText = false;
            }
        }
        return "<unknown title>";
    }

    private static FileObject findHhcFileObject(final FileObject chmRootDir) throws FileSystemException {
        final FileObject[] files = chmRootDir.getChildren();
        for (final FileObject file : files) {
            if ("hhc".equalsIgnoreCase(file.getName().getExtension())) {
                return file;
            }
        }
        return null;
    }


    private static Resources findResources(final FileObject rootDir, final String inputEncoding) throws IOException {
        final Resources result = new Resources();
        final FileObject[] allFiles = rootDir.findFiles(new AllFileSelector());
        for (final FileObject file : allFiles) {
            if (file.getType() == FileType.FOLDER) {
                continue;
            }
            final MediaType mediaType = MediatypeService.determineMediaType(file.getName().getBaseName());
            if (mediaType == null) {
                continue;
            }
            final String href = file.getName().toString().substring(rootDir.getName().toString().length() + 1);
            byte[] resourceData = IOUtils.toByteArray(file.getContent().getInputStream());
            if ((mediaType == MediatypeService.XHTML) && !Constants.CHARACTER_ENCODING
                    .equalsIgnoreCase(inputEncoding)) {
                resourceData = ResourceUtil.recode(inputEncoding, Constants.CHARACTER_ENCODING, resourceData);
            }
            final Resource fileResource = new Resource(null, resourceData, href, mediaType);
            result.add(fileResource);
        }
        return result;
    }
}
