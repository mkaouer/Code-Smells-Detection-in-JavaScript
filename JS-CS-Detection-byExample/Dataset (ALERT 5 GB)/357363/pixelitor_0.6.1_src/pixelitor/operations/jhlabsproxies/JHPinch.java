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
package pixelitor.operations.jhlabsproxies;

import com.jhlabs.image.PinchFilter;
import pixelitor.ImageChangeReason;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.gui.ImagePositionParam;
import pixelitor.operations.gui.IntChoiceParam;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.ParamSet;

import java.awt.image.BufferedImage;

/**
 * JHPinch
 */
public class JHPinch extends OperationWithParametrizedGUI {
    private ImagePositionParam center = new ImagePositionParam("Center");
    private RangeParam radius = new RangeParam("Radius", 1, 500, 100);
    private RangeParam twirlAngle = new RangeParam("Twirl Angle", -360, 360, 0);
    private RangeParam pinchBulgeAmount = new RangeParam("Pinch-Bulge Amount", -100, 100, 0);

    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private PinchFilter filter;

    public JHPinch() {
        super("Pinch, Bulge, Twirl", true);
        paramSet = new ParamSet(
                center,
                radius,
                pinchBulgeAmount,
                twirlAngle,
                edgeAction,
                interpolation
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        if (filter == null) {
            filter = new PinchFilter();
        }

        filter.setRadius(radius.getValue());
        filter.setAngle(2 * twirlAngle.getValueInRadians());
        filter.setAmount((-1) * pinchBulgeAmount.getValueAsPercentage());
        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());
        filter.setEdgeAction(edgeAction.getCurrentInt());

        filter.setInterpolation(interpolation.getCurrentInt());

        dest = filter.filter(src, dest);
        return dest;
    }
}