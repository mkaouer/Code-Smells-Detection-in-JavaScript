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

import com.jhlabs.image.GlintFilter;
import com.jhlabs.image.LinearColormap;
import pixelitor.filters.OperationWithParametrizedGUI;
import pixelitor.filters.gui.ColorParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Glint based on the JHLabs GlintFilter
 */
public class JHGlint extends OperationWithParametrizedGUI {
    private RangeParam threshold = new RangeParam("Threshold (%)", 0, 100, 70);
    private RangeParam coverage = new RangeParam("Coverage (%)", 0, 100, 50);
    private RangeParam amount = new RangeParam("Amount (%)", 0, 100, 15);
    private RangeParam length = new RangeParam("Length", 0, 100, 20);
    private RangeParam blur = new RangeParam("Blur", 0, 20, 1);
    private ColorParam color1 = new ColorParam("Inner Color", Color.WHITE, true);
    private ColorParam color2 = new ColorParam("Outer Color", Color.WHITE, true);
//    private BooleanParam glintOnly = new BooleanParam("Glint Only", false);

    private GlintFilter filter;

    public JHGlint() {
        super("Glint", true);
        paramSet = new ParamSet(
                threshold,
                coverage,
                amount,
                length,
                blur,
                color1,
                color2
//                glintOnly
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new GlintFilter();
        }

        filter.setThreshold(threshold.getValueAsPercentage());
        filter.setCoverage(coverage.getValueAsPercentage());
        filter.setAmount(amount.getValueAsPercentage());
        filter.setLength(length.getValue());
        filter.setBlur(blur.getValue());
        filter.setColormap(new LinearColormap(
                color1.getColor().getRGB(),
                color2.getColor().getRGB()
        ));
//        filter.setGlintOnly(glintOnly.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}