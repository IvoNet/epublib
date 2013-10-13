package org.htmlcleaner;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class EpublibXmlSerializer extends SimpleXmlSerializer {
    private final String outputEncoding;

    public EpublibXmlSerializer(final CleanerProperties paramCleanerProperties, final String outputEncoding) {
        super(paramCleanerProperties);
        this.outputEncoding = outputEncoding;
    }

    @Override
    protected String escapeXml(final String xmlContent) {
        return xmlContent;
    }

    /**
     * Differs from the super.serializeOpenTag in that it:
     * <ul>
     * <li>skips the xmlns:xml="xml" attribute</li>
     * <li>if the tagNode is a meta tag setting the contentType then it sets the encoding to the actual encoding</li>
     * </ul>
     */
    @Override
    protected void serializeOpenTag(final TagNode tagNode, final Writer writer, final boolean newLine)
            throws IOException {
        String tagName = tagNode.getName();

        if (Utils.isEmptyString(tagName)) {
            return;
        }

        final boolean nsAware = this.props.isNamespacesAware();

        Set<String> definedNSPrefixes = null;
        Set<String> additionalNSDeclNeeded = null;

        final String tagPrefix = Utils.getXmlNSPrefix(tagName);
        if (tagPrefix != null) {
            if (nsAware) {
                definedNSPrefixes = new HashSet<String>();
                tagNode.collectNamespacePrefixesOnPath(definedNSPrefixes);
                if (!definedNSPrefixes.contains(tagPrefix)) {
                    additionalNSDeclNeeded = new TreeSet<String>();
                    additionalNSDeclNeeded.add(tagPrefix);
                }
            } else {
                tagName = Utils.getXmlName(tagName);
            }
        }

        writer.write("<" + tagName);

        if (isMetaContentTypeTag(tagNode)) {
            tagNode.setAttribute("content", "text/html; charset=" + this.outputEncoding);
        }

        // write attributes
        for (final Map.Entry<String, String> entry : tagNode.getAttributes().entrySet()) {
            String attName = entry.getKey();
            final String attPrefix = Utils.getXmlNSPrefix(attName);
            if (attPrefix != null) {
                if (nsAware) {
                    // collect used namespace prefixes in attributes in order to explicitly define
                    // ns declaration if needed; otherwise it would be ill-formed xml
                    if (definedNSPrefixes == null) {
                        definedNSPrefixes = new HashSet<String>();
                        tagNode.collectNamespacePrefixesOnPath(definedNSPrefixes);
                    }
                    if (!definedNSPrefixes.contains(attPrefix)) {
                        if (additionalNSDeclNeeded == null) {
                            additionalNSDeclNeeded = new TreeSet<String>();
                        }
                        additionalNSDeclNeeded.add(attPrefix);
                    }
                } else {
                    attName = Utils.getXmlName(attName);
                }
            }
            writer.write(" " + attName + "=\"" + escapeXml(entry.getValue()) + "\"");
        }

        // write namespace declarations 
        if (nsAware) {
            final Map<String, String> nsDeclarations = tagNode.getNamespaceDeclarations();
            if (nsDeclarations != null) {
                for (final Map.Entry<String, String> entry : nsDeclarations.entrySet()) {
                    final String prefix = entry.getKey();
                    String att = "xmlns";
                    if (!prefix.isEmpty()) {
                        att += ":" + prefix;
                    }
                    writer.write(" " + att + "=\"" + escapeXml(entry.getValue()) + "\"");
                }
            }
        }

        // write additional namespace declarations needed for this tag in order xml to be well-formed
        if (additionalNSDeclNeeded != null) {
            for (final String prefix : additionalNSDeclNeeded) {
                // skip the xmlns:xml="xml" attribute
                if ("xml".equalsIgnoreCase(prefix)) {
                    continue;
                }
                writer.write(" xmlns:" + prefix + "=\"" + prefix + "\"");
            }
        }

        if (isMinimizedTagSyntax(tagNode)) {
            writer.write(" />");
            if (newLine) {
                writer.write("\n");
            }
        } else if (dontEscape(tagNode)) {
            writer.write("><![CDATA[");
        } else {
            writer.write(">");
        }
    }

    private boolean isMetaContentTypeTag(final TagNode tagNode) {
        return "meta".equalsIgnoreCase(tagNode.getName())
               && "Content-Type".equalsIgnoreCase(tagNode.getAttributeByName("http-equiv"));
    }
}