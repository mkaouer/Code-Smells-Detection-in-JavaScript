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
package pixelitor.operations.jhlabsproxies;

import com.jhlabs.image.BoxBlurFilter;
import pixelitor.ImageChangeReason;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.Operations;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.ParamSet;

import java.awt.image.BufferedImage;

/**
 * JH Box Blur based on the JHLabs BoxBlurFilter
 */
public class JHBoxBlur extends OperationWithParametrizedGUI {
    private RangeParam horizontalRadius = new RangeParam("Horizontal Radius", 0, 100, 0);
    private RangeParam verticalRadius = new RangeParam("Vertical Radius", 0, 100, 0);
    private RangeParam numberOfIterations = new RangeParam("Number of Iterations", 0, 10, 3);
    private BoxBlurFilter filter;

    public JHBoxBlur() {
        super("Box Blur", false);
        paramSet = new ParamSet(
                horizontalRadius,
                verticalRadius,
                numberOfIterations
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        int hRadius = horizontalRadius.getValue();
        int vRadius = verticalRadius.getValue();

        if((hRadius == 0) && (vRadius == 0)) {
            return Operations.getDefaultBufferedImage(src);
        }

        if (filter == null) {
            filter = new BoxBlurFilter();
        }

        filter.setHRadius(hRadius);
        filter.setVRadius(vRadius);
        filter.setIterations(numberOfIterations.getValue());
        filter.setPremultiplyAlpha(false);

        dest = filter.filter(src, dest);
        return dest;
    }
}