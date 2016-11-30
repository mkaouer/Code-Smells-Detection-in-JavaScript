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

import org.jdesktop.swingx.image.FastBlurFilter;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Fast Blur
 */
public class FastBlur extends FilterWithSOParametrizedGUI {
    private RangeParam radiusParam = new RangeParam("Radius", 0, 100, 0);
    private BooleanParam hpSharpening = BooleanParam.createParamForHPSharpening();

    public FastBlur() {
        super("Fast Blur", false);
        paramSet = new ParamSet(
                radiusParam,
                hpSharpening,
                showOriginalParam);
    }

    @Override
    public BufferedImage realTransform(BufferedImage src, BufferedImage dest) {
        int radius = radiusParam.getValue();
        if ((radius == 0)) {
            return FilterUtils.getDefaultImage(src);
        }

        FastBlurFilter filter = new FastBlurFilter(radius);
        dest = filter.filter(src, dest);

        if (hpSharpening.getValue()) {
            dest = ImageUtils.getHighPassSharpenedImage(src, dest);
        }

        return dest;
    }

}