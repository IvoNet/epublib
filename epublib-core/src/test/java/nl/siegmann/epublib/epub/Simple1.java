package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;

import java.io.FileOutputStream;

public class Simple1 {
    public static void main(final String[] args) {
        try {
            // Create new Book
            final Book book = new Book();

            // Set the title
            book.getMetadata().addTitle("Epublib test book 1");

            // Add an Author
            book.getMetadata().addAuthor(new Author("Joe", "Tester"));

            // Set cover image
            book.setCoverImage(new Resource(Simple1.class.getResourceAsStream("/book1/test_cover.png"), "cover.png"));

            // Add Chapter 1
            book.addSection("Introduction", new Resource(Simple1.class
                                                                 .getResourceAsStream("/book1/chapter1.html"),
                                                         "chapter1.html"));

            // Add css file
            book.getResources().add(new Resource(Simple1.class.getResourceAsStream("/book1/book1.css"), "book1.css"));

            // Add Chapter 2
            final TOCReference chapter2 = book.addSection("Second Chapter", new Resource(Simple1.class
                                                                                                 .getResourceAsStream
                                                                                                         ("/book1/chapter2.html"), "chapter2.html"));

            // Add image used by Chapter 2
            book.getResources()
                    .add(new Resource(Simple1.class.getResourceAsStream("/book1/flowers_320x240.jpg"), "flowers.jpg"));

            // Add Chapter2, Section 1
            book.addSection(chapter2, "Chapter 2, section 1", new Resource(Simple1.class
                                                                                   .getResourceAsStream
                                                                                           ("/book1/chapter2_1.html")
                    , "chapter2_1.html"));

            // Add Chapter 3
            book.addSection("Conclusion", new Resource(Simple1.class
                                                               .getResourceAsStream("/book1/chapter3.html"),
                                                       "chapter3.html"));

            // Create EpubWriter
            final EpubWriter epubWriter = new EpubWriter();

            // Write the Book as Epub
            epubWriter.write(book, new FileOutputStream("test1_book1.epub"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}