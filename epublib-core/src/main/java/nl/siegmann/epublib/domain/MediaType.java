package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

/**
 * MediaType is used to tell the type of content a resource is.
 *
 * Examples of mediatypes are image/gif, text/css and application/xhtml+xml
 *
 * All allowed mediaTypes are maintained bye the MediaTypeService.
 *
 * @see nl.siegmann.epublib.service.MediatypeService
 *
 * @author paul
 *
 */
public class MediaType implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7256091153727506788L;
    private final String name;
    private final String defaultExtension;
    private final Collection<String> extensions;

    public MediaType(final String name, final String defaultExtension) {
        this(name, defaultExtension, new String[]{defaultExtension});
    }

    public MediaType(final String name, final String defaultExtension,
                     final String[] extensions) {
        this(name, defaultExtension, Arrays.asList(extensions));
    }

    public int hashCode() {
        if (this.name == null) {
            return 0;
        }
        return this.name.hashCode();
    }

    private MediaType(final String name, final String defaultExtension,
                      final Collection<String> extensions) {
        this.name = name;
        this.defaultExtension = defaultExtension;
        this.extensions = extensions;
    }


    public String getName() {
        return this.name;
    }


    public String getDefaultExtension() {
        return this.defaultExtension;
    }


    public Collection<String> getExtensions() {
        return this.extensions;
    }

    public boolean equals(final Object otherMediaType) {
        if (!(otherMediaType instanceof MediaType)) {
            return false;
        }
        return this.name.equals(((MediaType) otherMediaType).getName());
    }

    public String toString() {
        return this.name;
    }
}
