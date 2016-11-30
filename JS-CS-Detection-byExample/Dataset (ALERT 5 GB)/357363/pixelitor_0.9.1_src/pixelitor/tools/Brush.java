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
import pixelitor.operations.Fill;
import pixelitor.operations.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

/**
 *
 */
public class Brush {
    private BufferedImage templateImage;
    private BufferedImage coloredBrushImage;
    private BufferedImage finalScaledImage;
    private int size;

    public Brush(final RangeParam radiusParam) {
//        templateImage = ImageUtils.loadBufferedImage("brush.png");
        templateImage = createBrush(25, 0.2);

        colorizeBrushImage();
        FgBgColorSelector.INSTANCE.addPropertyChangeListener("FG", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                colorizeBrushImage();
                setSize(2* radiusParam.getValue());
            }
        });

        setSize(2* radiusParam.getValue());
        radiusParam.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setSize(2 * radiusParam.getValue());
            }
        });
    }

    public void setSize(int newSize) {
        if (coloredBrushImage.getWidth() == newSize) {
            finalScaledImage = coloredBrushImage;
        }
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
            int srcAverage = (srcRed + srcGreen + srcBlue) / 3;

            destPixels[i] = (0xFF - srcAverage) << 24 | red << 16 | green << 8 | blue;
        }
    }


    public void drawTo(Graphics2D g, int x, int y) {

        g.drawImage(finalScaledImage, x, y, null);
    }

    public void drawLine(Graphics2D g, int startX, int startY, int endX, int endY) {
        double xDelta = endX - startX;
        double yDelta = endY - startY;
        double delta = Math.max(Math.abs(xDelta), Math.abs(yDelta));
        double xIncrement = xDelta / delta;
        double yIncrement = yDelta / delta;

        double x = startX;
        double y = startY;

        int halfSize = size / 2;

        for (int i = 0; i < delta; i++) {
            int interpolatedX = (int) x;
            int interpolatedY = (int) y;

            drawTo(g, interpolatedX - halfSize, interpolatedY - halfSize);

            x += xIncrement;
            y += yIncrement;
        }
    }

    public static BufferedImage createBrush(int radius, double density) {
        if (density < 0.0 && density > 1.0) {
            throw new IllegalArgumentException("density is " + density);
        }

        int size = 2 * radius;
        BufferedImage brushImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        int radius2 = radius * radius;
        Random random = new Random();

        int[] pixels = ImageUtils.getPixelsAsArray(brushImage);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int dx = x - radius;
                int dy = y - radius;
                int centerDistance2 = dx * dx + dy * dy;
                if (centerDistance2 < radius2) {
                    if (density > Math.random()) {
                        pixels[x + y * size] = random.nextInt();
                    } else {
                        pixels[x + y * size] = 0xFFFFFFFF;
                    }
                } else {
                    pixels[x + y * size] = 0xFFFFFFFF;
                }
            }
        }

//        Fill.fillImage(brushImage, Color.BLACK);

        return brushImage;
    }
}
