package nl.siegmann.epublib.domain;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;


/**
 * A wrapper class for closing a ZipFile object when the InputStream derived
 * from it is closed.
 *
 * @author ttopalov
 *
 */
class ResourceInputStream extends FilterInputStream {

    private final ZipFile zipResource;

    /**
     * Constructor.
     *
     * @param in
     *            The InputStream object.
     * @param f
     *            The ZipFile object.
     */
    public ResourceInputStream(final InputStream in, final ZipFile f) {
        super(in);
        this.zipResource = f;
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.zipResource.close();
    }
}
