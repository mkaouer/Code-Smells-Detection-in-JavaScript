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

import org.jdesktop.swingx.image.StackBlurFilter;
import pixelitor.ImageChangeReason;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.ParamSet;

import java.awt.image.BufferedImage;

/**
 * Stack Blur
 */
public class StackBlur extends OperationWithParametrizedGUI {
    private RangeParam radius = new RangeParam("Radius", 0, 100, 0);

    public StackBlur() {
        super("Stack Blur", false);
        paramSet = new ParamSet(radius);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        StackBlurFilter filter = new StackBlurFilter(radius.getValue());
        dest = filter.filter(src, dest);
        return dest;
    }
}