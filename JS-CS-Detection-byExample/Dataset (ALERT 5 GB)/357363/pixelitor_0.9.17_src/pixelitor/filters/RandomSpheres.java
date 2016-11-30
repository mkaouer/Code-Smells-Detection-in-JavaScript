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

package pixelitor.filters;

import pixelitor.filters.gui.ActionParam;
import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ElevationAngleParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.tools.FgBgColorSelector;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Fills the image with random circles
 */
public class RandomSpheres extends OperationWithParametrizedGUI {
    private static final double INTUITIVE_RADIANS_45 = 5.497787143782138;

    private static final int COLORS_SAMPLE_IMAGE = 1;
    private static final int COLORS_FG_BG = 2;

    private RangeParam radiusParam = new RangeParam("Radius", 2, 100, 10);
    private RangeParam densityParam = new RangeParam("Density (%)", 1, 200, 50);

    private IntChoiceParam colorSourceParam = new IntChoiceParam("Colors Source", new IntChoiceParam.Value[]{
            new IntChoiceParam.Value("Sample Image", COLORS_SAMPLE_IMAGE),
            new IntChoiceParam.Value("Use FG, BG Colors", COLORS_FG_BG),
    });
    private BooleanParam addHighLightsCB = new BooleanParam("Add Highlights", true);
    private AngleParam highlightAngleSelector = new AngleParam("Light Direction (Azimuth) - Degrees", 0);

    private ElevationAngleParam highlightElevationSelector = new ElevationAngleParam("Highlight Elevation (Degrees)", INTUITIVE_RADIANS_45);

    private RangeParam opacityParam = new RangeParam("Opacity", 0, 100, 100);
    private Random rand;
    private long seed;


    @SuppressWarnings({"FieldCanBeLocal"})
    private ActionParam reseedAction = new ActionParam("Reseed", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            reseed();
        }
    });


    public RandomSpheres() {
        super("Random Spheres", true);
        paramSet = new ParamSet(
                radiusParam,
                densityParam,
                opacityParam,
                colorSourceParam,
                addHighLightsCB,
                highlightAngleSelector,
                highlightElevationSelector,
                reseedAction
        );

        seed = System.nanoTime();
        rand = new Random(seed);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        Graphics2D g2 = dest.createGraphics();
        rand.setSeed(seed);

        int width = dest.getWidth();
        int height = dest.getHeight();

        int radius = radiusParam.getValue();
        double angle = highlightAngleSelector.getValueInRadians();
        angle += Math.PI;

        double elevation = highlightElevationSelector.getValueInRadians();

        int centerShiftX = (int) (radius * Math.cos(angle) * Math.cos(elevation));
        int centerShiftY = (int) (radius * Math.sin(angle) * Math.cos(elevation));

        boolean addHighlights = addHighLightsCB.getValue();
        float density = densityParam.getValueAsPercentage();
        float opacity = opacityParam.getValueAsPercentage();

        g2.setComposite(AlphaComposite.SrcOver.derive(opacity));

        int colorSource = colorSourceParam.getValue();

        int numCircles = (int) (width * height * density / (radius * radius));

        Color[] colors = null;
        Color c = null;

        if (colorSource == COLORS_FG_BG) {
            colors = new Color[]{FgBgColorSelector.getFG(), FgBgColorSelector.getBG()};
            c = colors[0];
        }

        for (int i = 0; i < numCircles; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);

            int srcColor = src.getRGB(x, y);
            int alpha = (srcColor >>> 24) & 0xFF;
            if (alpha == 0) {
                continue;
            }

            if (colorSource == COLORS_SAMPLE_IMAGE) {
                c = new Color(srcColor);
                if (addHighlights) {
                    colors = new Color[]{c.brighter().brighter(), c};
                }
            }

            if (addHighlights) {
                float[] fractions = {0.0f, 1.0f};
                MultipleGradientPaint.CycleMethod cycleMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
                RadialGradientPaint gradientPaint = new RadialGradientPaint(x + centerShiftX, y + centerShiftY, radius, fractions, colors, cycleMethod);

                g2.setPaint(gradientPaint);
            } else {
                g2.setColor(c);
            }

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

    private void reseed() {
        seed = System.nanoTime();
    }
}