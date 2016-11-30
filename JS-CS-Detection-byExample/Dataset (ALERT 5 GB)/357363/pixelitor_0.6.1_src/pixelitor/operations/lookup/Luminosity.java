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
package pixelitor.operations.lookup;

import pixelitor.ImageChangeReason;
import pixelitor.operations.Operation;
import pixelitor.utils.ImageUtils;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 *
 */
public class Luminosity extends Operation {

    public Luminosity() {
        super("Luminosity");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        int[] srcData = ImageUtils.getPixelsAsArray(src);
        int[] destData = ImageUtils.getPixelsAsArray(dest);

        for (int i = 0; i < destData.length; i++) {
            int rgb = srcData[i];
            int alpha = rgb & 0xFF000000;
            if(alpha == 0) {
                destData[i] = 0;
            } else {
                int lum = LuminanceLookup.getLuminosity(rgb);
                destData[i] = alpha | (lum << 16) | (lum << 8) | lum;
            }
        }

        return dest;
    }
}