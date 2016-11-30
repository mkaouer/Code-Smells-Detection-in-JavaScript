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

import com.jhlabs.image.CellularFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Cells based on the JHLabs CellularFilter
 */
public class JHCells extends FilterWithParametrizedGUI {
    private static final int TYPE_CELLS = 1;
    private static final int TYPE_GRID = 2;
    private static final int TYPE_STRANGE = 3;


    private GradientParam gradientParam = new GradientParam("Colors", Color.BLACK, Color.WHITE);

    private RangeParam scaleParam = new RangeParam("Zoom (%)", 1, 500, 100);
    private RangeParam stretchParam = new RangeParam("Stretch (%)", 100, 999, 100);
//    private RangeParam f1Param = new RangeParam("F1", -100, 100, -100);
//    private RangeParam f2Param = new RangeParam("F2", -100, 100, -100);
//    private RangeParam f3Param = new RangeParam("F3", -100, 100, 0);

    private RangeParam randomness = new RangeParam("Grid Randomness", 1, 100, 1);
    private IntChoiceParam gridType = IntChoiceParam.getGridTypeChoices("Grid Type", randomness);

    private IntChoiceParam typeParam = new IntChoiceParam("Type", new IntChoiceParam.Value[]{
//            new IntChoiceParam.Value("Free", 0),
            new IntChoiceParam.Value("Cells", TYPE_CELLS),
            new IntChoiceParam.Value("Grid", TYPE_GRID),
            new IntChoiceParam.Value("Grid 2", TYPE_STRANGE),
    });
    private RangeParam tuneParam = new RangeParam("Type", 0, 100, 0);
    private RangeParam bwParam = new RangeParam("Dark/Light Balance", -20, 20, 0);

    private AngleParam angleParam = new AngleParam("Angle", 0);


    private CellularFilter filter;

    public JHCells() {
        super("Cells", false, false);
        setParamSet(new ParamSet(
                typeParam,
                tuneParam,
                gridType,
                randomness,
                gradientParam,
                bwParam,
                scaleParam,
                stretchParam,
                angleParam
//                f1Param,
//                f2Param,
//                f3Param
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new CellularFilter();
        }

        int scale = scaleParam.getValue();
        float stretch = stretchParam.getValueAsPercentage();
        float angle = (float) (angleParam.getValueInRadians() + (Math.PI / 2));

        int type = typeParam.getValue();
        float tune = tuneParam.getValueAsPercentage();

        float f1, f2, f3;

        switch (type) {
//            case 0:
//                f1 = f1Param.getValueAsPercentage();
//                f2 = f2Param.getValueAsPercentage();
//                f3 = f3Param.getValueAsPercentage();
            case TYPE_CELLS:
                f1 = 1.0f - tune;
                f2 = tune;
                f3 = -tune / 3;
                break;
            case TYPE_GRID:
                f1 = -1.0f + tune;
                f2 = 1.0f;
                f3 = -tune / 2;
                break;
            case TYPE_STRANGE:
                f1 = -0.5f + tune;
                f2 = 0.5f - tune;
                f3 = 0.15f + tune / 2;
                break;
            default:
                throw new IllegalStateException();
        }

        float bw = bwParam.getValueAsPercentage();
        f1 += bw;
        f2 += bw;
        f3 += bw;

        filter.setScale(scale);
        filter.setStretch(stretch);
        filter.setAngle(angle);

        filter.setF1(f1);
        filter.setF2(f2);
        filter.setF3(f3);
        filter.setGridType(gridType.getValue());
        filter.setRandomness(randomness.getValueAsPercentage());

        filter.setColormap(gradientParam.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}