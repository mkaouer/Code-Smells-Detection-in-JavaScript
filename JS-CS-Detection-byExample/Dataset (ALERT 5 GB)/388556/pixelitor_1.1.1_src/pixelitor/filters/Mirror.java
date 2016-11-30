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

import com.jhlabs.image.TransformFilter;
import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.impl.MirrorFilter;

import java.awt.image.BufferedImage;

/**
 * Mirror filter
 */
public class Mirror extends FilterWithParametrizedGUI {
    private AngleParam angle = new AngleParam("Angle", 0);
    private ImagePositionParam center = new ImagePositionParam("Center");
//    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
//    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private IntChoiceParam typeParam = new IntChoiceParam("Type", new IntChoiceParam.Value[] {
            new IntChoiceParam.Value("Left Over Right", MirrorFilter.LEFT_OVER_RIGHT),
            new IntChoiceParam.Value("Right Over Left", MirrorFilter.RIGHT_OVER_LEFT),
            new IntChoiceParam.Value("Bottom Over Top", MirrorFilter.BOTTOM_OVER_TOP),
            new IntChoiceParam.Value("Top Over Bottom", MirrorFilter.TOP_OVER_BOTTOM),
//            new IntChoiceParam.Value("Central Symmetry", MirrorFilter.CENTRAL_SYMMETRY),
    });

    private MirrorFilter filter;

    public Mirror() {
        super("Mirror");
        paramSet = new ParamSet(
                typeParam,
                center
//                angle,
//                edgeAction,
//                interpolation
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new MirrorFilter();
        }

        filter.setType(typeParam.getValue());
        filter.setCenterX(center.getRelativeX());
        filter.setCenterY(center.getRelativeY());

//        filter.setEdgeAction(edgeAction.getValue());
//        filter.setInterpolation(interpolation.getValue());

        filter.setEdgeAction(TransformFilter.ZERO);
        filter.setInterpolation(TransformFilter.NEAREST_NEIGHBOUR);
        

        dest = filter.filter(src, dest);
        return dest;
    }
}

