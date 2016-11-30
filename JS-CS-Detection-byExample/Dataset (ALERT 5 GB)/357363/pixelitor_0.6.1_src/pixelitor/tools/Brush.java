/*
 * Copyright 2010 László Balázs-Csíki
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
package pixelitor.tools;

import pixelitor.FgBgColorSelector;
import pixelitor.operations.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 */
public class Brush {
    private BufferedImage templateImage;
    private BufferedImage coloredBrushImage;
    private BufferedImage finalScaledImage;
    private int size;

    public Brush(final RangeParam sizeParam) {
        templateImage = ImageUtils.loadBufferedImage("brush.png");
        colorizeBrushImage();
        FgBgColorSelector.INSTANCE.addPropertyChangeListener("FG", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                colorizeBrushImage();
                setSize(sizeParam.getValue());
            }
        });

        setSize(sizeParam.getValue());
        sizeParam.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setSize(sizeParam.getValue());
            }
        });
    }

    public void setSize(int newSize) {
        size = newSize;
        finalScaledImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalScaledImage.createGraphics();
        g.drawImage(coloredBrushImage, 0, 0, size, size, null);
        g.dispose();
    }


    private void colorizeBrushImage() {
        coloredBrushImage = new BufferedImage(templateImage.getWidth(), templateImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int[] srcPixels = ImageUtils.getPixelsAsArray(templateImage);
        int[] destPixels = ImageUtils.getPixelsAsArray(coloredBrushImage);

        Color color = FgBgColorSelector.getFG();

        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        for (int i = 0; i < destPixels.length; i++) {
            int srcRGB = srcPixels[i];

            //int a = (srcRGB >>> 24) & 0xFF;
            int srcRed = (srcRGB >>> 16) & 0xFF;
            int srcGreen = (srcRGB >>> 8) & 0xFF;
            int srcBlue = (srcRGB) & 0xFF;
            int srcAverage = (srcRed + srcGreen + srcBlue)/3;

            destPixels[i] = (0xFF - srcAverage) << 24 |  red << 16 | green << 8 | blue;
        }
    }


    public void drawTo(Graphics2D g, int x, int y) {
        int halfSize = size / 2;
        g.drawImage(finalScaledImage, x - halfSize, y - halfSize, null);
    }

    public void drawLine(Graphics2D g, int startX, int startY, int endX, int endY) {
        double xDelta = endX - startX;
        double yDelta = endY - startY;
        double delta = Math.max(Math.abs(xDelta), Math.abs(yDelta));
        double xIncrement = xDelta / delta;
        double yIncrement = yDelta / delta;

        double x = startX;
        double y = startY;

        for (int i = 0; i < delta; i++) {
            int interpolatedX = (int) x;
            int interpolatedY = (int) y;

            drawTo(g, interpolatedX, interpolatedY);

            x += xIncrement;
            y += yIncrement;
        }
    }
}
