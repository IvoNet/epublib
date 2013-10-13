package nl.siegmann.epublib.epub;

import org.junit.Test;
import org.w3c.dom.Document;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PackageDocumentReaderTest {

    @Test
    public void testFindCoverHref_content1() {
        final EpubReader epubReader = new EpubReader();
        final Document packageDocument;
        try {
            packageDocument = EpubProcessorSupport.createDocumentBuilder()
                    .parse(PackageDocumentReaderTest.class.getResourceAsStream("/opf/test1.opf"));
            final Collection<String> coverHrefs = PackageDocumentReader.findCoverHrefs(packageDocument);
            assertEquals(1, coverHrefs.size());
            assertEquals("cover.html", coverHrefs.iterator().next());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
