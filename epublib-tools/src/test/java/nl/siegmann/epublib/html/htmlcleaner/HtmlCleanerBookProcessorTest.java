package nl.siegmann.epublib.html.htmlcleaner;

import junit.framework.TestCase;
import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.bookprocessor.HtmlCleanerBookProcessor;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;

import java.io.IOException;

public class HtmlCleanerBookProcessorTest extends TestCase {

    public void testSimpleDocument1() {
        final Book book = new Book();
        final String testInput = "<html><head><title>title</title></head><body>Hello, world!</html>";
        final String expectedResult = Constants.DOCTYPE_XHTML
                                      + "\n<html xmlns=\"http://www.w3"
                                      + ".org/1999/xhtml\"><head><title>title</title></head><body>Hello, "
                                      + "world!</body></html>";
        try {
            final Resource resource = new Resource(testInput.getBytes(Constants.CHARACTER_ENCODING), "test.html");
            book.getResources().add(resource);
            final HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
            final byte[] processedHtml = htmlCleanerBookProcessor
                    .processHtml(resource, book, Constants.CHARACTER_ENCODING);
            final String actualResult = new String(processedHtml, Constants.CHARACTER_ENCODING);
            assertEquals(expectedResult, actualResult);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    public void testSimpleDocument2() {
        final Book book = new Book();
        final String testInput = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>test page</title><link "
                                 + "foo=\"bar\" /></head><body background=\"red\">Hello, world!</body></html>";
        try {
            final Resource resource = new Resource(testInput.getBytes(Constants.CHARACTER_ENCODING), "test.html");
            book.getResources().add(resource);
            final HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
            final byte[] processedHtml = htmlCleanerBookProcessor
                    .processHtml(resource, book, Constants.CHARACTER_ENCODING);
            final String result = new String(processedHtml, Constants.CHARACTER_ENCODING);
            assertEquals(Constants.DOCTYPE_XHTML + "\n" + testInput, result);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    public void testSimpleDocument3() {
        final Book book = new Book();
        final String testInput = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>test "
                                 + "page</title></head><body>Hello, world! ß</body></html>";
        try {
            final Resource resource = new Resource(null, testInput
                    .getBytes(Constants.CHARACTER_ENCODING), "test.html", MediatypeService.XHTML,
                                                   Constants.CHARACTER_ENCODING);
            book.getResources().add(resource);
            final HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
            final byte[] processedHtml = htmlCleanerBookProcessor
                    .processHtml(resource, book, Constants.CHARACTER_ENCODING);
            final String result = new String(processedHtml, Constants.CHARACTER_ENCODING);
            assertEquals(Constants.DOCTYPE_XHTML + "\n" + testInput, result);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    public void testSimpleDocument4() {
        final Book book = new Book();
        final String testInput = "<html><head><title>title</title></head><body>Hello, world!\nHow are you ?</html>";
        final String expectedResult = Constants.DOCTYPE_XHTML
                                      + "\n<html xmlns=\"http://www.w3"
                                      + ".org/1999/xhtml\"><head><title>title</title></head><body>Hello, "
                                      + "world!\nHow are you ?</body></html>";
        try {
            final Resource resource = new Resource(testInput.getBytes(Constants.CHARACTER_ENCODING), "test.html");
            book.getResources().add(resource);
            final HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
            final byte[] processedHtml = htmlCleanerBookProcessor
                    .processHtml(resource, book, Constants.CHARACTER_ENCODING);
            final String actualResult = new String(processedHtml, Constants.CHARACTER_ENCODING);
            assertEquals(expectedResult, actualResult);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }


    public void testMetaContentType() {
        final Book book = new Book();
        final String testInput = "<html><head><title>title</title><meta http-equiv=\"Content-Type\" "
                                 + "content=\"text/html; charset=iso-8859-1\"/></head><body>Hello, world!</html>";
        final String expectedResult = Constants.DOCTYPE_XHTML
                                      + "\n<html xmlns=\"http://www.w3"
                                      + ".org/1999/xhtml\"><head><title>title</title><meta "
                                      + "http-equiv=\"Content-Type\" content=\"text/html; charset="
                                      + Constants.CHARACTER_ENCODING + "\" /></head><body>Hello, world!</body></html>";
        try {
            final Resource resource = new Resource(testInput.getBytes(Constants.CHARACTER_ENCODING), "test.html");
            book.getResources().add(resource);
            final HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
            final byte[] processedHtml = htmlCleanerBookProcessor
                    .processHtml(resource, book, Constants.CHARACTER_ENCODING);
            final String actualResult = new String(processedHtml, Constants.CHARACTER_ENCODING);
            assertEquals(expectedResult, actualResult);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    public void testDocType1() {
        final Book book = new Book();
        final String testInput = "<html><head><title>title</title><meta http-equiv=\"Content-Type\" "
                                 + "content=\"text/html; charset=iso-8859-1\"/></head><body>Hello, world!</html>";
        final String expectedResult = Constants.DOCTYPE_XHTML
                                      + "\n<html xmlns=\"http://www.w3"
                                      + ".org/1999/xhtml\"><head><title>title</title><meta "
                                      + "http-equiv=\"Content-Type\" content=\"text/html; charset="
                                      + Constants.CHARACTER_ENCODING + "\" /></head><body>Hello, world!</body></html>";
        try {
            final Resource resource = new Resource(testInput.getBytes(Constants.CHARACTER_ENCODING), "test.html");
            book.getResources().add(resource);
            final HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
            final byte[] processedHtml = htmlCleanerBookProcessor
                    .processHtml(resource, book, Constants.CHARACTER_ENCODING);
            final String actualResult = new String(processedHtml, Constants.CHARACTER_ENCODING);
            assertEquals(expectedResult, actualResult);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    public void testDocType2() {
        final Book book = new Book();
        final String testInput = Constants.DOCTYPE_XHTML
                                 + "\n<html><head><title>title</title><meta http-equiv=\"Content-Type\" "
                                 + "content=\"text/html; charset=iso-8859-1\"/></head><body>Hello, world!</html>";
        final String expectedResult = Constants.DOCTYPE_XHTML
                                      + "\n<html xmlns=\"http://www.w3"
                                      + ".org/1999/xhtml\"><head><title>title</title><meta "
                                      + "http-equiv=\"Content-Type\" content=\"text/html; charset="
                                      + Constants.CHARACTER_ENCODING + "\" /></head><body>Hello, world!</body></html>";
        try {
            final Resource resource = new Resource(testInput.getBytes(Constants.CHARACTER_ENCODING), "test.html");
            book.getResources().add(resource);
            final HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
            final byte[] processedHtml = htmlCleanerBookProcessor
                    .processHtml(resource, book, Constants.CHARACTER_ENCODING);
            final String actualResult = new String(processedHtml, Constants.CHARACTER_ENCODING);
            assertEquals(expectedResult, actualResult);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    public void testXmlNS() {
        final Book book = new Book();
        final String testInput = "<html><head><title>title</title></head><body xmlns:xml=\"xml\">Hello, world!</html>";
        final String expectedResult = Constants.DOCTYPE_XHTML
                                      + "\n<html xmlns=\"http://www.w3"
                                      + ".org/1999/xhtml\"><head><title>title</title></head><body>Hello, "
                                      + "world!</body></html>";
        try {
            final Resource resource = new Resource(testInput.getBytes(Constants.CHARACTER_ENCODING), "test.html");
            book.getResources().add(resource);
            final HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
            final byte[] processedHtml = htmlCleanerBookProcessor
                    .processHtml(resource, book, Constants.CHARACTER_ENCODING);
            final String actualResult = new String(processedHtml, Constants.CHARACTER_ENCODING);
            assertEquals(expectedResult, actualResult);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    public void testApos() {
        final Book book = new Book();
        final String testInput = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>test "
                                 + "page</title></head><body>'hi'</body></html>";
        try {
            final Resource resource = new Resource(null, testInput
                    .getBytes(Constants.CHARACTER_ENCODING), "test.html", MediatypeService.XHTML,
                                                   Constants.CHARACTER_ENCODING);
            book.getResources().add(resource);
            final HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
            final byte[] processedHtml = htmlCleanerBookProcessor
                    .processHtml(resource, book, Constants.CHARACTER_ENCODING);
            final String result = new String(processedHtml, Constants.CHARACTER_ENCODING);
            assertEquals(Constants.DOCTYPE_XHTML + "\n" + testInput, result);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }
}
