package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.epub.BookProcessor;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.CollectionUtil;
import nl.siegmann.epublib.util.ResourceUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * If the book contains a cover image then this will add a cover page to the book.
 * If the book contains a cover html page it will set that page's first image as the book's cover image.
 *
 * FIXME:
 *  will overwrite any "cover.jpg" or "cover.html" that are already there.
 *
 * @author paul
 *
 */
public class CoverpageBookProcessor implements BookProcessor {

    private static final String DEFAULT_COVER_PAGE_ID = "cover";
    private static final String DEFAULT_COVER_PAGE_HREF = "cover.html";
    private static final String DEFAULT_COVER_IMAGE_ID = "cover-image";
    private static final String DEFAULT_COVER_IMAGE_HREF = "images/cover.png";
    private static final Logger log = LoggerFactory.getLogger(CoverpageBookProcessor.class);
    private static final int MAX_COVER_IMAGE_SIZE = 999;

    // package
    static String calculateAbsoluteImageHref(final String relativeImageHref,
                                             final String baseHref) {
        if (relativeImageHref.startsWith("/")) {
            return relativeImageHref;
        }
        final String result = FilenameUtils
                .normalize(baseHref.substring(0, baseHref.lastIndexOf('/') + 1) + relativeImageHref, true);
        return result;
    }

//	private String getCoverImageHref(Resource coverImageResource) {
//		return "cover" + coverImageResource.getMediaType().getDefaultExtension();
//	}

    @Override
    public Book processBook(final Book book) {
        final Metadata metadata = book.getMetadata();
        if ((book.getCoverPage() == null) && (book.getCoverImage() == null)) {
            return book;
        }
        Resource coverPage = book.getCoverPage();
        if (coverPage == null) {
            coverPage = findCoverPage(book);
            book.setCoverPage(coverPage);
        }
        Resource coverImage = book.getCoverImage();
        if (coverPage == null) {
            if (coverImage != null) {
                if (StringUtils.isBlank(coverImage.getHref())) {
                    coverImage.setHref(getCoverImageHref(coverImage, book));
                }
                final String coverPageHtml = createCoverpageHtml(CollectionUtil.first(metadata.getTitles()), coverImage
                        .getHref());
                coverPage = new Resource(null, coverPageHtml
                        .getBytes(), getCoverPageHref(book), MediatypeService.XHTML);
                fixCoverResourceId(book, coverPage, DEFAULT_COVER_PAGE_ID);
            }
        } else { // coverPage != null
            if (book.getCoverImage() == null) {
                coverImage = getFirstImageSource(coverPage, book.getResources());
                book.setCoverImage(coverImage);
                if (coverImage != null) {
                    book.getResources().remove(coverImage.getHref());
                }
            }
        }

        book.setCoverImage(coverImage);
        book.setCoverPage(coverPage);
        setCoverResourceIds(book);
        return book;
    }

    private Resource findCoverPage(final Book book) {
        if (book.getCoverPage() != null) {
            return book.getCoverPage();
        }
        if (!(book.getSpine().isEmpty())) {
            return book.getSpine().getResource(0);
        }
        return null;
    }

    private void setCoverResourceIds(final Book book) {
        if (book.getCoverImage() != null) {
            fixCoverResourceId(book, book.getCoverImage(), DEFAULT_COVER_IMAGE_ID);
        }
        if (book.getCoverPage() != null) {
            fixCoverResourceId(book, book.getCoverPage(), DEFAULT_COVER_PAGE_ID);
        }
    }

    private void fixCoverResourceId(final Book book, final Resource resource, final String defaultId) {
        if (StringUtils.isBlank(resource.getId())) {
            resource.setId(defaultId);
        }
        book.getResources().fixResourceId(resource);
    }

    private String getCoverPageHref(final Book book) {
        return DEFAULT_COVER_PAGE_HREF;
    }

    private String getCoverImageHref(final Resource imageResource, final Book book) {
        return DEFAULT_COVER_IMAGE_HREF;
    }

    private Resource getFirstImageSource(final Resource titlePageResource, final Resources resources) {
        try {
            final Document titlePageDocument = ResourceUtil.getAsDocument(titlePageResource);
            final NodeList imageElements = titlePageDocument.getElementsByTagName("img");
            for (int i = 0; i < imageElements.getLength(); i++) {
                final String relativeImageHref = ((Element) imageElements.item(i)).getAttribute("src");
                final String absoluteImageHref = calculateAbsoluteImageHref(relativeImageHref, titlePageResource
                        .getHref());
                final Resource imageResource = resources.getByHref(absoluteImageHref);
                if (imageResource != null) {
                    return imageResource;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private String createCoverpageHtml(final String title, final String imageHref) {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3"
               + ".org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
               + "\t<head>\n" + "\t\t<title>Cover</title>\n"
               + "\t\t<style type=\"text/css\"> img { max-width: 100%; } </style>\n" + "\t</head>\n" + "\t<body>\n"
               + "\t\t<div id=\"cover-image\">\n" + "\t\t\t<img src=\"" + StringEscapeUtils.escapeHtml(imageHref)
               + "\" alt=\"" + StringEscapeUtils
                .escapeHtml(title) + "\"/>\n" + "\t\t</div>\n" + "\t</body>\n" + "</html>\n";
    }

    private Dimension calculateResizeSize(final BufferedImage image) {
        final Dimension result;
        result = image.getWidth() > image.getHeight() ?
                 new Dimension(MAX_COVER_IMAGE_SIZE, (int) (((double) MAX_COVER_IMAGE_SIZE / (double) image.getWidth())
                                                            * (double) image.getHeight())) :
                 new Dimension((int) (((double) MAX_COVER_IMAGE_SIZE / (double) image.getHeight()) * (double) image
                         .getWidth()), MAX_COVER_IMAGE_SIZE);
        return result;
    }

    @SuppressWarnings("unused")
    private byte[] createThumbnail(final byte[] imageData) throws IOException {
        final BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
        final Dimension thumbDimension = calculateResizeSize(originalImage);
        final BufferedImage thumbnailImage = createResizedCopy(originalImage, (int) thumbDimension
                .getWidth(), (int) thumbDimension.getHeight(), false);
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        ImageIO.write(thumbnailImage, "png", result);
        return result.toByteArray();

    }

    private BufferedImage createResizedCopy(final java.awt.Image originalImage, final int scaledWidth,
                                            final int scaledHeight, final boolean preserveAlpha) {
        final int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        final BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        final Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }
}
