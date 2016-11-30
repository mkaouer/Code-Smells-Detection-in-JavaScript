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

public class FillWithColorWheel extends AbstractOperationWithDialog {
    private LinearIntParam hueShiftParam = new LinearIntParam("hue shift (degrees, clockwise)", 0, 360, 0);
    private LinearIntParam brightnessParam = new LinearIntParam("brightness (%)", 0, 100, 75);
    private LinearIntParam satParam = new LinearIntParam("saturation (%)", 0, 100, 90);
    private ParamSet paramSet = new ParamSet(new LinearIntParam[] { hueShiftParam, brightnessParam, satParam });

    public FillWithColorWheel() {
        super("Fill with a Color Wheel");
    }


    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        int[] destData = Utils.getPixelsAsArray(dest);

        int width = dest.getWidth();
        int height = dest.getHeight();

        int cx = width / 2;
        int cy = height / 2;

//            double maxDistance = Math.sqrt(cx*cx + cy*cy);

        float hueShift = hueShiftParam.getValueInRadians();
        float saturation = satParam.getValueAsFloat();
        float brightness = brightnessParam.getValueAsFloat();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double ydiff = (double) (cy - y);
                double xdiff = (double) x - cx;
                float angle = (float) (Math.atan2(ydiff, xdiff)) + hueShift;

                float hue = (float) (angle / (2 * Math.PI));

//                    double distance =   Math.sqrt((x-cx)*(x-cx) + (y-cy)*(y-cy));
//                    float saturation = 1 - (float) (distance/maxDistance);

                destData[x + y * width] = Color.HSBtoRGB(hue, saturation, brightness);
            }
        }

        return dest;
    }

    @Override
    public AdjustPanel getAdjustPanel() {
        if(adjustPanel == null) {
            adjustPanel =  new ParametrizedAdjustments(this, true, SliderSpinner.TextPosition.BORDER, true);
        } else {
            adjustPanel.setRunFiltersIfStateChanged(false);
            paramSet.reset();
            adjustPanel.setRunFiltersIfStateChanged(true);
        }

        return adjustPanel;

    }

    @Override
    public ParamSet getParams() {
        return paramSet;
    }
}
