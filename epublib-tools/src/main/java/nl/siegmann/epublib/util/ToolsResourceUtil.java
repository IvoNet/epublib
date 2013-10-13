package nl.siegmann.epublib.util;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Various resource utility methods
 *
 * @author paul
 *
 */
public class ToolsResourceUtil {

    private static final Logger log = LoggerFactory.getLogger(ToolsResourceUtil.class);


    public static String getTitle(final Resource resource) {
        if (resource == null) {
            return "";
        }
        if (resource.getMediaType() != MediatypeService.XHTML) {
            return resource.getHref();
        }
        String title = findTitleFromXhtml(resource);
        if (title == null) {
            title = "";
        }
        return title;
    }


    /**
     * Retrieves whatever it finds between &lt;title&gt;...&lt;/title&gt; or &lt;h1-7&gt;...&lt;/h1-7&gt;.
     * The first match is returned, even if it is a blank string.
     * If it finds nothing null is returned.
     * @param resource
     * @return whatever it finds in the resource between &lt;title&gt;...&lt;/title&gt; or &lt;h1-7&gt;...&lt;/h1-7&gt;.
     */
    public static String findTitleFromXhtml(final Resource resource) {
        if (resource == null) {
            return "";
        }
        if (resource.getTitle() != null) {
            return resource.getTitle();
        }
        final Pattern h_tag = Pattern.compile("^h\\d\\s*", Pattern.CASE_INSENSITIVE);
        String title = null;
        try {
            final Reader content = resource.getReader();
            final Scanner scanner = new Scanner(content);
            scanner.useDelimiter("<");
            while (scanner.hasNext()) {
                final String text = scanner.next();
                final int closePos = text.indexOf('>');
                final String tag = text.substring(0, closePos);
                if ("title".equalsIgnoreCase(tag)
                    || h_tag.matcher(tag).find()) {

                    title = text.substring(closePos + 1).trim();
                    title = StringEscapeUtils.unescapeHtml(title);
                    break;
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        resource.setTitle(title);
        return title;
    }
}
