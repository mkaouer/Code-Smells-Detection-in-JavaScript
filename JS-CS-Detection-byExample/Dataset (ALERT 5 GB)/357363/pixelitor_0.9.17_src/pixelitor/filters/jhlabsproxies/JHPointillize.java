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

import com.jhlabs.image.PointillizeFilter;
import pixelitor.filters.OperationWithParametrizedGUI;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ColorParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Pointillize based on the JHLabs PointillizeFilter
 */
public class JHPointillize extends OperationWithParametrizedGUI {
    private RangeParam size = new RangeParam("Grid Scale", 1, 100, 15);
    //    private RangeParam angle = new RangeParam("Angle", 0, 360, 0);
    private RangeParam randomness = new RangeParam("Randomness", 0, 100, 0);
    private RangeParam dotSize = new RangeParam("Dot Size", 0, 100, 45);
    private RangeParam fuzziness = new RangeParam("Fuzziness", 0, 100, 0);
    private ColorParam edgeColor = new ColorParam("Fill Color", Color.BLACK, true);
    private BooleanParam fadeEdges = new BooleanParam("Fade Instead of Fill", false);

    private IntChoiceParam gridType = IntChoiceParam.getGridTypeChoices("Grid Type");


    private PointillizeFilter filter;

    public JHPointillize() {
        super("Pointillize", true);
        paramSet = new ParamSet(
                size,
//                angle,
                randomness,
                dotSize,
                fuzziness,
                edgeColor,
                fadeEdges,
                gridType
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new PointillizeFilter();
        }

//        filter.setAngle(angle.getValueInRadians());
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