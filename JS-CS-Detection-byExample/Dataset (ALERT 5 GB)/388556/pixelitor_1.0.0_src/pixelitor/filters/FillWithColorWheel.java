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

import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class FillWithColorWheel extends FilterWithParametrizedGUI {
    private ImagePositionParam center = new ImagePositionParam("Center");
    private AngleParam hueShiftParam = new AngleParam("Hue Shift (degrees)", 0);
    private RangeParam brightnessParam = new RangeParam("Brightness (%)", 0, 100, 75);
    private RangeParam satParam = new RangeParam("Saturation (%)", 0, 100, 90);


    public FillWithColorWheel() {
        super("Fill with a Color Wheel", true);
        paramSet = new ParamSet(center, hueShiftParam, brightnessParam, satParam);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int[] destData = ImageUtils.getPixelsAsArray(dest);

        int width = dest.getWidth();
        int height = dest.getHeight();

        int cx = (int) (width * center.getRelativeX());
        int cy = (int) (height * center.getRelativeY());

        float hueShift = (float) hueShiftParam.getValueInRadians();
        float saturation = satParam.getValueAsPercentage();
        float brightness = brightnessParam.getValueAsPercentage();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double yDiff = (double) (cy - y);
                double xDiff = (double) x - cx;
                float angle = (float) (Math.atan2(yDiff, xDiff)) + hueShift;
                float hue = (float) (angle / (2 * Math.PI));

                destData[x + y * width] = Color.HSBtoRGB(hue, saturation, brightness);
            }
        }

        return dest;
    }
}
