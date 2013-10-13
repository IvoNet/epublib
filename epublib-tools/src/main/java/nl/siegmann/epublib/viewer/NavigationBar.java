package nl.siegmann.epublib.viewer;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.NavigationHistory;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.search.SearchIndex;
import nl.siegmann.epublib.search.SearchResult;
import nl.siegmann.epublib.search.SearchResults;
import nl.siegmann.epublib.util.ToolsResourceUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A toolbar that contains the history back and forward buttons and the page title.
 *
 * @author paul.siegmann
 *
 */
public class NavigationBar extends JToolBar implements NavigationEventListener {

    /**
     *
     */
    private static final long serialVersionUID = 1166410773448311544L;
    private final JTextField titleField;
    private JTextField searchField;
    private final NavigationHistory navigationHistory;
    private final Navigator navigator;
    private final SearchIndex searchIndex = new SearchIndex();
    private String previousSearchTerm;
    private int searchResultIndex = -1;
    private SearchResults searchResults;

    public NavigationBar(final Navigator navigator) {
        this.navigationHistory = new NavigationHistory(navigator);
        this.navigator = navigator;
        navigator.addNavigationEventListener(this);
        addHistoryButtons();
        this.titleField = (JTextField) add(new JTextField());
        addSearchButtons();
        initBook(navigator.getBook());
    }

    private void initBook(final Book book) {
        if (book == null) {
            return;
        }
        this.searchIndex.initBook(book);
    }

    private void addHistoryButtons() {
        final Font historyButtonFont = new Font("SansSerif", Font.BOLD, 24);
        final JButton previousButton = ViewerUtil.createButton("history-previous", "<=");
        previousButton.setFont(historyButtonFont);
//		previousButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));

        previousButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                NavigationBar.this.navigationHistory.move(-1);
            }
        });

        add(previousButton);

        final JButton nextButton = ViewerUtil.createButton("history-next", "=>");
        nextButton.setFont(historyButtonFont);
        nextButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                NavigationBar.this.navigationHistory.move(1);
            }
        });
        add(nextButton);
    }

    private void doSearch(final int move) {
        final String searchTerm = this.searchField.getText();
        if (searchTerm.equals(this.previousSearchTerm)) {
            this.searchResultIndex += move;
        } else {
            this.searchResults = this.searchIndex.doSearch(searchTerm);
            this.previousSearchTerm = searchTerm;
            this.searchResultIndex = 0;
        }
        if (this.searchResultIndex < 0) {
            this.searchResultIndex = this.searchResults.size() - 1;
        } else if (this.searchResultIndex >= this.searchResults.size()) {
            this.searchResultIndex = 0;
        }
        if (!this.searchResults.isEmpty()) {
            final SearchResult searchResult = this.searchResults.getHits().get(this.searchResultIndex);
            this.navigator.gotoResource(searchResult.getResource(), searchResult.getPagePos(), NavigationBar.this);
        }

    }

    private void addSearchButtons() {
        final JPanel searchForm = new JPanel(new BorderLayout());
        searchForm.setPreferredSize(new Dimension(200, 28));
        final Font historyButtonFont = new Font("SansSerif", Font.BOLD, 20);
        final JButton previousButton = ViewerUtil.createButton("search-previous", "<");
        previousButton.setFont(historyButtonFont);
//		previousButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));

        previousButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                doSearch(-1);
            }
        });

        searchForm.add(previousButton, BorderLayout.WEST);

        this.searchField = new JTextField();
//		JPanel searchInput = new JPanel();
//		searchInput.add(new JLabel(ViewerUtil.createImageIcon("search-icon")));
//		searchInput.add(searchField);
        this.searchField.setMinimumSize(new Dimension(100, 20));
        this.searchField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(final KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(final KeyEvent e) {
            }

            @Override
            public void keyReleased(final KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    doSearch(1);
                }
            }
        });
//		searchInput.setMinimumSize(new Dimension(140, 20));
        searchForm.add(this.searchField, BorderLayout.CENTER);
        final JButton nextButton = ViewerUtil.createButton("search-next", ">");
        nextButton.setFont(historyButtonFont);
        nextButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                doSearch(1);
            }
        });
        searchForm.add(nextButton, BorderLayout.EAST);
        add(searchForm);
    }

    @Override
    public void navigationPerformed(final NavigationEvent navigationEvent) {
        if (navigationEvent.isBookChanged()) {
            initBook(navigationEvent.getCurrentBook());
        }
        if (navigationEvent.getCurrentResource() != null) {
            final String title = ToolsResourceUtil.getTitle(navigationEvent.getCurrentResource());
            this.titleField.setText(title);
        }
    }
}