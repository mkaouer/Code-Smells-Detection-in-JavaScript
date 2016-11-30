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

import com.jhlabs.image.SphereFilter;
import pixelitor.ImageChangeReason;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.Operations;
import pixelitor.operations.gui.ImagePositionParam;
import pixelitor.operations.gui.IntChoiceParam;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.ParamSet;

import java.awt.image.BufferedImage;

/**
 * Sphere based on the JHLabs SphereFilter
 */
public class JHSphere extends OperationWithParametrizedGUI {
    private RangeParam radius = new RangeParam("Radius", 0, 500, 0);
    private RangeParam refractionIndex = new RangeParam("Refraction Index (%)", 100, 300, 100);
    private ImagePositionParam center = new ImagePositionParam("Center");
    //    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private SphereFilter filter;

    public JHSphere() {
        super("Sphere", false);
        paramSet = new ParamSet(
                center,
                radius,
                refractionIndex,
//                edgeAction,
                interpolation
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        float refraction = refractionIndex.getValueAsPercentage();
        if(refraction == 1.0f) {
            return Operations.getDefaultBufferedImage(src);
        }

        if (filter == null) {
            filter = new SphereFilter();
        }

        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());

        filter.setRadius(radius.getValue());
        filter.setRefractionIndex(refraction);

//        filter.setEdgeAction(edgeAction.getCurrentInt());
        filter.setInterpolation(interpolation.getCurrentInt());

        dest = filter.filter(src, dest);
        return dest;
    }
}