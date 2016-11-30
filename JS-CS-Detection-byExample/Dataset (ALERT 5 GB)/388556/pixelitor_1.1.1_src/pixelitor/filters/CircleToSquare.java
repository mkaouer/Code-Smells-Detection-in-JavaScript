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
package pixelitor.filters;

import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.impl.CircleToSquareFilter;

import java.awt.image.BufferedImage;

/**
 * Circle To Square
 */
public class CircleToSquare extends FilterWithSOParametrizedGUI {
    private RangeParam radius = new RangeParam("Radius", 10, 999, 100);
    private RangeParam amount = new RangeParam("Amount (%)", -200, 200, 100);


    private ImagePositionParam center = new ImagePositionParam("Center");

    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private CircleToSquareFilter filter;

    public CircleToSquare() {
        super("Circle to Square");
        paramSet = new ParamSet(
                center,
                radius,
                amount,
                edgeAction,
                interpolation,
                showOriginalParam
        );
    }

    @Override
    public BufferedImage realTransform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new CircleToSquareFilter();
        }

        filter.setCenterX(center.getRelativeX());
        filter.setCenterY(center.getRelativeY());

        filter.setRadius(radius.getValue());
        filter.setAmount(amount.getValueAsPercentage());

        filter.setEdgeAction(edgeAction.getValue());
        filter.setInterpolation(interpolation.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}

