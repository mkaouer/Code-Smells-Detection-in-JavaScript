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

import com.jhlabs.image.OilFilter;
import pixelitor.ImageChangeReason;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.Operations;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Dry Brush based on the JHLabs OilFilter
 */
public class JHDryBrush extends OperationWithParametrizedGUI {
    private RangeParam brushSize = new RangeParam("Brush Size", 0, 5, 0);
    private RangeParam numberOfLevels = new RangeParam("Coarseness", 1, 255, 128);

    private OilFilter filter;

    public JHDryBrush() {
        super("Dry Brush", false);
        paramSet = new ParamSet(
                brushSize,
                numberOfLevels
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        int range = brushSize.getValue();
        if (range == 0) {
            return Operations.getDefaultBufferedImage(src);
        }

        if (filter == null) {
            filter = new OilFilter();
        }

        filter.setRange(range);
        filter.setLevels(numberOfLevels.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}