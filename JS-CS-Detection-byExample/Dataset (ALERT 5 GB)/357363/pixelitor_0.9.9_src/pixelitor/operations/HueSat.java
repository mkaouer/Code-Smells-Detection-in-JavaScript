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
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.lookup.LuminanceLookup;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HueSat extends OperationWithParametrizedGUI {
    private static final int MIN_HUE = -180;
    private static final int MAX_HUE = 180;
    private static final int DEFAULT_HUE = 0;

    private static final int MIN_SAT = -100;
    private static final int MAX_SAT = 100;
    private static final int DEFAULT_SAT = 0;

    private static final int MIN_BRI = -100;
    private static final int MAX_BRI = 100;
    private static final int DEFAULT_BRI = 0;

    private RangeParam hue = new RangeParam("Hue", MIN_HUE, MAX_HUE, DEFAULT_HUE);
    private RangeParam saturation = new RangeParam("Saturation", MIN_SAT, MAX_SAT, DEFAULT_SAT);
    private RangeParam brightness = new RangeParam("Brightness", MIN_BRI, MAX_BRI, DEFAULT_BRI);
    private BooleanParam colorizeCB = new BooleanParam("Colorize", false);

    public HueSat() {
        super("Hue/Saturation", false);
        paramSet = new ParamSet(
                hue,
                saturation,
                brightness,
                colorizeCB
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int[] srcData = ImageUtils.getPixelsAsArray(src);
        int[] destData = ImageUtils.getPixelsAsArray(dest);

        int length = srcData.length;
        if (length != destData.length) {
            throw new IllegalArgumentException("src and dest are not the same size");
        }

        float hueShift = hue.getValueAsPercentage() / 3.6f;
        float satShift = saturation.getValueAsPercentage();
        float briShift = brightness.getValueAsPercentage();
        boolean colorize = colorizeCB.getValue();

        if (colorize) {
            float saturation = (satShift + 1) / 2.0f; // convert from the -1,1 interval to the 0,1 interval
            float brightness = (briShift + 1) / 2.0f;
            Color colorizeColor = Color.getHSBColor(hueShift, saturation, brightness);

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

//                System.out.println("HueSat.transform destRed = " + destRed + ", destGreen = " + destGreen + ", destBlue = " + destBlue);

                destData[i] = a | (destRed << 16) | (destGreen << 8) | destBlue;
            }
        } else { // "colorized" is not checked, this is the normal hue, saturation, brightness shifting
            float[] tmpHSBArray = new float[]{0.0f, 0.0f, 0.0f};

            for (int i = 0; i < length; i++) {
                int rgb = srcData[i];
                int a = rgb & 0xFF000000;
                int r = (rgb >>> 16) & 0xFF;
                int g = (rgb >>> 8) & 0xFF;
                int b = (rgb) & 0xFF;

                tmpHSBArray = Color.RGBtoHSB(r, g, b, tmpHSBArray);

                float hue = tmpHSBArray[0] + hueShift;
                float sat = tmpHSBArray[1] + satShift;
                float bri = tmpHSBArray[2] + briShift;

                if (sat < 0.0f) {
                    sat = 0.0f;
                }
                if (sat > 1.0f) {
                    sat = 1.0f;
                }

                if (bri < 0.0f) {
                    bri = 0.0f;
                }
                if (bri > 1.0f) {
                    bri = 1.0f;
                }

                int newRGB = Color.HSBtoRGB(hue, sat, bri);  // alpha is 255 here
                newRGB &= 0x00FFFFFF;  // set alpha to 0
                destData[i] = a | newRGB; // add the real alpha
            }
        }

        return dest;
    }
}