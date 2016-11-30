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
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.Operations;
import pixelitor.operations.gui.ImagePositionParam;
import pixelitor.operations.gui.IntChoiceParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Fisheye based on the JHLabs SphereFilter
 */
public class JHFishEye extends OperationWithParametrizedGUI {
    private RangeParam radius = new RangeParam("Radius", 0, 500, 100);

    // less than 100 doesn't create anything usable
    private RangeParam refractionIndex = new RangeParam("Refraction Index (%)", 100, 300, 100);

    private ImagePositionParam center = new ImagePositionParam("Center");
//    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private SphereFilter filter;

    public JHFishEye() {
        super("Fisheye", false);
        paramSet = new ParamSet(
                refractionIndex,
                radius,
                center,
//                edgeAction,  // edge action doesn't create anything usable in this case
                interpolation
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        float refraction = refractionIndex.getValueAsPercentage();
        if (refraction == 1.0f) {
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