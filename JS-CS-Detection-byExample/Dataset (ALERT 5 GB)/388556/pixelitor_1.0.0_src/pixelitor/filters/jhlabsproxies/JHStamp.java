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

import com.jhlabs.image.StampFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.ColorParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Stamp based on the JHLabs StampFilter
 */
public class JHStamp extends FilterWithParametrizedGUI {
    private RangeParam lightDarkBalance = new RangeParam("Light/Dark Balance (%)", 0, 100, 50);
    private RangeParam smoothness = new RangeParam("Smoothness", 0, 50, 25);
    private RangeParam soften = new RangeParam("Soften", 0, 100, 3);
    private ColorParam darkColor = new ColorParam("Dark Color", Color.BLACK, true, false);
    private ColorParam brightColor = new ColorParam("Bright Color", Color.WHITE, true, false);

    private StampFilter filter;

    public JHStamp() {
        super("Stamp", true);
        paramSet = new ParamSet(
                lightDarkBalance,
                smoothness,
                soften,
                brightColor,
                darkColor
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new StampFilter();
        }

        filter.setBlack(darkColor.getColor().getRGB());
        filter.setWhite(brightColor.getColor().getRGB());
        filter.setRadius(smoothness.getValue());
        filter.setSoftness(soften.getValueAsPercentage());
        filter.setThreshold(lightDarkBalance.getValueAsPercentage());

        dest = filter.filter(src, dest);
        return dest;
    }
}