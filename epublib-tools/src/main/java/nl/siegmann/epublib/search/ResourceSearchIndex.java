package nl.siegmann.epublib.search;

import nl.siegmann.epublib.domain.Resource;

/**
 * The search index for a single resource.
 *
 * @author paul.siegmann
 *
 */
// package
class ResourceSearchIndex {
    private final String content;
    private final Resource resource;

    public ResourceSearchIndex(final Resource resource, final String searchContent) {
        this.resource = resource;
        this.content = searchContent;
    }

    public String getContent() {
        return this.content;
    }

    public Resource getResource() {
        return this.resource;
    }

}