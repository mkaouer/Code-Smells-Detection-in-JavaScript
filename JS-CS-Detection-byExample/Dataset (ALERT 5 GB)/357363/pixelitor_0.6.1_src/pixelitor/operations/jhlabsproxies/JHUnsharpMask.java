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

import com.jhlabs.image.UnsharpFilter;
import pixelitor.ImageChangeReason;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.ParamSet;

import java.awt.image.BufferedImage;

/**
 * Unsharp Mask based on the JHLabs UnsharpFilter
 */
public class JHUnsharpMask extends OperationWithParametrizedGUI {
    private RangeParam amount = new RangeParam("Amount", 1, 100, 1);
    private RangeParam radius = new RangeParam("Radius", 0, 100, 0);
    private RangeParam threshold = new RangeParam("Threshold", 0, 100, 0);

    UnsharpFilter filter;

    public JHUnsharpMask() {
        super("Unsharp Mask", true);
        paramSet = new ParamSet(
                amount,
                radius,
                threshold
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        if (filter == null) {
            filter = new UnsharpFilter();
        }

        filter.setAmount(amount.getValueAsPercentage());
        filter.setThreshold(threshold.getValue());
        filter.setRadius(radius.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}