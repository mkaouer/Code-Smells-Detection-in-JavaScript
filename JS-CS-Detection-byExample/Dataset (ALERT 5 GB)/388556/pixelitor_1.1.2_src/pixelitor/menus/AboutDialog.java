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

import pixelitor.Build;
import pixelitor.PixelitorWindow;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.OKDialog;
import pixelitor.utils.OpenInBrowserAction;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class AboutDialog extends OKDialog {
    private static Box box;

    public AboutDialog(JFrame owner, JComponent form) {
        super(owner, "About Pixelitor", form);
    }

    public static void showDialog() {
        box = Box.createVerticalBox();

        addLabel("<html><b><font size=+1>Pixelitor</font></b></html>");
        addLabel("Version " + Build.VERSION_NUMBER);
        box.add(Box.createRigidArea(new Dimension(10, 20)));
        addLabel("  Copyright \u00A9 2009-2010 László Balázs-Csíki  ");
        addLabel("lbalazscs\u0040gmail.com");

        String homePage = "http://pixelitor.sourceforge.net";
        OpenInBrowserAction browserAction = new OpenInBrowserAction(null, homePage);

        JButton linkButton = new JButton("<HTML><FONT color=\"#000099\"><U>" + homePage + "</U></FONT></HTML>");

        linkButton.setHorizontalAlignment(SwingConstants.CENTER);
        linkButton.setBorderPainted(false);
        linkButton.setFocusPainted(false);
        linkButton.setOpaque(false);
        linkButton.setBackground(box.getBackground());
        linkButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        linkButton.addActionListener(browserAction);

        box.add(linkButton);
        box.add(Box.createGlue());

        new AboutDialog(PixelitorWindow.getInstance(), box);
    }

    private static void addLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(label);
    }

}


