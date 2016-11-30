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
package pixelitor.operations.jhlabsproxies;


import com.jhlabs.image.GaussianFilter;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.Operations;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Gaussian Blur based on JHLabs GaussianFilter
 */
public class JHGaussianBlur extends OperationWithParametrizedGUI {
    private RangeParam radius = new RangeParam("Radius", 0, 100, 0);

    private GaussianFilter filter;

    public JHGaussianBlur() {
        super("Gaussian Blur", false);
        paramSet = new ParamSet(radius);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int radiusValue = radius.getValue();
        if (radiusValue == 0) {
            return Operations.getDefaultBufferedImage(src);
        }

        if (filter == null) {
            filter = new GaussianFilter();
        }

        filter.setRadius(radiusValue);
        filter.setPremultiplyAlpha(false);


        dest = filter.filter(src, dest);
        return dest;
    }
}