package nl.siegmann.epublib.viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * First stab at an about dialog.
 *
 * @author paul.siegmann
 *
 */
public class AboutDialog extends JDialog {

    private static final long serialVersionUID = -1766802200843275782L;

    public AboutDialog(final JFrame parent) {
        super(parent, true);

        setResizable(false);
        getContentPane().setLayout(new GridLayout(3, 1));
        setSize(400, 150);
        setTitle("About epublib");
        setLocationRelativeTo(parent);

        final JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                AboutDialog.this.dispose();
            }
        });
        getRootPane().setDefaultButton(close);
        add(new JLabel("epublib viewer"));
        add(new JLabel("http://www.siegmann.nl/epublib"));
        add(close);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                AboutDialog.this.dispose();
            }
        });
        pack();
        setVisible(true);

    }
}