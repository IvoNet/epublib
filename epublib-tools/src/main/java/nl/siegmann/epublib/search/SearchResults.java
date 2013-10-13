package nl.siegmann.epublib.search;

import nl.siegmann.epublib.domain.Book;

import java.util.ArrayList;
import java.util.List;

public class SearchResults {
    private String searchTerm;

    public String getSearchTerm() {
        return this.searchTerm;
    }

    public void setSearchTerm(final String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(final Book book) {
        this.book = book;
    }

    public List<SearchResult> getHits() {
        return this.hits;
    }

    public void setHits(final List<SearchResult> hits) {
        this.hits = hits;
    }

    private Book book;
    private List<SearchResult> hits = new ArrayList<SearchResult>();

    public boolean isEmpty() {
        return this.hits.isEmpty();
    }

    public int size() {
        return this.hits.size();
    }

    public void addAll(final List<SearchResult> searchResults) {
        this.hits.addAll(searchResults);
    }
}