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
import org.jdesktop.swingx.image.StackBlurFilter;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

/**
 * Fast Blur
 */
public class FastBlur extends FilterWithSOParametrizedGUI {
    private RangeParam radiusParam = new RangeParam("Radius", 0, 100, 0);
    private BooleanParam hpSharpening = BooleanParam.createParamForHPSharpening();

    private static final int METHOD_BETTER = 1;
    private static final int METHOD_FASTER = 2;

    private IntChoiceParam qualityParam = new IntChoiceParam("Quality", new IntChoiceParam.Value[] {
            new IntChoiceParam.Value("Better", METHOD_BETTER),
            new IntChoiceParam.Value("Faster", METHOD_FASTER),
    });

    public FastBlur() {
        super("Fast Blur");
        paramSet = new ParamSet(
                radiusParam,
                qualityParam,
                hpSharpening,
                showOriginalParam);
    }

    @Override
    public BufferedImage realTransform(BufferedImage src, BufferedImage dest) {
        int radius = radiusParam.getValue();
        if ((radius == 0)) {
            return FilterUtils.getDefaultImage(src);
        }

        BufferedImageOp filter = null;
        int method = qualityParam.getValue();
        if(method == METHOD_BETTER) {
            filter = new StackBlurFilter(radiusParam.getValue());
        } else if(method == METHOD_FASTER) {
            filter = new FastBlurFilter(radius);
        } else {
            throw new IllegalStateException("method = " + method);
        }

        dest = filter.filter(src, dest);

        if (hpSharpening.getValue()) {
            dest = ImageUtils.getHighPassSharpenedImage(src, dest);
        }

        return dest;
    }

}