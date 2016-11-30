/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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

import com.jhlabs.image.GlowFilter;
import pixelitor.filters.FilterUtils;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.FilterWithSOParametrizedGUI;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Glow based on the JHLabs GlowFilter
 */
public class JHGlow extends FilterWithSOParametrizedGUI {
    private RangeParam amount = new RangeParam("Amount", 0, 100, 0);
    private RangeParam softness = new RangeParam("Softness", 0, 100, 20);

    private GlowFilter filter;

    public JHGlow() {
        super("Glow");
        paramSet = new ParamSet(
                amount,
                softness,
                showOriginalParam
        );
    }

    @Override
    public BufferedImage realTransform(BufferedImage src, BufferedImage dest) {
        float amountValue = amount.getValueAsPercentage();
        if (amountValue == 0.0f) {
            return FilterUtils.getDefaultImage(src);
        }

        if (filter == null) {
            filter = new GlowFilter();
        }

        filter.setAmount(amountValue);
        filter.setRadius(softness.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }

}