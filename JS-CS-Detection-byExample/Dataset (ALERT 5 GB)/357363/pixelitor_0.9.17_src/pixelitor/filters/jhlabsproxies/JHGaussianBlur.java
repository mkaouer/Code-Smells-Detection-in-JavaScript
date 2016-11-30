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
package pixelitor.filters.jhlabsproxies;


import com.jhlabs.image.GaussianFilter;
import pixelitor.filters.OperationWithParametrizedGUI;
import pixelitor.filters.Operations;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Gaussian Blur based on JHLabs GaussianFilter
 */
public class JHGaussianBlur extends OperationWithParametrizedGUI {
    private RangeParam radius = new RangeParam("Radius", 0, 100, 0);
    private BooleanParam hpSharpening = BooleanParam.paramForHPSharpening();
    private BooleanParam showOriginalParam = BooleanParam.paramForShowOriginal();

    private GaussianFilter filter;

    public JHGaussianBlur() {
        super("Gaussian Blur", false);
        paramSet = new ParamSet(
                radius,
                hpSharpening,
                showOriginalParam
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int radiusValue = radius.getValue();
        if ((radiusValue == 0) || showOriginalParam.getValue()) {
            return Operations.getDefaultBufferedImage(src);
        }

        if (filter == null) {
            filter = new GaussianFilter();
        }

        filter.setRadius(radiusValue);
        filter.setPremultiplyAlpha(false);

        dest = filter.filter(src, dest);

        if (hpSharpening.getValue()) {
            dest = ImageUtils.getHighPassSharpenedImage(src, dest);
        }

        return dest;
    }

    public void setRadius(int newRadius) {
        radius.setValue(newRadius);
    }
}