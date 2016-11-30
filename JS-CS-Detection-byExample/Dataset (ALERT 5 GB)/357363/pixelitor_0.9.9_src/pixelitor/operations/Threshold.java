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
import pixelitor.utils.SliderSpinner;

import java.awt.image.BufferedImage;

public class Threshold extends OperationWithParametrizedGUI {
    private RangeParam intParam = new RangeParam("Threshold", 0,
            255, 128, false, SliderSpinner.TextPosition.BORDER);


    public Threshold() {
        super("Threshold", true);
        paramSet = new ParamSet(intParam);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        RGBPixelOp pixelOp = new RGBPixelOp() {
            @Override
            public int changeRGB(int a, int r, int g, int b) {
                int luminosity = (int) (0.299 * r + 0.587 * g + 0.114 * b); // TODO can be faster with the luminosity lookup
                if (luminosity > intParam.getValue()) {
                    r = 255;
                    g = 255;
                    b = 255;
                } else {
                    r = 0;
                    g = 0;
                    b = 0;
                }

                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        };

        return Operations.runRGBPixelOp(pixelOp, src, dest);
    }
}
