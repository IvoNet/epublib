package nl.siegmann.epublib.viewer;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MetadataPane extends JPanel implements NavigationEventListener {

    private static final Logger log = LoggerFactory.getLogger(MetadataPane.class);

    private static final long serialVersionUID = -2810193923996466948L;
    private final JScrollPane scrollPane;

    public MetadataPane(final Navigator navigator) {
        super(new GridLayout(1, 0));
        this.scrollPane = (JScrollPane) add(new JScrollPane());
        navigator.addNavigationEventListener(this);
        initBook(navigator.getBook());
    }

    private void initBook(final Book book) {
        if (book == null) {
            return;
        }
        final JTable table = new JTable(
                createTableData(book.getMetadata()),
                new String[]{"", ""});
        table.setEnabled(false);
        table.setFillsViewportHeight(true);
        final JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.add(table, BorderLayout.CENTER);
        setCoverImage(contentPanel, book);

        this.scrollPane.getViewport().removeAll();
        this.scrollPane.getViewport().add(contentPanel);
    }

    private void setCoverImage(final JPanel contentPanel, final Book book) {
        if (book == null) {
            return;
        }
        final Resource coverImageResource = book.getCoverImage();
        if (coverImageResource == null) {
            return;
        }
        try {
            Image image = ImageIO.read(coverImageResource.getInputStream());
            if (image == null) {
                log.error("Unable to load cover image from book");
                return;
            }
            image = image.getScaledInstance(200, -1, Image.SCALE_SMOOTH);
            final JLabel label = new JLabel(new ImageIcon(image));
//			label.setSize(100, 100);
            contentPanel.add(label, BorderLayout.NORTH);
        } catch (IOException e) {
            log.error("Unable to load cover image from book", e.getMessage());
        }
    }

    private Object[][] createTableData(final Metadata metadata) {
        final List<String[]> result = new ArrayList<String[]>();
        addStrings(metadata.getIdentifiers(), "Identifier", result);
        addStrings(metadata.getTitles(), "Title", result);
        addStrings(metadata.getAuthors(), "Author", result);
        result.add(new String[]{"Language", metadata.getLanguage()});
        addStrings(metadata.getContributors(), "Contributor", result);
        addStrings(metadata.getDescriptions(), "Description", result);
        addStrings(metadata.getPublishers(), "Publisher", result);
        addStrings(metadata.getDates(), "Date", result);
        addStrings(metadata.getSubjects(), "Subject", result);
        addStrings(metadata.getTypes(), "Type", result);
        addStrings(metadata.getRights(), "Rights", result);
        result.add(new String[]{"Format", metadata.getFormat()});
        return result.toArray(new Object[result.size()][2]);
    }

    private void addStrings(final List<?> values, final String label, final List<String[]> result) {
        boolean labelWritten = false;
        for (final Object value : values) {
            if (value == null) {
                continue;
            }
            final String valueString = String.valueOf(value);
            if (StringUtils.isBlank(valueString)) {
                continue;
            }

            String currentLabel = "";
            if (!labelWritten) {
                currentLabel = label;
                labelWritten = true;
            }
            result.add(new String[]{currentLabel, valueString});
        }

    }

    private TableModel createTableModel(final Navigator navigator) {
        return new AbstractTableModel() {

            @Override
            public Object getValueAt(final int rowIndex, final int columnIndex) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getRowCount() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getColumnCount() {
                return 2;
            }
        };
    }

    @Override
    public void navigationPerformed(final NavigationEvent navigationEvent) {
        if (navigationEvent.isBookChanged()) {
            initBook(navigationEvent.getCurrentBook());
        }
    }
}
