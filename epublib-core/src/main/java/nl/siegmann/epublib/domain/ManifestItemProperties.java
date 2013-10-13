package nl.siegmann.epublib.domain;

public enum ManifestItemProperties implements ManifestProperties {
    COVER_IMAGE("cover-image"),
    MATHML("mathml"),
    NAV("nav"),
    REMOTE_RESOURCES("remote-resources"),
    SCRIPTED("scripted"),
    SVG("svg"),
    SWITCH("switch");

    private final String name;

    ManifestItemProperties(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
