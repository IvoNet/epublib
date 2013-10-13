package nl.siegmann.epublib.viewer;

import nl.siegmann.epublib.browsersupport.Navigator;

import javax.swing.*;
import java.awt.*;

public class BrowseBar extends JPanel {

    private static final long serialVersionUID = -5745389338067538254L;

    public BrowseBar(final Navigator navigator, final ContentPane chapterPane) {
        super(new BorderLayout());
        add(new ButtonBar(navigator, chapterPane), BorderLayout.CENTER);
        add(new SpineSlider(navigator), BorderLayout.NORTH);
    }
}
