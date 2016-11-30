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

import com.jhlabs.image.PinchFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * JHPinch
 */
public class JHPinch extends FilterWithParametrizedGUI {
    private final ImagePositionParam center = new ImagePositionParam("Center");
    private final RangeParam radius = new RangeParam("Radius", 1, 999, 200);
    private final RangeParam twirlAngle = new RangeParam("Twirl Angle", -360, 360, 0);
    private final RangeParam pinchBulgeAmount = new RangeParam("Pinch-Bulge Amount", -100, 100, 0);

    private final IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private final IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private PinchFilter filter;

    public JHPinch() {
        super("Pinch, Bulge, Twirl", true, true);
        setParamSet(new ParamSet(
                pinchBulgeAmount,
                twirlAngle,
                radius.adjustMaxAccordingToImage(),
                center,
                edgeAction,
                interpolation
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new PinchFilter();
        }

        filter.setRadius(radius.getValue());
        filter.setAngle(2 * twirlAngle.getValueInRadians());
        filter.setAmount((-1) * pinchBulgeAmount.getValueAsPercentage());
        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());
        filter.setEdgeAction(edgeAction.getValue());

        filter.setInterpolation(interpolation.getValue());

        dest = filter.filter(src, dest);
        setAffectedAreaShapes(filter.getAffectedAreaShapes());

        return dest;
    }
}