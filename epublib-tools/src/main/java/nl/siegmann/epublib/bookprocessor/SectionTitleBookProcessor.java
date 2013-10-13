package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.BookProcessor;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.List;

public class SectionTitleBookProcessor implements BookProcessor {

    @Override
    public Book processBook(final Book book) {
        final XPath xpath = createXPathExpression();
        processSections(book.getTableOfContents().getTocReferences(), book, xpath);
        return book;
    }

    private void processSections(final List<TOCReference> tocReferences, final Book book, final XPath xpath) {
        for (final TOCReference tocReference : tocReferences) {
            if (!StringUtils.isBlank(tocReference.getTitle())) {
                continue;
            }
            try {
                final String title = getTitle(tocReference, book, xpath);
                tocReference.setTitle(title);
            } catch (XPathExpressionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    private String getTitle(final TOCReference tocReference, final Book book, final XPath xpath)
            throws IOException, XPathExpressionException {
        final Resource resource = tocReference.getResource();
        if (resource == null) {
            return null;
        }
        final InputSource inputSource = new InputSource(resource.getInputStream());
        final String title = xpath.evaluate("/html/head/title", inputSource);
        return title;
    }


    private XPath createXPathExpression() {
        return XPathFactory.newInstance().newXPath();
    }
}
