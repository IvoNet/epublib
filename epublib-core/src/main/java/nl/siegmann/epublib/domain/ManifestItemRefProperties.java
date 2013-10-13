package nl.siegmann.epublib.domain;

public enum ManifestItemRefProperties implements ManifestProperties {
    PAGE_SPREAD_LEFT("page-spread-left"),
    PAGE_SPREAD_RIGHT("page-spread-right");

    private final String name;

    ManifestItemRefProperties(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
