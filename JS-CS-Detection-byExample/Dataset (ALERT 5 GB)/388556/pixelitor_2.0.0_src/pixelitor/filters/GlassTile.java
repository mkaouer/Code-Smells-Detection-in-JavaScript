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
package pixelitor.filters;

import pixelitor.filters.gui.CoupledRangeParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.impl.TileFilter;

import java.awt.image.BufferedImage;

/**
 * Glass Tile filter
 */
public class GlassTile extends FilterWithParametrizedGUI {
    private final CoupledRangeParam size = new CoupledRangeParam("Tile Size", 5, 500, 100);
    private final CoupledRangeParam curvature = new CoupledRangeParam("Curvature", 0, 20, 10);
    private final IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private final IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private TileFilter filter;

    public GlassTile() {
        super("Glass Tile", true, false);
        setParamSet(new ParamSet(
                size,
                curvature,
                edgeAction,
                interpolation
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new TileFilter();
        }

        filter.setSizeX(size.getFirstValue());
        filter.setSizeY(size.getSecondValue());
        filter.setCurvatureX(curvature.getFirstValue());
        filter.setCurvatureY(curvature.getSecondValue());
        filter.setEdgeAction(edgeAction.getValue());
        filter.setInterpolation(interpolation.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}

