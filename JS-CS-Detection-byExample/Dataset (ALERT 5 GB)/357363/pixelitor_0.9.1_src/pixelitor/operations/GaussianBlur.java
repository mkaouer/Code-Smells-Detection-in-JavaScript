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

import org.jdesktop.swingx.image.GaussianBlurFilter;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Gaussian Blur
 */
public class GaussianBlur extends OperationWithParametrizedGUI {
    private RangeParam radius = new RangeParam("Radius", 0, 100, 0);

    public GaussianBlur() {
        super("Gaussian Blur", false);
        paramSet = new ParamSet(radius);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        GaussianBlurFilter filter = new GaussianBlurFilter(radius.getValue());
        dest = filter.filter(src, dest);
        return dest;
    }
}
