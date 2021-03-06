package nl.siegmann.epublib;

import nl.siegmann.epublib.bookprocessor.CoverpageBookProcessor;
import nl.siegmann.epublib.bookprocessor.DefaultBookProcessorPipeline;
import nl.siegmann.epublib.bookprocessor.XslBookProcessor;
import nl.siegmann.epublib.chm.ChmParser;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.BookProcessor;
import nl.siegmann.epublib.epub.BookProcessorPipeline;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.fileset.FilesetBookCreator;
import nl.siegmann.epublib.util.VFSUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Fileset2Epub {

    public static void main(final String[] args) throws Exception {
        String inputLocation = "";
        String outLocation = "";
        String xslFile = "";
        String coverImage = "";
        String title = "";
        final List<String> authorNames = new ArrayList<String>();
        String type = "";
        String isbn = "";
        String inputEncoding = Constants.CHARACTER_ENCODING;
        final List<String> bookProcessorClassNames = new ArrayList<String>();

        for (int i = 0; i < args.length; i++) {
            if ("--in".equalsIgnoreCase(args[i])) {
                inputLocation = args[++i];
            } else if ("--out".equalsIgnoreCase(args[i])) {
                outLocation = args[++i];
            } else if ("--input-encoding".equalsIgnoreCase(args[i])) {
                inputEncoding = args[++i];
            } else if ("--xsl".equalsIgnoreCase(args[i])) {
                xslFile = args[++i];
            } else if ("--book-processor-class".equalsIgnoreCase(args[i])) {
                bookProcessorClassNames.add(args[++i]);
            } else if ("--cover-image".equalsIgnoreCase(args[i])) {
                coverImage = args[++i];
            } else if ("--author".equalsIgnoreCase(args[i])) {
                authorNames.add(args[++i]);
            } else if ("--title".equalsIgnoreCase(args[i])) {
                title = args[++i];
            } else if ("--isbn".equalsIgnoreCase(args[i])) {
                isbn = args[++i];
            } else if ("--type".equalsIgnoreCase(args[i])) {
                type = args[++i];
            }
        }
        if (StringUtils.isBlank(inputLocation) || StringUtils.isBlank(outLocation)) {
            usage();
        }
        final BookProcessorPipeline epubCleaner = new DefaultBookProcessorPipeline();
        epubCleaner.addBookProcessors(createBookProcessors(bookProcessorClassNames));
        final EpubWriter epubWriter = new EpubWriter(epubCleaner);
        if (!StringUtils.isBlank(xslFile)) {
            epubCleaner.addBookProcessor(new XslBookProcessor(xslFile));
        }

        if (StringUtils.isBlank(inputEncoding)) {
            inputEncoding = Constants.CHARACTER_ENCODING;
        }

        final Book book;
        if ("chm".equals(type)) {
            book = ChmParser.parseChm(VFSUtil.resolveFileObject(inputLocation), inputEncoding);
        } else {
            book = "epub".equals(type) ?
                   new EpubReader().readEpub(VFSUtil.resolveInputStream(inputLocation), inputEncoding) :
                   FilesetBookCreator
                           .createBookFromDirectory(VFSUtil.resolveFileObject(inputLocation), inputEncoding);
        }

        if (StringUtils.isNotBlank(coverImage)) {
//			book.getResourceByHref(book.getCoverImage());
            book.setCoverImage(new Resource(VFSUtil.resolveInputStream(coverImage), coverImage));
            epubCleaner.getBookProcessors().add(new CoverpageBookProcessor());
        }

        if (StringUtils.isNotBlank(title)) {
            final List<String> titles = new ArrayList<String>();
            titles.add(title);
            book.getMetadata().setTitles(titles);
        }

        if (StringUtils.isNotBlank(isbn)) {
            book.getMetadata().addIdentifier(new Identifier(Identifier.Scheme.ISBN, isbn));
        }

        initAuthors(authorNames, book);

        OutputStream result;
        try {
            result = VFS.getManager().resolveFile(outLocation).getContent().getOutputStream();
        } catch (FileSystemException e) {
            result = new FileOutputStream(outLocation);
        }
        epubWriter.write(book, result);
    }

    private static void initAuthors(final List<String> authorNames, final Book book) {
        if ((authorNames == null) || authorNames.isEmpty()) {
            return;
        }
        final List<Author> authorObjects = new ArrayList<Author>();
        for (final String authorName : authorNames) {
            final String[] authorNameParts = authorName.split(",");
            Author authorObject = null;
            if (authorNameParts.length > 1) {
                authorObject = new Author(authorNameParts[1], authorNameParts[0]);
            } else if (authorNameParts.length > 0) {
                authorObject = new Author(authorNameParts[0]);
            }
            authorObjects.add(authorObject);
        }
        book.getMetadata().setAuthors(authorObjects);
    }


    private static List<BookProcessor> createBookProcessors(final List<String> bookProcessorNames) {
        final List<BookProcessor> result = new ArrayList<BookProcessor>(bookProcessorNames.size());
        for (final String bookProcessorName : bookProcessorNames) {
            final BookProcessor bookProcessor;
            try {
                bookProcessor = (BookProcessor) Class.forName(bookProcessorName).newInstance();
                result.add(bookProcessor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static void usage() {
        System.out.println("usage: " + Fileset2Epub.class.getName()
                           + "\n  --author [lastname,firstname]"
                           + "\n  --cover-image [image to use as cover]"
                           + "\n  --input-ecoding [text encoding]  # The encoding of the input html files. If funny "
                           + "characters show"
                           + "\n                             # up in the result try 'iso-8859-1', "
                           + "'windows-1252' or 'utf-8'"
                           + "\n                             # If that doesn't work try to find an appropriate one from"
                           + "\n                             # this list: http://en.wikipedia"
                           + ".org/wiki/Character_encoding"
                           + "\n  --in [input directory]"
                           + "\n  --isbn [isbn number]"
                           + "\n  --out [output epub file]"
                           + "\n  --title [book title]"
                           + "\n  --type [input type, can be 'epub', 'chm' or empty]"
                           + "\n  --xsl [html post processing file]"
        );
        System.exit(0);
    }
}