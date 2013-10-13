package nl.siegmann.epublib.fileset;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Book;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.NameScope;
import org.apache.commons.vfs.VFS;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FilesetBookCreatorTest extends TestCase {

    public void test1() {
        try {
            final FileObject dir = createDirWithSourceFiles();
            final Book book = FilesetBookCreator.createBookFromDirectory(dir);
            assertEquals(5, book.getSpine().size());
            assertEquals(5, book.getTableOfContents().size());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void test2() {
        try {
            final FileObject dir = createDirWithSourceFiles();

            // this file should be ignored
            copyInputStreamToFileObject(new ByteArrayInputStream("hi".getBytes()), dir, "foo.nonsense");

            final Book book = FilesetBookCreator.createBookFromDirectory(dir);
            assertEquals(5, book.getSpine().size());
            assertEquals(5, book.getTableOfContents().size());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private FileObject createDirWithSourceFiles() throws IOException {
        final FileSystemManager fsManager = VFS.getManager();
        final FileObject dir = fsManager.resolveFile("ram://fileset_test_dir");
        dir.createFolder();
        final String[] sourceFiles = {
                "book1.css",
                "chapter1.html",
                "chapter2_1.html",
                "chapter2.html",
                "chapter3.html",
                "cover.html",
                "flowers_320x240.jpg",
                "cover.png"
        };
        final String testSourcesDir = "/book1";
        for (final String filename : sourceFiles) {
            final String sourceFileName = testSourcesDir + "/" + filename;
            copyResourceToFileObject(sourceFileName, dir, filename);
        }
        return dir;
    }

    private void copyResourceToFileObject(final String resourceUrl, final FileObject targetDir,
                                          final String targetFilename) throws IOException {
        final InputStream inputStream = this.getClass().getResourceAsStream(resourceUrl);
        copyInputStreamToFileObject(inputStream, targetDir, targetFilename);
    }

    private void copyInputStreamToFileObject(final InputStream inputStream, final FileObject targetDir,
                                             final String targetFilename) throws IOException {
        final FileObject targetFile = targetDir.resolveFile(targetFilename, NameScope.DESCENDENT);
        targetFile.createFile();
        IOUtils.copy(inputStream, targetFile.getContent().getOutputStream());
        targetFile.getContent().close();
    }
}
