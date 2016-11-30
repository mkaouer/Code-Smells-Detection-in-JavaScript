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

import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class Brightness extends FilterWithParametrizedGUI {
    private RangeParam power = new RangeParam("Power (%)", 50, 150, 100);
    private RangeParam multiply = new RangeParam("Multiply (%)", 1, 200, 100);
    private RangeParam add = new RangeParam("Add", -255, 255, 0);

    public Brightness() {
        super("Brightness", false);
        paramSet = new ParamSet(
                power,
                multiply,
                add
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int[] srcData = ImageUtils.getPixelsAsArray(src);
        int[] destData = ImageUtils.getPixelsAsArray(dest);

        int addValue = add.getValue();
        float multiplyValue = multiply.getValueAsPercentage();
        float powerValue = power.getValueAsPercentage();

        int[] lookup = new int[256];
        for (int i = 0; i < lookup.length; i++) {
            float lookupValue = i; // by default do nothing

            lookupValue = (float) Math.pow(lookupValue, powerValue);
            int lookupValueInt = ((int) (lookupValue * multiplyValue)) + addValue;
            lookup[i] = ImageUtils.limitTo8Bits(lookupValueInt);
        }

        for (int i = 0; i < destData.length; i++) {
            int rgb = srcData[i];

//            int a = (rgb >>> 24) & 0xFF;
            int a = rgb & 0xFF000000;
            int r = (rgb >>> 16) & 0xFF;
            int g = (rgb >>> 8) & 0xFF;
            int b = (rgb) & 0xFF;

            if (a == 0) {
                destData[i] = 0; // for premultiplied images
            } else {
                r = lookup[r];
                g = lookup[g];
                b = lookup[b];

                destData[i] = a | (r << 16) | (g << 8) | b;
            }
        }

        return dest;
    }
}