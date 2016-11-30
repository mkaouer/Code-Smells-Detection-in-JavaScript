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

import pixelitor.operations.gui.BooleanParam;
import pixelitor.operations.gui.IntChoiceParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;
import pixelitor.tools.FgBgColorSelector;

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
    private static final int COLORS_SAMPLE_IMAGE = 1;
    private static final int COLORS_FG_BG = 2;

    private RangeParam radiusParam = new RangeParam("Radius", 2, 100, 10);

    private IntChoiceParam colorSourceParam = new IntChoiceParam("Colors Source", new IntChoiceParam.Value[]{
            new IntChoiceParam.Value("Sample Image", COLORS_SAMPLE_IMAGE),
            new IntChoiceParam.Value("Use FG, BG Colors", COLORS_FG_BG),
    });
    private BooleanParam skipAlphaCB = new BooleanParam("Nontransparent Areas Only", true);


    public RandomSpheres() {
        super("Random Spheres", true);
        paramSet = new ParamSet(
                radiusParam,
                colorSourceParam,
                skipAlphaCB);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        Graphics2D g2 = dest.createGraphics();
        int width = dest.getWidth();
        int height = dest.getHeight();
        int radius = radiusParam.getValue();

        Random rand = new Random();
        int numCircles = width * height / radius;

        int colorSource = colorSourceParam.getCurrentInt();
        Color[] colors = null;
        if (colorSource == COLORS_FG_BG) {
            colors = new Color[]{FgBgColorSelector.getFG(), FgBgColorSelector.getBG()};
        }

        boolean skipAlpha = skipAlphaCB.getValue();

        for (int i = 0; i < numCircles; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);

            int middleSrcColor = 0;
            if (colorSource == COLORS_SAMPLE_IMAGE || skipAlpha) {
                middleSrcColor = src.getRGB(x, y);
            }

            if (colorSource == COLORS_SAMPLE_IMAGE) {
                int alpha = (middleSrcColor >>> 24) & 0xFF;
                if (alpha == 0) {
                    continue; // if the image is sampled, the transparent areas are skipped anyway
                }
                Color c = new Color(middleSrcColor);
                colors = new Color[]{c.brighter(), c};
            } else if (colorSource == COLORS_FG_BG) {
                if (skipAlpha) {
                    int alpha = (middleSrcColor >>> 24) & 0xFF;
                    if (alpha == 0) {
                        continue;
                    }
                }
            }


            float[] fractions = {0.0f, 1.0f};
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

        g2.dispose();
        return dest;
    }
}