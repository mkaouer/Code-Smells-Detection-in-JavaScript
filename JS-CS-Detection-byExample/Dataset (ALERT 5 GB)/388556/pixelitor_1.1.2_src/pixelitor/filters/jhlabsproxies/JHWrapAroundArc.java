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

import com.jhlabs.image.CircleFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Wrap Around Arc based on the JHLabs CircleFilter
 */
public class JHWrapAroundArc extends FilterWithParametrizedGUI {
    private CircleFilter filter;

    private RangeParam radius = new RangeParam("Radius", 0, 500, 50);
    private RangeParam thickness = new RangeParam("Thickness", 0, 500, 150);
    private AngleParam rotateResult = new AngleParam("Rotate Result", 0);
    private RangeParam spread = new RangeParam("Spread", 0, 360, 180);

    private ImagePositionParam center = new ImagePositionParam("Center");
    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    public JHWrapAroundArc() {
        super("Wrap Around Arc");
        paramSet = new ParamSet(
                center,
                radius,
                thickness,
                rotateResult,
                spread,
                edgeAction,
                interpolation
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new CircleFilter();
        }

        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());
        filter.setRadius(radius.getValue());
        filter.setHeight(thickness.getValue());
        filter.setAngle((float) rotateResult.getValueInIntuitiveRadians());
        filter.setSpreadAngle(spread.getValueInRadians());

        filter.setInterpolation(interpolation.getValue());
        filter.setEdgeAction(edgeAction.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}