/*
 * Copyright 2009 László Balázs-Csíki
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

package pixelitor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

import pixelitor.utils.IntTextField;
import pixelitor.utils.OKCancelDialog;

public final class NewImagePanel extends JPanel {
    private final JTextField widthTextField;
    private final JTextField heightTextField;

    private static final int DISTANCE = 5;

    private NewImagePanel() {
        setLayout(new GridLayout(2, 2, DISTANCE, DISTANCE));
        setBorder(BorderFactory.createEmptyBorder(DISTANCE, DISTANCE, DISTANCE, DISTANCE));


        add(new JLabel("width:", SwingConstants.RIGHT));
        widthTextField = new IntTextField("400");
        add(widthTextField);

        add(new JLabel("height:", SwingConstants.RIGHT));
        heightTextField = new IntTextField("400");
        add(heightTextField);
    }

    private int getSelectedWidth() {
        return Integer.parseInt(widthTextField.getText());
    }

    private int getSelectedHeight() {
        return Integer.parseInt(heightTextField.getText());
    }


    private static void showInDialog() {
        final NewImagePanel p = new NewImagePanel();
        new OKCancelDialog(p, PixelitorWindow.getInstance(), "New Image", true) {
            @Override
            public void dialogAccepted() {
                int selectedWidth = p.getSelectedWidth();
                int selectedHeight = p.getSelectedHeight();
                BufferedImage newImage = new BufferedImage(selectedWidth, selectedHeight, BufferedImage.TYPE_INT_RGB);
                AppLogic.addUntitledImage(newImage);
                dispose();
            }

            @Override
            public void dialogCancelled() {
                dispose();
            }
        };
    }

    public static Action getAction() {
        return new AbstractAction("New Image...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInDialog();
            }
        };
    }
}
