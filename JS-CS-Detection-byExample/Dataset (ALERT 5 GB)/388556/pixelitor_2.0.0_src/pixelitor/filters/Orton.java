/*
 * Copyright 2010 Laszlo Balazs-Csiki
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

import com.jhlabs.composite.MultiplyComposite;
import com.jhlabs.image.BoxBlurFilter;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Orton effect - based on http://pcin.net/update/2006/11/01/the-orton-effect-digital-photography-tip-of-the-week/
 */
public class Orton extends FilterWithParametrizedGUI {
    private final RangeParam blurRadiusParam = new RangeParam("Blur Radius", 0, 10, 3);
    private final RangeParam amountParam = new RangeParam("Amount (%)", 0, 100, 100);

    public Orton() {
        super("Orton Effect", true, false);
        setParamSet(new ParamSet(
                blurRadiusParam,
                amountParam
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        int blurRadius = blurRadiusParam.getValue();
        float opacity = amountParam.getValueAsPercentage();

        dest = ImageUtils.copyImage(src);
        ImageUtils.screenWithItself(dest, opacity);

        BufferedImage blurredMultiplied = ImageUtils.copyImage(dest);

        if (blurRadius > 0) {
            BoxBlurFilter boxBlur = new BoxBlurFilter(blurRadius, blurRadius, 3);
            blurredMultiplied = boxBlur.filter(blurredMultiplied, blurredMultiplied);
        }

        Graphics2D g = dest.createGraphics();
        g.setComposite(new MultiplyComposite(opacity));
        g.drawImage(blurredMultiplied, 0, 0, null);
        g.dispose();

        return dest;
    }
}
