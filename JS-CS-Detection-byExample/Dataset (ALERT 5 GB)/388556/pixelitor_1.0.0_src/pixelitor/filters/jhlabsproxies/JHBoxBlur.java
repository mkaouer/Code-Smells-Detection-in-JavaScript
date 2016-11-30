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
package pixelitor.filters.jhlabsproxies;

import com.jhlabs.image.BoxBlurFilter;
import pixelitor.filters.FilterUtils;
import pixelitor.filters.FilterWithSOParametrizedGUI;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * JH Box Blur based on the JHLabs BoxBlurFilter
 */
public class JHBoxBlur extends FilterWithSOParametrizedGUI {
    private RangeParam horizontalRadius = new RangeParam("Horizontal Radius", 0, 100, 0);
    private RangeParam verticalRadius = new RangeParam("Vertical Radius", 0, 100, 0);
    private RangeParam numberOfIterations = new RangeParam("Number of Iterations", 1, 10, 3);

    private BooleanParam hpSharpening = BooleanParam.createParamForHPSharpening();

    private BoxBlurFilter filter;

    public JHBoxBlur() {
        super("Box Blur", false);
        paramSet = new ParamSet(
                horizontalRadius,
                verticalRadius,
                numberOfIterations,
                hpSharpening,
                showOriginalParam
        );
    }

    @Override
    public BufferedImage realTransform(BufferedImage src, BufferedImage dest) {
        int hRadius = horizontalRadius.getValue();
        int vRadius = verticalRadius.getValue();
        if ((hRadius == 0) && (vRadius == 0)) {
            return FilterUtils.getDefaultImage(src);
        }

        if (filter == null) {
            filter = new BoxBlurFilter();
        }

        filter.setHRadius(hRadius);
        filter.setVRadius(vRadius);
        filter.setIterations(numberOfIterations.getValue());
        filter.setPremultiplyAlpha(false);

        dest = filter.filter(src, dest);

        if (hpSharpening.getValue()) {
            dest = ImageUtils.getHighPassSharpenedImage(src, dest);
        }

        return dest;
    }
}