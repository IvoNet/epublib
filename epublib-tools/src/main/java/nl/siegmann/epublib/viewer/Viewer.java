package nl.siegmann.epublib.viewer;

import nl.siegmann.epublib.browsersupport.NavigationHistory;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.BookProcessor;
import nl.siegmann.epublib.epub.BookProcessorPipeline;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.EpubWriter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;


public class Viewer {

    private static final Logger log = LoggerFactory.getLogger(Viewer.class);
    private final JFrame mainWindow;
    private JSplitPane mainSplitPane;
    private JSplitPane rightSplitPane;
    private final Navigator navigator = new Navigator();
    private NavigationHistory browserHistory;
    private final BookProcessorPipeline epubCleaner = new BookProcessorPipeline(Collections.<BookProcessor>emptyList());

    private Viewer(final InputStream bookStream) {
        this.mainWindow = createMainWindow();
        final Book book;
        try {
            book = (new EpubReader()).readEpub(bookStream);
            gotoBook(book);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Viewer(final Book book) {
        this.mainWindow = createMainWindow();
        gotoBook(book);
    }

    private JFrame createMainWindow() {
        final JFrame result = new JFrame();
        result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        result.setJMenuBar(createMenuBar());

        final JPanel mainPanel = new JPanel(new BorderLayout());

        final JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.setTopComponent(new TableOfContentsPane(this.navigator));
        leftSplitPane.setBottomComponent(new GuidePane(this.navigator));
        leftSplitPane.setOneTouchExpandable(true);
        leftSplitPane.setContinuousLayout(true);
        leftSplitPane.setResizeWeight(0.8);

        this.rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.rightSplitPane.setOneTouchExpandable(true);
        this.rightSplitPane.setContinuousLayout(true);
        this.rightSplitPane.setResizeWeight(1.0);
        final ContentPane htmlPane = new ContentPane(this.navigator);
        final JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(htmlPane, BorderLayout.CENTER);
        final BrowseBar browseBar = new BrowseBar(this.navigator, htmlPane);
        contentPanel.add(browseBar, BorderLayout.SOUTH);
        this.rightSplitPane.setLeftComponent(contentPanel);
        this.rightSplitPane.setRightComponent(new MetadataPane(this.navigator));

        this.mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.mainSplitPane.setLeftComponent(leftSplitPane);
        this.mainSplitPane.setRightComponent(this.rightSplitPane);
        this.mainSplitPane.setOneTouchExpandable(true);
        this.mainSplitPane.setContinuousLayout(true);
        this.mainSplitPane.setResizeWeight(0.0);

        mainPanel.add(this.mainSplitPane, BorderLayout.CENTER);
        mainPanel.setPreferredSize(new Dimension(1000, 750));
        mainPanel.add(new NavigationBar(this.navigator), BorderLayout.NORTH);

        result.add(mainPanel);
        result.pack();
        setLayout(Layout.TocContentMeta);
        result.setVisible(true);
        return result;
    }


    private void gotoBook(final Book book) {
        this.mainWindow.setTitle(book.getTitle());
        this.navigator.gotoBook(book, this);
    }

    private static String getText(final String text) {
        return text;
    }

    private static JFileChooser createFileChooser(File startDir) {
        if (startDir == null) {
            startDir = new File(System.getProperty("user.home"));
            if (!startDir.exists()) {
                startDir = null;
            }
        }
        final JFileChooser fileChooser = new JFileChooser(startDir);
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("EPub files", "epub"));

        return fileChooser;
    }

    private JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = new JMenu(getText("File"));
        menuBar.add(fileMenu);
        final JMenuItem openFileMenuItem = new JMenuItem(getText("Open"));
        openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        openFileMenuItem.addActionListener(new ActionListener() {

            private File previousDir;

            @Override
            public void actionPerformed(final ActionEvent e) {
                final JFileChooser fileChooser = createFileChooser(previousDir);
                final int returnVal = fileChooser.showOpenDialog(Viewer.this.mainWindow);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                final File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile == null) {
                    return;
                }
                if (!selectedFile.isDirectory()) {
                    previousDir = selectedFile.getParentFile();
                }
                try {
                    final Book book = (new EpubReader()).readEpub(new FileInputStream(selectedFile));
                    gotoBook(book);
                } catch (Exception e1) {
                    log.error(e1.getMessage(), e1);
                }
            }
        });
        fileMenu.add(openFileMenuItem);

        final JMenuItem saveFileMenuItem = new JMenuItem(getText("Save as ..."));
        saveFileMenuItem
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        saveFileMenuItem.addActionListener(new ActionListener() {

            private File previousDir;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (Viewer.this.navigator.getBook() == null) {
                    return;
                }
                final JFileChooser fileChooser = createFileChooser(previousDir);
                final int returnVal = fileChooser.showOpenDialog(Viewer.this.mainWindow);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                final File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile == null) {
                    return;
                }
                if (!selectedFile.isDirectory()) {
                    previousDir = selectedFile.getParentFile();
                }
                try {
                    (new EpubWriter()).write(Viewer.this.navigator.getBook(), new FileOutputStream(selectedFile));
                } catch (Exception e1) {
                    log.error(e1.getMessage(), e1);
                }
            }
        });
        fileMenu.add(saveFileMenuItem);

        final JMenuItem reloadMenuItem = new JMenuItem(getText("Reload"));
        reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        reloadMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                gotoBook(Viewer.this.navigator.getBook());
            }
        });
        fileMenu.add(reloadMenuItem);

        final JMenuItem exitMenuItem = new JMenuItem(getText("Exit"));
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        exitMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        final JMenu viewMenu = new JMenu(getText("View"));
        menuBar.add(viewMenu);

        final JMenuItem viewTocContentMenuItem = new JMenuItem(getText("TOCContent"), ViewerUtil
                .createImageIcon("layout-toc-content"));
        viewTocContentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_MASK));
        viewTocContentMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                setLayout(Layout.TocContent);
            }
        });
        viewMenu.add(viewTocContentMenuItem);

        final JMenuItem viewContentMenuItem = new JMenuItem(getText("Content"), ViewerUtil
                .createImageIcon("layout-content"));
        viewContentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK));
        viewContentMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                setLayout(Layout.Content);
            }
        });
        viewMenu.add(viewContentMenuItem);

        final JMenuItem viewTocContentMetaMenuItem = new JMenuItem(getText("TocContentMeta"), ViewerUtil
                .createImageIcon("layout-toc-content-meta"));
        viewTocContentMetaMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_MASK));
        viewTocContentMetaMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                setLayout(Layout.TocContentMeta);
            }
        });
        viewMenu.add(viewTocContentMetaMenuItem);

        final JMenu helpMenu = new JMenu(getText("Help"));
        menuBar.add(helpMenu);
        final JMenuItem aboutMenuItem = new JMenuItem(getText("About"));
        aboutMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                new AboutDialog(Viewer.this.mainWindow);
            }
        });
        helpMenu.add(aboutMenuItem);

        return menuBar;
    }

    private enum Layout {
        TocContentMeta,
        TocContent,
        Content
    }

    private class LayoutX {
        private boolean tocPaneVisible;
        private boolean contentPaneVisible;
        private boolean metaPaneVisible;

    }

    private void setLayout(final Layout layout) {
        switch (layout) {
            case Content:
                this.mainSplitPane.setDividerLocation(0.0d);
                this.rightSplitPane.setDividerLocation(1.0d);
                break;
            case TocContent:
                this.mainSplitPane.setDividerLocation(0.2d);
                this.rightSplitPane.setDividerLocation(1.0d);
                break;
            case TocContentMeta:
                this.mainSplitPane.setDividerLocation(0.2d);
                this.rightSplitPane.setDividerLocation(0.6d);
                break;
        }
    }

    private static InputStream getBookInputStream(final String[] args) {
        // jquery-fundamentals-book.epub
//		final Book book = (new EpubReader()).readEpub(new FileInputStream("/home/paul/test2_book1.epub"));
//		final Book book = (new EpubReader()).readEpub(new FileInputStream("/home/paul/three_men_in_a_boat_jerome_k_jerome
// .epub"));

//		String bookFile = "/home/paul/test2_book1.epub";
//		bookFile = "/home/paul/project/private/library/epub/this_dynamic_earth-AAH813.epub";

        String bookFile = null;
        if (args.length > 0) {
            bookFile = args[0];
        }
        InputStream result = null;
        if (!StringUtils.isBlank(bookFile)) {
            try {
                result = new FileInputStream(bookFile);
            } catch (Exception e) {
                log.error("Unable to open " + bookFile, e);
            }
        }
        if (result == null) {
            result = Viewer.class.getResourceAsStream("/viewer/epublibviewer-help.epub");
        }
        return result;
    }


    public static void main(final String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.error("Unable to set native look and feel", e);
        }

        final InputStream bookStream = getBookInputStream(args);
//		final Book book = readBook(args);

        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Viewer(bookStream);
            }
        });
    }
}
