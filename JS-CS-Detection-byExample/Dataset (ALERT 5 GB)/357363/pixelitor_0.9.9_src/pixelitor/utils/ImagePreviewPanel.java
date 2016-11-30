/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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

package pixelitor.utils;

import pixelitor.io.FileExtensionUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;


public class ImagePreviewPanel extends JPanel
        implements PropertyChangeListener {
    private static final int SIZE = 200;
    private static final int EMPTY_SPACE_AT_LEFT = 5;

    private Image smallImage;
    private Color backgroundColor;
    private int newImgWidth;
    private int newImgHeight;
    private int imgWidth;
    private int imgHeight;
    private static final int MSG_STRING_X = 20;
    private static final int MSG_STRING_Y = 31;

    public ImagePreviewPanel() {
        setPreferredSize(new Dimension(SIZE, SIZE));
        backgroundColor = getBackground();
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();
        File file;

        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propertyName)) {
            file = null;
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propertyName)) {
            file = (File) e.getNewValue();
        } else {
            return;
        }
        if (file == null) {
            smallImage = null;
            repaint();
            return;
        }

        String fileName = file.getAbsolutePath();
        Image bigImage = null;

        if (FileExtensionUtils.isSupportedExtension(fileName, FileExtensionUtils.SUPPORTED_INPUT_FORMATS)) {
            if (fileName.toLowerCase().endsWith(".bmp")) {
                try {
                    bigImage = ImageIO.read(file);
                } catch (IOException ex) {
                    GUIUtils.showExceptionDialog(ex);
                }
            } else {
                // TODO: load all with ImageIO and cache
                // it seems that ImageIcon loads slower but it is cached
                ImageIcon icon = new ImageIcon(fileName);
                bigImage = icon.getImage();
            }

            smallImage = scaleImage(bigImage);
            repaint();
        }
    }

    private Image scaleImage(Image img) {
        if (img == null) {
            return null;
        }

        imgWidth = img.getWidth(null);
        imgHeight = img.getHeight(null);

        int availableWidth = getWidth() - EMPTY_SPACE_AT_LEFT;
        int availableHeight = getHeight();

        double heightScale = availableHeight / (double) imgHeight;
        double widthScale = availableWidth / (double) imgWidth;

        double scale = Math.min(heightScale, widthScale);

        newImgWidth = (int) (scale * (double) imgWidth);
        newImgHeight = (int) (scale * (double) imgHeight);

        return img.getScaledInstance(newImgWidth, newImgHeight, Image.SCALE_FAST);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(backgroundColor);
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        g.fillRect(0, 0, panelWidth, panelHeight);

        if (smallImage != null) {
            int x = (panelWidth - newImgWidth) / 2 + EMPTY_SPACE_AT_LEFT;
            int y = (panelHeight - newImgHeight) / 2;
            g.drawImage(smallImage, x, y, this);

            boolean doubleDrawMsg = y < MSG_STRING_Y - 10;

            String msg = "Size: " + imgWidth + " x " + imgHeight + " pixels";
            if (imgWidth == -1 || imgHeight == -1) {
                msg = "Unrecognized!";
                doubleDrawMsg = false;
            }

            g.setColor(Color.black);
            g.drawString(msg, MSG_STRING_X, MSG_STRING_Y);
            if (doubleDrawMsg) {
                g.setColor(Color.white);
                g.drawString(msg, MSG_STRING_X - 1, MSG_STRING_Y - 1);
            }

        }
    }

}

