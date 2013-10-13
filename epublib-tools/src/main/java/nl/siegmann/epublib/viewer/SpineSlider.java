package nl.siegmann.epublib.viewer;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// package
class SpineSlider extends JSlider implements NavigationEventListener {

    /**
     *
     */
    private static final long serialVersionUID = 8436441824668551056L;
    private final Navigator navigator;

    public SpineSlider(final Navigator navigator) {
        super(JSlider.HORIZONTAL);
        this.navigator = navigator;
        navigator.addNavigationEventListener(this);
        setPaintLabels(false);
        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent evt) {
                final JSlider slider = (JSlider) evt.getSource();
                final int value = slider.getValue();
                SpineSlider.this.navigator.gotoSpineSection(value, SpineSlider.this);
            }
        });
        initBook(navigator.getBook());
    }

    private void initBook(final Book book) {
        if (book == null) {
            return;
        }
        setMinimum(0);
        setMaximum(book.getSpine().size() - 1);
        setValue(0);
//			setPaintTicks(true);
        updateToolTip();
    }

    private void updateToolTip() {
        String tooltip = "";
        if ((this.navigator.getCurrentSpinePos() >= 0) && (this.navigator.getBook() != null)) {
            tooltip = (this.navigator.getCurrentSpinePos() + 1) + " / " + this.navigator.getBook().getSpine().size();
        }
        setToolTipText(tooltip);
    }

    @Override
    public void navigationPerformed(final NavigationEvent navigationEvent) {
        updateToolTip();
        if (this == navigationEvent.getSource()) {
            return;
        }

        if (navigationEvent.isBookChanged()) {
            initBook(navigationEvent.getCurrentBook());
        } else if (navigationEvent.isResourceChanged()) {
            setValue(navigationEvent.getCurrentSpinePos());
        }
    }

}