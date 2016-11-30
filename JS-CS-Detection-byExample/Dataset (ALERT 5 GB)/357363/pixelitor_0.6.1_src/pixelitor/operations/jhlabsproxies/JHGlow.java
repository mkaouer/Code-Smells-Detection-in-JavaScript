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

import com.jhlabs.image.GlowFilter;
import pixelitor.ImageChangeReason;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.Operations;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.ParamSet;

import java.awt.image.BufferedImage;

/**
 * Glow based on the JHLabs GlowFilter
 */
public class JHGlow extends OperationWithParametrizedGUI {
    private RangeParam amount = new RangeParam("Amount", 0, 100, 0);
    private RangeParam softness = new RangeParam("Softness", 0, 100, 20);

    private GlowFilter filter;

    public JHGlow() {
        super("Glow", false);
        paramSet = new ParamSet(
                amount,
                softness
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        float amountValue = amount.getValueAsPercentage();
        if(amountValue == 0f) {
            return Operations.getDefaultBufferedImage(src);
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