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

import pixelitor.filters.gui.ActionParam;
import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.BlendingModeParam;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ColorParam;
import pixelitor.filters.gui.ElevationAngleParam;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.gui.RangeWithColorsParam;
import pixelitor.filters.gui.TextParam;
import pixelitor.layers.BlendingMode;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * ParamTest
 */
public class ParamTest extends OperationWithParametrizedGUI {
    private RangeParam rangeParam = new RangeParam("RangeParam", 0, 100, 50);
    private RangeWithColorsParam rangeWithColorsParam = new RangeWithColorsParam(Color.RED, Color.BLUE, "RangeWithColorsParam", 0, 100, 50);
    private ImagePositionParam centerParam = new ImagePositionParam("ImagePositionParam");
    private IntChoiceParam edgeActionParam = new IntChoiceParam("IntChoiceParam", new IntChoiceParam.Value[] {
            new IntChoiceParam.Value("value 1", 1),
            new IntChoiceParam.Value("value 2", 2),
    });
    private ColorParam colorParam = new ColorParam("ColorParam:", Color.WHITE, false);
    private ActionParam actionParam = new ActionParam("ActionParam", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    });
    private AngleParam angleParam = new AngleParam("AngleParam", 0);
    private ElevationAngleParam elevationAngleParam = new ElevationAngleParam("ElevationAngleParam", 0);
    private BlendingModeParam blendingModeParam = new BlendingModeParam(BlendingMode.values());
    private BooleanParam booleanParam = new BooleanParam("BooleanParam", false);
    private TextParam textParam = new TextParam("TextParam", "default value");

    public ParamTest() {
        super("ParamTest", true);
        paramSet = new ParamSet(
                rangeParam,
                rangeWithColorsParam,
                centerParam,
                edgeActionParam,
                colorParam,
                actionParam,
                angleParam,
                elevationAngleParam,
                blendingModeParam,
                booleanParam,
                textParam
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        System.out.println("ParamTest.transform CALLED");

        dest = ImageUtils.copyImage(src);
        return dest;
    }
}