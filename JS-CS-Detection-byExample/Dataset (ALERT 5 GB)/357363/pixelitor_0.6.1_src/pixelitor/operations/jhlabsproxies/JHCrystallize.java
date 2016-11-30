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

import com.jhlabs.image.CellularFilter;
import com.jhlabs.image.CrystallizeFilter;
import pixelitor.ImageChangeReason;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.gui.BooleanParam;
import pixelitor.operations.gui.ColorParam;
import pixelitor.operations.gui.IntChoiceParam;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.ParamSet;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Crystallize based on the JHLabs CrystallizeFilter
 */
public class JHCrystallize extends OperationWithParametrizedGUI {
    private RangeParam edgeThickness = new RangeParam("Edge Thickness", 0, 100, 40);
    private RangeParam size = new RangeParam("Size", 1, 100, 10);
    private RangeParam randomness = new RangeParam("Randomness", 1, 100, 1);
    private ColorParam edgeColor = new ColorParam("Edge Color", Color.BLACK);
    private BooleanParam fadeEdges = new BooleanParam("Fade Edges", false);

    private static IntChoiceParam.Value[] gridTypeChoices = new IntChoiceParam.Value[]{
            new IntChoiceParam.Value("Random", CellularFilter.RANDOM),
            new IntChoiceParam.Value("Squares", CellularFilter.SQUARE),
            new IntChoiceParam.Value("Hexagons", CellularFilter.HEXAGONAL),
            new IntChoiceParam.Value("Octagons & Squares", CellularFilter.OCTAGONAL),
            new IntChoiceParam.Value("Triangles", CellularFilter.TRIANGULAR),
    };
    private IntChoiceParam gridType = new IntChoiceParam("Shape", gridTypeChoices);

    private CrystallizeFilter filter;

    public JHCrystallize() {
        super("Crystallize", true);
        paramSet = new ParamSet(
                edgeThickness,
                size,
                randomness,
                gridType,
                edgeColor,
                fadeEdges
        );
//        paramSet.addRandomizeAction();
//        paramSet.addResetAllAction();
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
//        Thread.dumpStack();

        if (filter == null) {
            filter = new CrystallizeFilter();
        }

        filter.setEdgeThickness(edgeThickness.getValueAsPercentage());
        filter.setScale(size.getValue());
        filter.setRandomness(randomness.getValueAsPercentage());
        filter.setEdgeColor(edgeColor.getColor().getRGB());
        filter.setGridType(gridType.getCurrentInt());
        filter.setFadeEdges(fadeEdges.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}