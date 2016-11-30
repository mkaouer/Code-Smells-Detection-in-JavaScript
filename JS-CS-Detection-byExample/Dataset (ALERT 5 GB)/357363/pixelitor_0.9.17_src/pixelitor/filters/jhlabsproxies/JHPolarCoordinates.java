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

import com.jhlabs.image.PolarFilter;
import pixelitor.filters.OperationWithParametrizedGUI;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;

import java.awt.image.BufferedImage;

/**
 * Polar Coordinates based on the JHLabs PolarFilter
 */
public class JHPolarCoordinates extends OperationWithParametrizedGUI {
    private ImagePositionParam center = new ImagePositionParam("Center");

    private static IntChoiceParam.Value[] gridTypeChoices = new IntChoiceParam.Value[]{
            new IntChoiceParam.Value("Rectangular to Polar ", PolarFilter.RECT_TO_POLAR),
            new IntChoiceParam.Value("Polar to Rectangular", PolarFilter.POLAR_TO_RECT),
            new IntChoiceParam.Value("Invert in Circle", PolarFilter.INVERT_IN_CIRCLE),
    };
    private IntChoiceParam type = new IntChoiceParam("Type", gridTypeChoices);
    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private PolarFilter filter;

    public JHPolarCoordinates() {
        super("Polar Coordinates", true);
        paramSet = new ParamSet(center, type, edgeAction, interpolation);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new PolarFilter();
        }
        filter.setType(type.getValue());
        filter.setEdgeAction(edgeAction.getValue());
        filter.setRelativeCentreX(center.getRelativeX());
        filter.setRelativeCentreY(center.getRelativeY());
        filter.setInterpolation(interpolation.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}