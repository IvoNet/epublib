package nl.siegmann.epublib.search;

import nl.siegmann.epublib.domain.Resource;

public class SearchResult {
    private int pagePos = -1;
    private final String searchTerm;
    private final Resource resource;

    public SearchResult(final int pagePos, final String searchTerm, final Resource resource) {
        this.pagePos = pagePos;
        this.searchTerm = searchTerm;
        this.resource = resource;
    }

    public int getPagePos() {
        return this.pagePos;
    }

    public String getSearchTerm() {
        return this.searchTerm;
    }

    public Resource getResource() {
        return this.resource;
    }
}