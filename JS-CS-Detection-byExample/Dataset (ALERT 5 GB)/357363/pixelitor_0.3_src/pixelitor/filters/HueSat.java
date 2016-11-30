/*
 * Copyright 2009 László Balázs-Csíki
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

import java.awt.Color;
import java.awt.image.BufferedImage;

import pixelitor.ImageChangeReason;
import pixelitor.utils.SliderSpinner;
import pixelitor.utils.Utils;

public class HueSat extends AbstractOperationWithDialog {
    private static final int MIN_HUE = -180;
    private static final int MAX_HUE = 180;
    private static final int DEFAULT_HUE = 0;

    private static final int MIN_SAT = -100;
    private static final int MAX_SAT = 100;
    private static final int DEFAULT_SAT = 0;

    private static final int MIN_BRI = -100;
    private static final int MAX_BRI = 100;
    private static final int DEFAULT_BRI = 0;

    private LinearIntParam[] params = new LinearIntParam[]{
            new LinearIntParam("Hue", MIN_HUE, MAX_HUE, DEFAULT_HUE),
            new LinearIntParam("Saturation", MIN_SAT, MAX_SAT, DEFAULT_SAT),
            new LinearIntParam("Brightness", MIN_BRI, MAX_BRI, DEFAULT_BRI)
    };
    private ParamSet paramSet = new ParamSet(params);

    public HueSat() {
        super("Hue/Saturation");
    }

    @Override
    public ParamSet getParams() {
        return paramSet;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        int[] srcData = Utils.getPixelsAsArray(src);
        int[] destData = Utils.getPixelsAsArray(dest);

        int length = srcData.length;
        if (length != destData.length) {
            throw new IllegalArgumentException("src and dest are not the same size");
        }

        float hueShift = params[0].getValueAsFloat() / 3.6f;
        float satShift = params[1].getValueAsFloat();
        float briShift = params[2].getValueAsFloat();

        // preallocated, so that an array allocation is not necessary for every pixel.
        float[] tmpHSBArray = new float[]{0f, 0f, 0f};

        for (int i = 0; i < length; i++) {
            int rgb = srcData[i];
//                int a = (rgb >>> 24) & 0xFF;
            int r = (rgb >>> 16) & 0xFF;
            int g = (rgb >>> 8) & 0xFF;
            int b = (rgb) & 0xFF;

            tmpHSBArray = Color.RGBtoHSB(r, g, b, tmpHSBArray);

            float hue = tmpHSBArray[0] + hueShift;
            float sat = tmpHSBArray[1] + satShift;
            float bri = tmpHSBArray[2] + briShift;

            if (sat < 0f) {
                sat = 0f;
            }
            if (sat > 1f) {
                sat = 1f;
            }

            if (bri < 0f) {
                bri = 0f;
            }
            if (bri > 1f) {
                bri = 1f;
            }

            destData[i] = Color.HSBtoRGB(hue, sat, bri);
        }

        return dest;
    }

    @Override
    public AdjustPanel getAdjustPanel() {
        if(adjustPanel == null) {
            adjustPanel =  new ParametrizedAdjustments(this, true, SliderSpinner.TextPosition.BORDER, false);
        } else {
            adjustPanel.setRunFiltersIfStateChanged(false);
            paramSet.reset();
            adjustPanel.setRunFiltersIfStateChanged(true);
        }

        return adjustPanel;
    }
}