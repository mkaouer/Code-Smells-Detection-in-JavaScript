/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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

import com.jhlabs.image.ImageMath;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Random;

public class AddNoise extends OperationWithParametrizedGUI {
    private RangeParam opacity = new RangeParam("Opacity", 0, 100, 100);
    private RangeParam coverage = new RangeParam("Coverage", 0, 100, 50);

    public AddNoise() {
        super("Add Noise", true);
        paramSet = new ParamSet(
                opacity,
                coverage
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int[] srcData = ImageUtils.getPixelsAsArray(src);
        int[] destData = ImageUtils.getPixelsAsArray(dest);

        float opacityValue = opacity.getValueAsPercentage();
        float coverageValue = coverage.getValueAsPercentage();

        Random random = new Random();
        for (int i = 0; i < destData.length; i++) {
            int srcRGB = srcData[i];


            float rn = random.nextFloat();  // faster than Math.random()
            if (rn > coverageValue) {
                destData[i] = srcRGB;
                continue;
            }

            int sourceAlpha = 0xFF000000 & srcRGB;
            if (sourceAlpha == 0) { // for premultiplied
                destData[i] = 0;
                continue;
            }

            int randomInt = random.nextInt();

            // alpha channel should be the same as for the source
            randomInt |= sourceAlpha;

            destData[i] = ImageMath.mixColors(opacityValue, srcRGB, randomInt);
        }

        return dest;
    }
}
