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

import com.jhlabs.image.QuantizeFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Quantize based on the JHLabs QuantizeFilter
 */
public class JHQuantize extends FilterWithParametrizedGUI {
    private RangeParam numberOfColors = new RangeParam("Number of Colors", 2, 256, 2);
    private BooleanParam dither = new BooleanParam("Dither", false);
    private BooleanParam serpentine = new BooleanParam("Serpentine", false);

    private QuantizeFilter filter;

    public JHQuantize() {
        super("Quantize", true);
        paramSet = new ParamSet(
                numberOfColors,
                dither,
                serpentine
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new QuantizeFilter();
        }

        filter.setNumColors(numberOfColors.getValue());
        filter.setDither(dither.getValue());
        filter.setSerpentine(serpentine.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}