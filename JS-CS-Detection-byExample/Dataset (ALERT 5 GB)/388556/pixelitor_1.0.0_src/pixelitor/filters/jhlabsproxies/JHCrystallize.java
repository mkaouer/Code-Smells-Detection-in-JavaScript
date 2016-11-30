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

import com.jhlabs.image.CrystallizeFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ColorParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Crystallize based on the JHLabs CrystallizeFilter
 */
public class JHCrystallize extends FilterWithParametrizedGUI {
    private RangeParam edgeThickness = new RangeParam("Edge Thickness", 0, 100, 40);
    private RangeParam size = new RangeParam("Size", 1, 200, 10);
    private ColorParam edgeColor = new ColorParam("Edge Color", Color.BLACK, true, true);
    private BooleanParam fadeEdges = new BooleanParam("Fade Edges", false);

    private IntChoiceParam gridType = IntChoiceParam.getGridTypeChoices("Shape");
    private RangeParam randomness = new RangeParam("Shape Randomness (%)", 1, 100, 1);

    private CrystallizeFilter filter;

    public JHCrystallize() {
        super("Crystallize", true);
        paramSet = new ParamSet(
                edgeThickness,
                size,
                gridType,
                randomness,
                edgeColor,
                fadeEdges
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new CrystallizeFilter();
        }

        filter.setEdgeThickness(edgeThickness.getValueAsPercentage());
        filter.setScale(size.getValue());
        filter.setRandomness(randomness.getValueAsPercentage());
        filter.setEdgeColor(edgeColor.getColor().getRGB());
        filter.setGridType(gridType.getValue());
        filter.setFadeEdges(fadeEdges.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}