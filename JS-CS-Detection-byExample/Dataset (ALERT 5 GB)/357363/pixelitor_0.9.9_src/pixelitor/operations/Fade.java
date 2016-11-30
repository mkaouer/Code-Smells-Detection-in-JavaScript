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

import pixelitor.history.History;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class Fade extends OperationWithParametrizedGUI {
    private static final int FADE_MIN = 0;
    private static final int FADE_MAX = 100;
    private static final int FADE_INIT = 100;

    private RangeParam opacityParam = new RangeParam("Opacity", FADE_MIN, FADE_MAX,
            FADE_INIT);


    public Fade() {
        super("Fade", false);
        paramSet = new ParamSet(opacityParam);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        BufferedImage previous = History.getPreviousImageForFade();
        if (previous == null) {
            // TODO an exception would be be better - but the testing methods should be adopted
            return Operations.getDefaultBufferedImage(src);
        }

        int[] srcData = ImageUtils.getPixelsAsArray(src);
        int[] destData = ImageUtils.getPixelsAsArray(dest);
        int[] prevData = ImageUtils.getPixelsAsArray(previous);

        int length = srcData.length;
        if (length != prevData.length) {
            throw new IllegalArgumentException("the image and the previous are not the same size");
        }
        for (int i = 0; i < length; i++) {
            int rgb = srcData[i];
            int a = (rgb >>> 24) & 0xFF;
            int r = (rgb >>> 16) & 0xFF;
            int g = (rgb >>> 8) & 0xFF;
            int b = (rgb) & 0xFF;

            int prev_a = (prevData[i] >>> 24) & 0xFF;
            int prev_r = (prevData[i] >>> 16) & 0xFF;
            int prev_g = (prevData[i] >>> 8) & 0xFF;
            int prev_b = (prevData[i]) & 0xFF;

            float fadeFactor = opacityParam.getValueAsPercentage();

            a = (int) (prev_a * (1.0 - fadeFactor) + a * fadeFactor);
            r = (int) (prev_r * (1.0 - fadeFactor) + r * fadeFactor);
            g = (int) (prev_g * (1.0 - fadeFactor) + g * fadeFactor);
            b = (int) (prev_b * (1.0 - fadeFactor) + b * fadeFactor);

            rgb = (a << 24) | (r << 16) | (g << 8) | b;
            destData[i] = rgb;
        }

        return dest;
    }

    public void setOpacity(int newOpacity) {
        opacityParam.setValue(newOpacity);
    }
}
