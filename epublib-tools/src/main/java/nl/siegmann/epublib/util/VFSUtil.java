package nl.siegmann.epublib.util;

import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utitilies for making working with apache commons VFS easier.
 *
 * @author paul
 *
 */
public class VFSUtil {

    private static final Logger log = LoggerFactory.getLogger(VFSUtil.class);

    public static Resource createResource(final FileObject rootDir, final FileObject file, final String inputEncoding)
            throws IOException {
        final MediaType mediaType = MediatypeService.determineMediaType(file.getName().getBaseName());
        if (mediaType == null) {
            return null;
        }
        final String href = calculateHref(rootDir, file);
        final Resource result = new Resource(null, IOUtils
                .toByteArray(file.getContent().getInputStream()), href, mediaType);
        result.setInputEncoding(inputEncoding);
        return result;
    }

    public static String calculateHref(final FileObject rootDir, final FileObject currentFile) throws IOException {
        String result = currentFile.getName().toString().substring(rootDir.getName().toString().length() + 1);
        result += ".html";
        return result;
    }

    /**
     * First tries to load the inputLocation via VFS; if that doesn't work it tries to load it as a local File
     * @param inputLocation
     * @return the FileObject referred to by the inputLocation
     * @throws FileSystemException
     */
    public static FileObject resolveFileObject(final String inputLocation) throws FileSystemException {
        FileObject result = null;
        try {
            result = VFS.getManager().resolveFile(inputLocation);
        } catch (Exception e) {
            try {
                result = VFS.getManager().resolveFile(new File("."), inputLocation);
            } catch (Exception e1) {
                log.error(e.getMessage(), e);
                log.error(e1.getMessage(), e);
            }
        }
        return result;
    }


    /**
     * First tries to load the inputLocation via VFS; if that doesn't work it tries to load it as a local File
     *
     * @param inputLocation
     * @return the InputStream referred to by the inputLocation
     * @throws FileSystemException
     */
    public static InputStream resolveInputStream(final String inputLocation) throws FileSystemException {
        InputStream result = null;
        try {
            result = VFS.getManager().resolveFile(inputLocation).getContent().getInputStream();
        } catch (Exception e) {
            try {
                result = new FileInputStream(inputLocation);
            } catch (FileNotFoundException e1) {
                log.error(e.getMessage(), e);
                log.error(e1.getMessage(), e);
            }
        }
        return result;
    }
}
