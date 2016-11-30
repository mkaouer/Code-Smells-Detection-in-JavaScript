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
package pixelitor.tools.brushes;

import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 */
public class RealBrush implements Brush {
    private BufferedImage templateImage;
    private BufferedImage coloredBrushImage;
    private BufferedImage finalScaledImage;

    private Color lastColor;

    public RealBrush(ImageBrush imageBrush) {
        templateImage = imageBrush.createTemplateBrush(50);
    }

    /**
     * This method assumes that the color of coloredBrushImage is OK
     */
    private void resizeBrushImage(float newSize, boolean force) {
        if (!force) {
            if (finalScaledImage != null && finalScaledImage.getWidth() == newSize) {
                return;
            }
        }

        if (finalScaledImage != null) {
            finalScaledImage.flush();
        }

        int newSizeInt = (int) newSize;
        finalScaledImage = new BufferedImage(newSizeInt, newSizeInt, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalScaledImage.createGraphics();
        g.drawImage(coloredBrushImage, 0, 0, newSizeInt, newSizeInt, null);
        g.dispose();
    }

    @Override
    public void drawPoint(Graphics2D g, int x, int y, float diameter) {
        updateStateBeforeDrawing(g, diameter);
        drawPointNoCheck(g, x, y);
    }

    private void updateStateBeforeDrawing(Graphics2D g, float diameter) {
        Color c = g.getColor();

        if (!c.equals(lastColor)) {
            colorizeBrushImage(c);
            lastColor = c;
            resizeBrushImage(diameter, true);
        } else {
            resizeBrushImage(diameter, false);
        }
    }

    /**
     * For performance reasons, this is called form drawLine, not drawPoint - the check is not necessary for each step
     */
    private void drawPointNoCheck(Graphics2D g, int x, int y) {
        g.drawImage(finalScaledImage, x, y, null);
    }

    @Override
    public void drawLine(Graphics2D g, int startX, int startY, int endX, int endY, float diameter) {
        updateStateBeforeDrawing(g, diameter);

        double xDelta = endX - startX;
        double yDelta = endY - startY;
        double delta = Math.max(Math.abs(xDelta), Math.abs(yDelta));
        double xIncrement = xDelta / delta;
        double yIncrement = yDelta / delta;

        double x = startX;
        double y = startY;

        float halfSize = diameter / 2;

        for (int i = 0; i < delta; i++) {
            int interpolatedX = (int) (x - halfSize);
            int interpolatedY = (int) (y - halfSize);

            drawPointNoCheck(g, interpolatedX, interpolatedY);

            x += xIncrement;
            y += yIncrement;
        }
    }

    /**
     * Creates a colorized brush image from the template image according to the foreground color
     *
     * @param color
     */
    private void colorizeBrushImage(Color color) {
        coloredBrushImage = new BufferedImage(templateImage.getWidth(), templateImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int[] srcPixels = ImageUtils.getPixelsAsArray(templateImage);
        int[] destPixels = ImageUtils.getPixelsAsArray(coloredBrushImage);

        int destRed = color.getRed();
        int destGreen = color.getGreen();
        int destBlue = color.getBlue();
        for (int i = 0; i < destPixels.length; i++) {
            int srcRGB = srcPixels[i];

            //int a = (srcRGB >>> 24) & 0xFF;
            int srcRed = (srcRGB >>> 16) & 0xFF;
            int srcGreen = (srcRGB >>> 8) & 0xFF;
            int srcBlue = (srcRGB) & 0xFF;
            int srcAverage = (srcRed + srcGreen + srcBlue) / 3;

            destPixels[i] = (0xFF - srcAverage) << 24 | destRed << 16 | destGreen << 8 | destBlue;
        }
    }


}
