/*
 * Copyright 2009-2010 László Balázs-Csíki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.menus;

import pixelitor.utils.GUIUtils;
import pixelitor.utils.Utils;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutDialog extends JDialog {
    private Box box;
    public static final String VERSION_NUMBER = "0.9.9";

    public AboutDialog(JFrame owner) {
        super(owner, "About Pixelitor");
        setLayout(new BorderLayout());

        box = Box.createVerticalBox();

        addLabel("<html><b><font size=+1>Pixelitor</font></b></html>");
        addLabel("Version " + VERSION_NUMBER);
        box.add(Box.createRigidArea(new Dimension(10, 20)));
        addLabel("  Copyright \u00A9 2009-2010 László Balázs-Csíki  ");
        addLabel("lbalazscs\u0040gmail.com");

        URI uri = null;
        try {
            uri = new URI("http://pixelitor.sourceforge.net");
        } catch (URISyntaxException e) {
            GUIUtils.showExceptionDialog(e);
        }
        JButton linkButton = new JButton("<HTML><FONT color=\"#000099\"><U>" + uri + "</U></FONT></HTML>");

        linkButton.setHorizontalAlignment(SwingConstants.CENTER);
        linkButton.setBorderPainted(false);
        linkButton.setFocusPainted(false);
        linkButton.setOpaque(false);
        linkButton.setBackground(getBackground());
        linkButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        final URI finalURIVar = uri;
        linkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Utils.openURI(finalURIVar);
            }
        });

        box.add(linkButton);
        box.add(Box.createGlue());

        add(box, BorderLayout.CENTER);

        JPanel p2 = new JPanel();
        JButton ok = new JButton("Ok");
        p2.add(ok);
        add(p2, BorderLayout.SOUTH);

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
                dispose();
            }
        });

        pack();

        GUIUtils.centerOnScreen(this);
        setVisible(true);
    }

    private void addLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(label);
    }

}


