package nl.siegmann.epublib.domain;

import junit.framework.TestCase;
import nl.siegmann.epublib.service.MediatypeService;

public class BookTest extends TestCase {

    public void testGetContents1() {
        final Book book = new Book();
        final Resource resource1 = new Resource("id1", "Hello, world !"
                .getBytes(), "chapter1.html", MediatypeService.XHTML);
        book.getSpine().addResource(resource1);
        book.getTableOfContents().addSection(resource1, "My first chapter");
        assertEquals(1, book.getContents().size());
    }

    public void testGetContents2() {
        final Book book = new Book();
        final Resource resource1 = new Resource("id1", "Hello, world !"
                .getBytes(), "chapter1.html", MediatypeService.XHTML);
        book.getSpine().addResource(resource1);
        final Resource resource2 = new Resource("id1", "Hello, world !"
                .getBytes(), "chapter2.html", MediatypeService.XHTML);
        book.getTableOfContents().addSection(resource2, "My first chapter");
        assertEquals(2, book.getContents().size());
    }

    public void testGetContents3() {
        final Book book = new Book();
        final Resource resource1 = new Resource("id1", "Hello, world !"
                .getBytes(), "chapter1.html", MediatypeService.XHTML);
        book.getSpine().addResource(resource1);
        final Resource resource2 = new Resource("id1", "Hello, world !"
                .getBytes(), "chapter2.html", MediatypeService.XHTML);
        book.getTableOfContents().addSection(resource2, "My first chapter");
        book.getGuide().addReference(new GuideReference(resource2, GuideReference.FOREWORD, "The Foreword"));
        assertEquals(2, book.getContents().size());
    }

    public void testGetContents4() {
        final Book book = new Book();

        final Resource resource1 = new Resource("id1", "Hello, world !"
                .getBytes(), "chapter1.html", MediatypeService.XHTML);
        book.getSpine().addResource(resource1);

        final Resource resource2 = new Resource("id1", "Hello, world !"
                .getBytes(), "chapter2.html", MediatypeService.XHTML);
        book.getTableOfContents().addSection(resource2, "My first chapter");

        final Resource resource3 = new Resource("id1", "Hello, world !"
                .getBytes(), "foreword.html", MediatypeService.XHTML);
        book.getGuide().addReference(new GuideReference(resource3, GuideReference.FOREWORD, "The Foreword"));

        assertEquals(3, book.getContents().size());
    }
}
