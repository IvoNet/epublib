package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The spine sections are the sections of the book in the order in which the book should be read.
 *
 * This contrasts with the Table of Contents sections which is an index into the Book's sections.
 *
 * @see nl.siegmann.epublib.domain.TableOfContents
 *
 * @author paul
 *
 */
public class Spine implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3878483958947357246L;
    private Resource tocResource;
    private List<SpineReference> spineReferences;

    public Spine() {
        this(new ArrayList<SpineReference>());
    }

    /**
     * Creates a spine out of all the resources in the table of contents.
     *
     * @param tableOfContents
     */
    public Spine(final TableOfContents tableOfContents) {
        this.spineReferences = createSpineReferences(tableOfContents.getAllUniqueResources());
    }

    private Spine(final List<SpineReference> spineReferences) {
        this.spineReferences = spineReferences;
    }

    private static List<SpineReference> createSpineReferences(final Collection<Resource> resources) {
        final List<SpineReference> result = new ArrayList<SpineReference>(resources.size());
        for (final Resource resource : resources) {
            result.add(new SpineReference(resource));
        }
        return result;
    }

    public List<SpineReference> getSpineReferences() {
        return this.spineReferences;
    }

    public void setSpineReferences(final List<SpineReference> spineReferences) {
        this.spineReferences = spineReferences;
    }

    /**
     * Gets the resource at the given index.
     * Null if not found.
     *
     * @param index
     * @return the resource at the given index.
     */
    public Resource getResource(final int index) {
        if ((index < 0) || (index >= this.spineReferences.size())) {
            return null;
        }
        return this.spineReferences.get(index).getResource();
    }

    /**
     * Finds the first resource that has the given resourceId.
     *
     * Null if not found.
     *
     * @param resourceId
     * @return the first resource that has the given resourceId.
     */
    public int findFirstResourceById(final String resourceId) {
        if (StringUtil.isBlank(resourceId)) {
            return -1;
        }

        for (int i = 0; i < this.spineReferences.size(); i++) {
            final SpineReference spineReference = this.spineReferences.get(i);
            if (resourceId.equals(spineReference.getResourceId())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds the given spineReference to the spine references and returns it.
     *
     * @param spineReference
     * @return the given spineReference
     */
    public SpineReference addSpineReference(final SpineReference spineReference) {
        if (this.spineReferences == null) {
            this.spineReferences = new ArrayList<SpineReference>();
        }
        this.spineReferences.add(spineReference);
        return spineReference;
    }

    /**
     * Adds the given resource to the spine references and returns it.
     *
     * @return the given spineReference
     */
    public SpineReference addResource(final Resource resource) {
        return addSpineReference(new SpineReference(resource));
    }

    /**
     * The number of elements in the spine.
     *
     * @return The number of elements in the spine.
     */
    public int size() {
        return this.spineReferences.size();
    }

    /**
     * As per the epub file format the spine officially maintains a reference to the Table of Contents.
     * The epubwriter will look for it here first, followed by some clever tricks to find it elsewhere if not found.
     * Put it here to be sure of the expected behaviours.
     *
     * @param tocResource
     */
    public void setTocResource(final Resource tocResource) {
        this.tocResource = tocResource;
    }

    /**
     * The resource containing the XML for the tableOfContents.
     * When saving an epub file this resource needs to be in this place.
     *
     * @return The resource containing the XML for the tableOfContents.
     */
    public Resource getTocResource() {
        return this.tocResource;
    }

    /**
     * The position within the spine of the given resource.
     *
     * @param currentResource
     * @return something &lt; 0 if not found.
     *
     */
    public int getResourceIndex(final Resource currentResource) {
        if (currentResource == null) {
            return -1;
        }
        return getResourceIndex(currentResource.getHref());
    }

    /**
     * The first position within the spine of a resource with the given href.
     *
     * @return something &lt; 0 if not found.
     *
     */
    int getResourceIndex(final String resourceHref) {
        int result = -1;
        if (StringUtil.isBlank(resourceHref)) {
            return result;
        }
        for (int i = 0; i < this.spineReferences.size(); i++) {
            if (resourceHref.equals(this.spineReferences.get(i).getResource().getHref())) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * Whether the spine has any references
     * @return Whether the spine has any references
     */
    public boolean isEmpty() {
        return this.spineReferences.isEmpty();
    }
}
