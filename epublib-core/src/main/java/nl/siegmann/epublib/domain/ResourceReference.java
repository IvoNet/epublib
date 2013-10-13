package nl.siegmann.epublib.domain;

import java.io.Serializable;

public class ResourceReference implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2596967243557743048L;
    Resource resource;

    ResourceReference(final Resource resource) {
        this.resource = resource;
    }


    public Resource getResource() {
        return this.resource;
    }

    /**
     * Besides setting the resource it also sets the fragmentId to null.
     *
     * @param resource
     */
    void setResource(final Resource resource) {
        this.resource = resource;
    }


    /**
     * The id of the reference referred to.
     *
     * null of the reference is null or has a null id itself.
     *
     * @return The id of the reference referred to.
     */
    public String getResourceId() {
        if (this.resource != null) {
            return this.resource.getId();
        }
        return null;
    }
}
