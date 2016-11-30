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
package pixelitor.filters.jhlabsproxies;

import com.jhlabs.image.PointillizeFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Pointillize based on the JHLabs PointillizeFilter
 */
public class JHPointillize extends FilterWithParametrizedGUI {
    private final RangeParam size = new RangeParam("Grid Size", 1, 200, 15);
    private final RangeParam dotSize = new RangeParam("Dot Relative Size", 0, 100, 45);
    private final RangeParam fuzziness = new RangeParam("Fuzziness (%)", 0, 100, 0);
    private final ColorParam edgeColor = new ColorParam("Fill Color", Color.BLACK, true, true);
    private final BooleanParam fadeEdges = new BooleanParam("Fade Instead of Fill", false);

    private final RangeParam randomness = new RangeParam("Grid Randomness (%)", 0, 100, 0);
    private final IntChoiceParam gridType = IntChoiceParam.getGridTypeChoices("Grid Type", randomness);


    private PointillizeFilter filter;

    public JHPointillize() {
        super("Pointillize", true, false);
        setParamSet(new ParamSet(
                size,
                dotSize,
                fuzziness,
                gridType,
                randomness,
                edgeColor,
                fadeEdges
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new PointillizeFilter();
        }

        // there is an angle property but it does not work as expected
        filter.setScale(size.getValue());
        filter.setRandomness(randomness.getValueAsPercentage());
        filter.setEdgeThickness(dotSize.getValueAsPercentage());
        filter.setFuzziness(fuzziness.getValueAsPercentage());
        filter.setGridType(gridType.getValue());
        filter.setFadeEdges(fadeEdges.getValue());
        filter.setEdgeColor(edgeColor.getColor().getRGB());

        dest = filter.filter(src, dest);
        return dest;
    }
}