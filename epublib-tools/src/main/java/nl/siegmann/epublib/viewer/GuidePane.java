package nl.siegmann.epublib.viewer;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Guide;
import nl.siegmann.epublib.domain.GuideReference;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a Panel for navigating a Book via its Guide
 *
 * @author paul
 *
 */
public class GuidePane extends JScrollPane implements NavigationEventListener {

    private static final long serialVersionUID = -8988054938907109295L;
    private final Navigator navigator;

    public GuidePane(final Navigator navigator) {
        this.navigator = navigator;
        navigator.addNavigationEventListener(this);
        initBook(navigator.getBook());
    }

    private void initBook(final Book book) {
        if (book == null) {
            return;
        }
        getViewport().removeAll();
        final JTable table = new JTable(
                createTableData(this.navigator.getBook().getGuide()),
                new String[]{"", ""});
//		table.setEnabled(false);
        table.setFillsViewportHeight(true);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (GuidePane.this.navigator.getBook() == null) {
                    return;
                }
                final int guideIndex = e.getFirstIndex();
                final GuideReference guideReference = GuidePane.this.navigator.getBook().getGuide().getReferences()
                        .get(guideIndex);

                GuidePane.this.navigator.gotoResource(guideReference.getResource(), GuidePane.this);
            }
        });
        getViewport().add(table);
    }

    private Object[][] createTableData(final Guide guide) {
        final List<String[]> result = new ArrayList<String[]>();
        for (final GuideReference guideReference : guide.getReferences()) {
            result.add(new String[]{guideReference.getType(), guideReference.getTitle()});
        }
        return result.toArray(new Object[result.size()][2]);
    }

    @Override
    public void navigationPerformed(final NavigationEvent navigationEvent) {
        if (navigationEvent.isBookChanged()) {
            initBook(navigationEvent.getCurrentBook());
        }
    }
}
