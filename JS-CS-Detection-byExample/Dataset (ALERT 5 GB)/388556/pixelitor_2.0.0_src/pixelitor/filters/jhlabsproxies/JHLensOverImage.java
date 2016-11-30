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

import com.jhlabs.image.SphereFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.CoupledRangeParam;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Fisheye based on the JHLabs SphereFilter
 */
public class JHLensOverImage extends FilterWithParametrizedGUI {
    private final CoupledRangeParam radius = new CoupledRangeParam("Radius", 0, 999, 200);

    // less than 100% doesn't create anything usable
    private final RangeParam refractionIndex = new RangeParam("Refraction Index (%)", 100, 300, 150);

    private final ImagePositionParam center = new ImagePositionParam("Center");
    //    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private final IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private SphereFilter filter;

    public JHLensOverImage() {
        super("Lens Over Image", true, true);
        setParamSet(new ParamSet(
                radius.adjustMaxAccordingToImage(),
                refractionIndex,
                center,
//                edgeAction,  // edge action doesn't create anything usable in this case
                interpolation
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        float refraction = refractionIndex.getValueAsPercentage();
        int hRadius = radius.getFirstValue();
        int vRadius = radius.getSecondValue();

        if (refraction == 1.0f || hRadius == 0 || vRadius == 0) {
            return src;
        }

        if (filter == null) {
            filter = new SphereFilter();
        }

        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());

//        filter.setRadius(radius.getValue());

        filter.setA(hRadius);
        filter.setB(vRadius);

        filter.setRefractionIndex(refraction);

//        filter.setEdgeAction(edgeAction.getCurrentInt());
        filter.setInterpolation(interpolation.getValue());

        dest = filter.filter(src, dest);

        super.setAffectedAreaShapes(filter.getAffectedAreaShapes());

        return dest;
    }
}