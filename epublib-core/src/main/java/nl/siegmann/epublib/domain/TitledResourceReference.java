package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.util.StringUtil;

public class TitledResourceReference extends ResourceReference {

    /**
     *
     */
    private static final long serialVersionUID = 3918155020095190080L;
    private String fragmentId;
    private String title;

    public TitledResourceReference(final Resource resource) {
        this(resource, null);
    }

    TitledResourceReference(final Resource resource, final String title) {
        this(resource, title, null);
    }

    TitledResourceReference(final Resource resource, final String title, final String fragmentId) {
        super(resource);
        this.title = title;
        this.fragmentId = fragmentId;
    }

    public String getFragmentId() {
        return this.fragmentId;
    }

    public void setFragmentId(final String fragmentId) {
        this.fragmentId = fragmentId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }


    /**
     * If the fragmentId is blank it returns the resource href, otherwise it returns the resource href + '#' + the
     * fragmentId.
     *
     * @return If the fragmentId is blank it returns the resource href, otherwise it returns the resource href + '#'
     * + the fragmentId.
     */
    public String getCompleteHref() {
        return StringUtil.isBlank(this.fragmentId) ? this.resource.getHref() :
               (this.resource.getHref() + Constants.FRAGMENT_SEPARATOR_CHAR + this.fragmentId);
    }

    void setResource(final Resource resource, final String fragmentId) {
        super.setResource(resource);
        this.fragmentId = fragmentId;
    }

    /**
     * Sets the resource to the given resource and sets the fragmentId to null.
     *
     */
    @Override
    public void setResource(final Resource resource) {
        setResource(resource, null);
    }
}
