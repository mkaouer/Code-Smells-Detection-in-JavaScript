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

package pixelitor.operations;

import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Fills the image with random circles
 */
public class RandomSpheres extends OperationWithParametrizedGUI {
    private RangeParam radiusParam = new RangeParam("Radius", 2, 100, 10);

    public RandomSpheres() {
        super("Random Spheres", true);
        paramSet = new ParamSet(radiusParam);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        Graphics2D g2 = dest.createGraphics();
        int width = dest.getWidth();
        int height = dest.getHeight();
        int radius = radiusParam.getValue();

        Random rand = new Random();
        int numCircles = width * height / radius;

        for (int i = 0; i < numCircles; i++) {

            int x = rand.nextInt(width);
            int y = rand.nextInt(height);

            int middleSrcColor = src.getRGB(x, y);
            int alpha = (middleSrcColor >>> 24) & 0xFF;
            if (alpha == 0) {
                continue;
            }
            Color c = new Color(middleSrcColor);

            float[] fractions = {0.0f, 1.0f};
            Color[] colors = {c.brighter(), c};
            MultipleGradientPaint.CycleMethod cycleMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
            RadialGradientPaint gradientPaint = new RadialGradientPaint(x, y, radius, fractions, colors, cycleMethod);

            g2.setPaint(gradientPaint);

//            g2.setColor(c);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int drawX = x - radius;
            int drawY = y - radius;
            int diameter = 2 * radius;
            g2.fillOval(drawX, drawY, diameter, diameter);
        }

        return dest;
    }
}