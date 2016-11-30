/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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

import com.jhlabs.image.KaleidoscopeFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * A kaleidoscope based on the JHLabs KaleidoscopeFilter
 */
public class JHKaleidoscope extends FilterWithParametrizedGUI {
    private AngleParam angle = new AngleParam("Angle", 0);
    private AngleParam rotateResult = new AngleParam("Rotate Result", 0);
    private ImagePositionParam center = new ImagePositionParam("Center");
    private RangeParam sides = new RangeParam("Sides", 0, 10, 3);
    private RangeParam radius = new RangeParam("Radius", 0, 999, 0);
    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private KaleidoscopeFilter filter;

    public JHKaleidoscope() {
        super("Kaleidoscope", true);
        paramSet = new ParamSet(
                sides,
                angle,
                rotateResult,
                center,
                radius,
                edgeAction,
                interpolation
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new KaleidoscopeFilter();
        }
        filter.setAngle((float) angle.getValueInRadians());
        filter.setAngle2((float) rotateResult.getValueInRadians());
        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());
        filter.setRadius(radius.getValue());
        filter.setSides(sides.getValue());
        filter.setEdgeAction(edgeAction.getValue());
        filter.setInterpolation(interpolation.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}
