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
package pixelitor.filters;

import pixelitor.filters.gui.ColorParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.lookup.LuminanceLookup;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Colorize
 */
public class Colorize extends FilterWithParametrizedGUI {
    private RangeParam adjustBrightnessParam = new RangeParam("Adjust Brightness", -100, 100, 0);
    private ColorParam colorParam = new ColorParam("Color:", new Color(255, 207, 119), false, false);

    public Colorize() {
        super("Colorize");
        paramSet = new ParamSet(
                colorParam,
                adjustBrightnessParam
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {

        float briShift = adjustBrightnessParam.getValueAsPercentage();

        Color colorizeColor = colorParam.getColor();

        return colorize(src, dest, colorizeColor, briShift);
    }

    public static BufferedImage colorize(BufferedImage src, BufferedImage dest, Color colorizeColor, float briShift) {
        int[] srcData = ImageUtils.getPixelsAsArray(src);
        int[] destData = ImageUtils.getPixelsAsArray(dest);


        int colorizeR = colorizeColor.getRed();
        int colorizeG = colorizeColor.getGreen();
        int colorizeB = colorizeColor.getBlue();

        // The final R,G,B values depend on the colorize R,G,B values and on the luminosity of the source pixels.
        // For performance reasons the luminosity will be the index in these lookup tables
        int[] redLookup = new int[256];
        int[] greenLookup = new int[256];
        int[] blueLookup = new int[256];
        for (int i = 0; i < 256; i++) {
            redLookup[i] = (i * colorizeR) / 255;
            greenLookup[i] = (i * colorizeG) / 255;
            blueLookup[i] = (i * colorizeB) / 255;
        }

        int length = srcData.length;

        for (int i = 0; i < length; i++) {
            int rgb = srcData[i];
            int a = rgb & 0xFF000000;
            int lum = LuminanceLookup.getLuminosity(rgb);
            if (briShift > 0) {
                lum = (int) ((float) lum * (1.0f - briShift));
                lum += 255 - (1.0f - briShift) * 255.0f;
            } else if (briShift < 0) {
                lum = (int) ((float) lum * (briShift + 1.0f));
            }

            int destRed = redLookup[lum];
            int destGreen = greenLookup[lum];
            int destBlue = blueLookup[lum];

            destData[i] = a | (destRed << 16) | (destGreen << 8) | destBlue;
        }

        return dest;
    }
}