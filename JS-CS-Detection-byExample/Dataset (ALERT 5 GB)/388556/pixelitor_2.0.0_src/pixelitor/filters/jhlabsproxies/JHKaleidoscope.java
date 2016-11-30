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

import com.jhlabs.image.KaleidoscopeFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.*;

import java.awt.image.BufferedImage;

/**
 * A kaleidoscope based on the JHLabs KaleidoscopeFilter
 */
public class JHKaleidoscope extends FilterWithParametrizedGUI {
    private final AngleParam angle = new AngleParam("Angle", 0);
    private final AngleParam rotateResult = new AngleParam("Rotate Result", 0);
    private final ImagePositionParam center = new ImagePositionParam("Center");
    private final RangeParam sides = new RangeParam("Sides", 0, 10, 3);
//    private final RangeParam radius = new RangeParam("Radius", 0, 999, 0);

    private RangeParam divideParam = new RangeParam("Divide (%)", 50, 500, 100);
    private final IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private final IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private KaleidoscopeFilter filter;

    public JHKaleidoscope() {
        super("Kaleidoscope", true, false);
        setParamSet(new ParamSet(
                center,
                angle,
                sides,
//                radius,
                divideParam,
                rotateResult,
                edgeAction,
                interpolation
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new KaleidoscopeFilter();
        }
        filter.setAngle((float) angle.getValueInRadians());
        filter.setAngle2((float) rotateResult.getValueInRadians());
        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());
//        filter.setRadius(radius.getValue());
        filter.setSides(sides.getValue());
        filter.setEdgeAction(edgeAction.getValue());
        filter.setInterpolation(interpolation.getValue());
        filter.setDivideFactor(divideParam.getValueAsPercentage());

        dest = filter.filter(src, dest);
        return dest;
    }
}
