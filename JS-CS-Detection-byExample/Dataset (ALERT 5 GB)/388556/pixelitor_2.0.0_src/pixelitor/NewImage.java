/*
 * Copyright 2009-2014 Laszlo Balazs-Csiki
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

import pixelitor.utils.AppPreferences;
import pixelitor.utils.GridBagHelper;
import pixelitor.utils.IntTextField;
import pixelitor.utils.OKCancelDialog;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * Static utility methods related to creating new images
 */
public final class NewImage {
    private static int untitledCount = 1;

    private static Dimension lastNew;

    private NewImage() {
    }

    public static void addNewImage(FillType bg, int width, int height, String title) {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
        fillImage(newImage, bg);
        PixelitorWindow.getInstance().addNewImage(newImage, null, title);
    }

    private static void fillImage(BufferedImage img, FillType bg) {
        if (bg == FillType.TRANSPARENT) {
            return;
        }
        Color c = bg.getColor();
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        Graphics2D g = img.createGraphics();
        g.setColor(c);
        g.fillRect(0, 0, imgWidth, imgHeight);
        g.dispose();
    }

    private static void showInDialog() {
        if(lastNew == null) {
            lastNew = AppPreferences.loadNewImageSize();
        }
        final NewImagePanel p = new NewImagePanel(lastNew.width, lastNew.height);
        OKCancelDialog d = new OKCancelDialog(p, "New Image") {
            @Override
            public void dialogAccepted() {
                super.dialogAccepted();

                int selectedWidth = p.getSelectedWidth();
                int selectedHeight = p.getSelectedHeight();
                FillType bg = p.getSelectedBackground();

                String title = "Untitled" + untitledCount;
                addNewImage(bg, selectedWidth, selectedHeight, title);
                untitledCount++;

                lastNew.width = selectedWidth;
                lastNew.height = selectedHeight;

                dispose();
            }

            @Override
            public void dialogCanceled() {
                super.dialogCanceled();
                dispose();
            }
        };
        d.setVisible(true);
    }

    public static Action getAction() {
        return new AbstractAction("New Image...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInDialog();
            }
        };
    }

    public static Dimension getLastNew() {
        return lastNew;
    }

    private static class NewImagePanel extends JPanel {
        private final JTextField widthTextField;
        private final JTextField heightTextField;

        private static final int WIDGET_DISTANCE = 5;
        private final JComboBox<FillType> backgroundSelector;

        private NewImagePanel(int defaultWidth, int defaultHeight) {
            setLayout(new GridBagLayout());

            setBorder(BorderFactory.createEmptyBorder(WIDGET_DISTANCE, WIDGET_DISTANCE, WIDGET_DISTANCE, WIDGET_DISTANCE));

            GridBagHelper.addLabel(this, "Width:", 0, 0);
            widthTextField = new IntTextField(String.valueOf(defaultWidth));
            GridBagHelper.addControl(this, widthTextField);

            GridBagHelper.addLabel(this, "Height:", 0, 1);
            heightTextField = new IntTextField(String.valueOf(defaultHeight));
            GridBagHelper.addControl(this, heightTextField);

            GridBagHelper.addLabel(this, "Fill:", 0, 2);
            backgroundSelector = new JComboBox(FillType.values());
            GridBagHelper.addControl(this, backgroundSelector);
        }

        private int getSelectedWidth() {
            return Integer.parseInt(widthTextField.getText());
        }

        private int getSelectedHeight() {
            return Integer.parseInt(heightTextField.getText());
        }

        private FillType getSelectedBackground() {
            return (FillType) backgroundSelector.getSelectedItem();
        }
    }
}

