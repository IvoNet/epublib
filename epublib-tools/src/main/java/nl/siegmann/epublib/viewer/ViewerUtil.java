package nl.siegmann.epublib.viewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

final class ViewerUtil {

    private static final Logger log = LoggerFactory.getLogger(ViewerUtil.class);

    private ViewerUtil() {
    }

    /**
     * Creates a button with the given icon. The icon will be loaded from the classpath.
     * If loading the icon is unsuccessful it will use the defaultLabel.
     *
     * @param iconName
     * @param backupLabel
     * @return a button with the given icon.
     */
    // package
    static JButton createButton(final String iconName, final String backupLabel) {
        final JButton result;
        final ImageIcon icon = createImageIcon(iconName);
        result = icon == null ? new JButton(backupLabel) : new JButton(icon);
        return result;
    }


    static ImageIcon createImageIcon(final String iconName) {
        ImageIcon result = null;
        final String fullIconPath = "/viewer/icons/" + iconName + ".png";
        try {
            final Image image = ImageIO.read(ViewerUtil.class.getResourceAsStream(fullIconPath));
            result = new ImageIcon(image);
        } catch (Exception e) {
            log.error("Icon \'" + fullIconPath + "\' not found");
        }
        return result;
    }
}
