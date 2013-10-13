package nl.siegmann.epublib.viewer;

import nl.siegmann.epublib.browsersupport.Navigator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Creates a panel with the first,previous,next and last buttons.
 *
 */
class ButtonBar extends JPanel {
    private static final long serialVersionUID = 6431437924245035812L;

    private final JButton startButton = ViewerUtil.createButton("chapter-first", "|<");
    private final JButton previousChapterButton = ViewerUtil.createButton("chapter-previous", "<<");
    private final JButton previousPageButton = ViewerUtil.createButton("page-previous", "<");
    private final JButton nextPageButton = ViewerUtil.createButton("page-next", ">");
    private final JButton nextChapterButton = ViewerUtil.createButton("chapter-next", ">>");
    private final JButton endButton = ViewerUtil.createButton("chapter-last", ">|");
    private final ContentPane chapterPane;
    private final ValueHolder<Navigator> navigatorHolder = new ValueHolder<Navigator>();

    public ButtonBar(final Navigator navigator, final ContentPane chapterPane) {
        super(new GridLayout(0, 4));
        this.chapterPane = chapterPane;

        final JPanel bigPrevious = new JPanel(new GridLayout(0, 2));
        bigPrevious.add(this.startButton);
        bigPrevious.add(this.previousChapterButton);
        add(bigPrevious);

        add(this.previousPageButton);
        add(this.nextPageButton);

        final JPanel bigNext = new JPanel(new GridLayout(0, 2));
        bigNext.add(this.nextChapterButton);
        bigNext.add(this.endButton);
        add(bigNext);

        setSectionWalker(navigator);
    }

    void setSectionWalker(final Navigator navigator) {
        this.navigatorHolder.setValue(navigator);

        this.startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                ButtonBar.this.navigatorHolder.getValue().gotoFirstSpineSection(ButtonBar.this);
            }
        });
        this.previousChapterButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                ButtonBar.this.navigatorHolder.getValue().gotoPreviousSpineSection(ButtonBar.this);
            }
        });
        this.previousPageButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                ButtonBar.this.chapterPane.gotoPreviousPage();
            }
        });

        this.nextPageButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                ButtonBar.this.chapterPane.gotoNextPage();
            }
        });
        this.nextChapterButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                ButtonBar.this.navigatorHolder.getValue().gotoNextSpineSection(ButtonBar.this);
            }
        });

        this.endButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                ButtonBar.this.navigatorHolder.getValue().gotoLastSpineSection(ButtonBar.this);
            }
        });
    }
}
