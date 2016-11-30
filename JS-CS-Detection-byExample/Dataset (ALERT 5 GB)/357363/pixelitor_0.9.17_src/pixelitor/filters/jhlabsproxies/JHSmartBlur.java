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

import com.jhlabs.image.SmartBlurFilter;
import pixelitor.filters.OperationWithParametrizedGUI;
import pixelitor.filters.Operations;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Smart Blur based on the JHLabs SmartBlurFilter
 */
public class JHSmartBlur extends OperationWithParametrizedGUI {
    private RangeParam horizontalRadius = new RangeParam("Radius", 0, 100, 0);
    private RangeParam threshold = new RangeParam("Threshold", 0, 255, 50);
    private BooleanParam hpSharpening = BooleanParam.paramForHPSharpening();
    private BooleanParam showOriginalParam = BooleanParam.paramForShowOriginal();

    private SmartBlurFilter filter;

    public JHSmartBlur() {
        super("Smart Blur", false);
        paramSet = new ParamSet(
                horizontalRadius,
                threshold,
                hpSharpening,
                showOriginalParam
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int horizontalValue = horizontalRadius.getValue();
        if ((horizontalValue == 0) || showOriginalParam.getValue()) {
            return Operations.getDefaultBufferedImage(src);
        }

        if (filter == null) {
            filter = new SmartBlurFilter();
        }

        filter.setRadius(horizontalValue);
        filter.setThreshold(threshold.getValue());

        dest = filter.filter(src, dest);

        if (hpSharpening.getValue()) {
            dest = ImageUtils.getHighPassSharpenedImage(src, dest);
        }

        return dest;
    }
}